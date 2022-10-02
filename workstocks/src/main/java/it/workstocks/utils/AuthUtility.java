package it.workstocks.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import it.workstocks.dto.user.UserDto;
import it.workstocks.dto.user.applicant.SimpleApplicantDto;
import it.workstocks.dto.user.company.SimpleCompanyOwnerDto;
import it.workstocks.entity.user.User;
import it.workstocks.security.UserDetailsImpl;

public class AuthUtility {
	
	public static Authentication getAuth() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static UserDto getCurrentUser() {
		Authentication authentication = getAuth();
		if (authentication != null && authentication instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
			return ((UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal()).getUser();
		} else {
			throw new RuntimeException();
		}
	}
	
	public static SimpleApplicantDto getCurrentApplicant()  {
		UserDto user = getCurrentUser();
		if (user instanceof SimpleApplicantDto) {
			return (SimpleApplicantDto) user;
		} else {
			return null;
		}
	}
	
	public static SimpleCompanyOwnerDto getCurrentCompanyOwner()  {
		UserDto user = getCurrentUser();
		if (user instanceof SimpleCompanyOwnerDto) {
			return (SimpleCompanyOwnerDto) user;
		} else {
			return null;
		}
	}
	
	public static boolean hasRole(String roleName) {		
		return getAuth() != null && getAuth().getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
	}
	
	public static boolean compareCurrentUser(Long idUserToCompare) {
		UserDto user = getCurrentUser();
		return user.getId().equals(idUserToCompare);
	}
	
	public static void renewAuthenticationPrincipal(User user) {
	    UserDetails u = new UserDetailsImpl(user);
	    Authentication authentication = new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities());
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
