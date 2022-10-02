package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import it.workstocks.entity.user.PasswordResetToken;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
	PasswordResetToken findByToken(String token);
	Set<PasswordResetToken> findAllByActiveAndUserEmail(boolean active, String email);
}
