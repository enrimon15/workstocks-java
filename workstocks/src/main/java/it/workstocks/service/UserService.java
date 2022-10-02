package it.workstocks.service;

import it.workstocks.dto.registration.Registration;
import it.workstocks.dto.user.PasswordDto;
import it.workstocks.dto.user.UserDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.utils.TokenValid;

public interface UserService {
	void save(Registration registration) throws WorkstocksBusinessException;
	void updatePassword(PasswordDto changePasswordDto, Long userId) throws WorkstocksBusinessException;
	UserDto findUserById(Long userId) throws WorkstocksBusinessException;
	boolean createPasswordResetTokenForUser(String email, String token) throws WorkstocksBusinessException;
	TokenValid validatePasswordResetToken(String token) throws WorkstocksBusinessException;
	void updatePasswordByToken(PasswordDto resetPasswordDto) throws WorkstocksBusinessException;
}
