package it.workstocks.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.registration.Registration;
import it.workstocks.dto.registration.UserType;
import it.workstocks.dto.user.PasswordDto;
import it.workstocks.dto.user.UserDto;
import it.workstocks.entity.Address;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.company.WorkingPlace;
import it.workstocks.entity.user.PasswordResetToken;
import it.workstocks.entity.user.User;
import it.workstocks.entity.user.admin.Admin;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.companyowner.CompanyOwner;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.user.AdminRepository;
import it.workstocks.repository.user.ApplicantRepository;
import it.workstocks.repository.user.CompanyOwnerRepository;
import it.workstocks.repository.user.PasswordTokenRepository;
import it.workstocks.repository.user.UserRepository;
import it.workstocks.service.UserService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.TokenValid;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private CompanyOwnerRepository companyOwnerRepository;

	@Autowired
	private PasswordTokenRepository passwordTokenRepository;

	@Autowired
	private PasswordEncoder pwdEncoder;

	@Autowired
	private EntityMapper entityMapper;
	
	@Autowired
	private WorkstocksProperties props;

	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public void save(Registration registration) throws WorkstocksBusinessException {

		if (registration.getUserType() == UserType.APPLICANT) {
			Applicant app = new Applicant();
			app.setEmail(registration.getEmail());
			app.setName(registration.getName());
			app.setSurname(registration.getSurname());
			app.setPassword(pwdEncoder.encode(registration.getPassword()));

			applicantRepository.save(app);
		} else if (registration.getUserType() == UserType.COMPANY_OWNER) {
			Address adr = entityMapper.addressFromRegistration(registration);

			WorkingPlace wp = new WorkingPlace();
			wp.setAddress(adr);
			wp.setWorkingPlaceType(registration.getWorkingPlaceType());
			wp.setMainWorkingPlace(true);
			
			Company company = new Company();
			company.setName(registration.getCompanyName());
			company.setCompanyForm(registration.getCompanyForm());
			company.setEmployeesNumber(registration.getEmployeesNumber());
			company.setFoundationYear(registration.getFoundationYear());
			company.setTelephone(registration.getContactPhone());
			company.setVatNumber(registration.getVatNumber());
			company.setWebsite(registration.getWebsite());
			company.setWorkingPlaces(new LinkedHashSet<WorkingPlace>(Arrays.asList(wp)));
			
			wp.setCompany(company);

			CompanyOwner owner = new CompanyOwner();
			owner.setEmail(registration.getEmail());
			owner.setName(registration.getName());
			owner.setSurname(registration.getSurname());
			owner.setPassword(pwdEncoder.encode(registration.getPassword()));
			owner.setCompany(company);
			companyOwnerRepository.save(owner);
		} else if (registration.getUserType() == UserType.ADMIN) {
			Admin admin = new Admin();
			admin.setEmail(registration.getEmail());
			admin.setName(registration.getName());
			admin.setSurname(registration.getSurname());
			admin.setPassword(pwdEncoder.encode(registration.getPassword()));

			adminRepository.save(admin);
		} else {
			throw new WorkstocksBusinessException("registration error: User type is not valid");
		}
	}
	
	private User findOptionalUser(Long userId) throws WorkstocksBusinessException {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new WorkstocksBusinessException("User not found");
		}
	}

	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public void updatePassword(PasswordDto changePasswordDto, Long userId) throws WorkstocksBusinessException {
		User user = findOptionalUser(userId);
		String pwdEncoded = pwdEncoder.encode(changePasswordDto.getNewPassword());
		user.setPassword(pwdEncoded);
		user = userRepository.save(user);
		if (!AuthUtility.hasRole("ROLE_ANONYMOUS")) AuthUtility.renewAuthenticationPrincipal(user);
	}

	@Override
	public UserDto findUserById(Long userId) throws WorkstocksBusinessException {
		return entityMapper.toDto(findOptionalUser(userId));
	}

	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public boolean createPasswordResetTokenForUser(String email, String token) throws WorkstocksBusinessException {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isEmpty()) return false;
	    
	    //invalidare altri token ACTIVE dello stesso utente
	    Set<PasswordResetToken> allUserToken = passwordTokenRepository.findAllByActiveAndUserEmail(true, email);
		for (PasswordResetToken tokenDb : allUserToken) {
	    	tokenDb.setActive(false);
	    }
	    passwordTokenRepository.saveAll(allUserToken);
	    
	    PasswordResetToken userToken = new PasswordResetToken();
		userToken.setToken(token);
		userToken.setUser(user.get());
		userToken.setActive(true);
	    passwordTokenRepository.save(userToken);
	    
	    return true;	
	}
	
	private PasswordResetToken getPasswordResetToken(String token) {
		return passwordTokenRepository.findByToken(token);
	}

	@Override
	public TokenValid validatePasswordResetToken(String token) throws WorkstocksBusinessException {
		PasswordResetToken passToken = getPasswordResetToken(token);
		
		if (passToken == null || !passToken.isActive()) {
			return TokenValid.INVALID;
		} else {
			Integer validity = null;
			try {
				validity = Integer.parseInt(props.getResetPasswordValidity());
			} catch (NumberFormatException e) {
			    throw new WorkstocksBusinessException("Validità token non valida");
			}
			
			LocalDateTime dateExpiration = passToken.getCreatedAt().plusSeconds(validity);
			if (dateExpiration.isBefore(LocalDateTime.now())) return TokenValid.EXPIRED;
		}
		
		return TokenValid.VALID;
	}

	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public void updatePasswordByToken(PasswordDto resetPasswordDto) throws WorkstocksBusinessException {
		PasswordResetToken passToken = getPasswordResetToken(resetPasswordDto.getToken());
		if (passToken == null) throw new WorkstocksBusinessException("Password reset token not found");
		updatePassword(resetPasswordDto, passToken.getUser().getId());
		
		passToken.setActive(false);
		passwordTokenRepository.save(passToken);
	}

}
