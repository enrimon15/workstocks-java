package it.workstocks.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.auth.PasswordDto;
import it.workstocks.dto.auth.RegistrationRequest;
import it.workstocks.entity.user.PasswordResetToken;
import it.workstocks.entity.user.User;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.ApplicantRepository;
import it.workstocks.repository.PasswordTokenRepository;
import it.workstocks.repository.UserRepository;
import it.workstocks.service.AuthService;
import it.workstocks.service.EmailService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ApplicantRepository applicantRepository;

	@Autowired
	private PasswordTokenRepository passwordTokenRepository;

	@Autowired
	private PasswordEncoder pwdEncoder;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private Translator translator;
	
	@Autowired
	private WorkstocksProperties props;

	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public Long save(RegistrationRequest registration) throws WorkstocksBusinessException {
		Applicant app = new Applicant();
		app.setEmail(registration.getEmail());
		app.setName(registration.getName());
		app.setSurname(registration.getSurname());
		app.setPassword(pwdEncoder.encode(registration.getPassword()));

		Applicant applicantSaved = applicantRepository.save(app);
		return applicantSaved.getId();
	}
	
	private User findOptionalUser(Long userId) throws WorkstocksBusinessException {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			return user.get();
		} else {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.NOT_FOUND, new Object[] {"user", userId}),HttpStatus.NOT_FOUND);
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

	/*@Override
	public UserDto findUserById(Long userId) throws WorkstocksBusinessException {
		return entityMapper.toDto(findOptionalUser(userId));
	}*/

	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public void createPasswordResetTokenForUser(String email, String token) throws WorkstocksBusinessException {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isEmpty()) throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.USER_EMAIL_NOT_FOUND, new String[] {email}));
	    
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
	    
	    //send mail
	    emailService.sendResetToken(email, token);
	}
	
	private PasswordResetToken getPasswordResetToken(String token) throws WorkstocksBusinessException {
		PasswordResetToken tokenDB = passwordTokenRepository.findByToken(token);
		if (tokenDB == null) throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.TOKEN_NOT_FOUND, null), HttpStatus.NOT_FOUND);
		return tokenDB;
	}

	@Override
	public void validatePasswordResetToken(String token) throws WorkstocksBusinessException {
		PasswordResetToken passToken = getPasswordResetToken(token);
		
		if (passToken == null || !passToken.isActive()) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.INVALID_TOKEN, null), HttpStatus.FORBIDDEN);
		} else {
			Integer validity = null;
			try {
				validity = Integer.parseInt(props.getResetPassword().getTokenValidity());
			} catch (NumberFormatException e) {
				throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.INVALID_TOKEN, null), HttpStatus.FORBIDDEN);
			}
			
			LocalDateTime dateExpiration = passToken.getCreatedAt().plusSeconds(validity);
			if (dateExpiration.isBefore(LocalDateTime.now())) throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.TOKEN_EXPIRED, null), HttpStatus.FORBIDDEN);
		}
	}

	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public void updatePasswordByToken(PasswordDto resetPasswordDto) throws WorkstocksBusinessException {
		PasswordResetToken passToken = getPasswordResetToken(resetPasswordDto.getToken());
		if (passToken == null) throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.TOKEN_NOT_FOUND, null), HttpStatus.NOT_FOUND);
		updatePassword(resetPasswordDto, passToken.getUser().getId());
		
		passToken.setActive(false);
		passwordTokenRepository.save(passToken);
	}

}
