package it.workstocks.service;

import it.workstocks.dto.auth.PasswordDto;
import it.workstocks.dto.auth.RegistrationRequest;
import it.workstocks.exception.WorkstocksBusinessException;

public interface AuthService {
	Long save(RegistrationRequest registration) throws WorkstocksBusinessException;
	void updatePassword(PasswordDto changePasswordDto, Long userId) throws WorkstocksBusinessException;
	//UserDto findUserById(Long userId) throws WorkstocksBusinessException;
	void createPasswordResetTokenForUser(String email, String token) throws WorkstocksBusinessException;
	void validatePasswordResetToken(String token) throws WorkstocksBusinessException;
	void updatePasswordByToken(PasswordDto resetPasswordDto) throws WorkstocksBusinessException;
}
