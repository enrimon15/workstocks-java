package it.workstocks.service;

import it.workstocks.dto.user.UserDto;
import it.workstocks.exception.WorkstocksBusinessException;

public interface ProfileService {
	UserDto loadFullUser(String email) throws WorkstocksBusinessException;
	UserDto loadFullUserById(Long id, Class<?> clazz) throws WorkstocksBusinessException;
}
