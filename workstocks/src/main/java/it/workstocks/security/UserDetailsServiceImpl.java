package it.workstocks.security;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import it.workstocks.entity.user.User;
import it.workstocks.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user;
		try {
			user = repository.findByEmail(email).get();
			if (user != null) {
				return new UserDetailsImpl(user);
			} else {
				throw new UsernameNotFoundException("User not found");
			}

		} catch (NoSuchElementException e) {
			log.error("Load user detail error: " + e);
			throw new UsernameNotFoundException("User not found");
		}
	}


}
