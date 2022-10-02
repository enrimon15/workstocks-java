package it.workstocks.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.user.UserDto;
import it.workstocks.entity.user.User;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.companyowner.CompanyOwner;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.user.UserRepository;
import it.workstocks.service.ProfileService;

@Service
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityMapper mapper;

	@Override
	public UserDto loadFullUser(String email) {
		return prepareUser(userRepository.findByEmail(email));
	}

	@Override
	public UserDto loadFullUserById(Long id,Class<?> clazz) throws WorkstocksBusinessException {
		UserDto dto = prepareUser(userRepository.findById(id));
		if (dto != null && dto.getClass().equals(clazz)) {
			return dto;

		} else {
			String error = String.format("User of type %s does not exists for the id %d", clazz.getSimpleName(),id);
			throw new WorkstocksBusinessException(error);
		}
	}

	private UserDto prepareUser(Optional<User> optUser) {
		UserDto dto = null;

		if (optUser.isPresent()) {
			User u = optUser.get();

			if (u instanceof Applicant) {
				dto = mapper.toDto((Applicant) u);
			} else if (u instanceof CompanyOwner) {
				CompanyOwner owner = (CompanyOwner) u;
				dto = mapper.toDto(owner);
			}

		}

		return dto;
	}
}
