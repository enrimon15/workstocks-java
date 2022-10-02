package it.workstocks.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.user.applicant.ApplicantDto;
import it.workstocks.entity.user.User;
import it.workstocks.entity.user.applicant.Applicant;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 7113485140073334648L;
	
	private EntityMapper mapper = Mappers.getMapper(EntityMapper.class);
	
	private static final SimpleGrantedAuthority ROLE_APPLICANT = new SimpleGrantedAuthority(Roles.APPLICANT);
	private List<GrantedAuthority> result = new ArrayList<GrantedAuthority>();
	
	private ApplicantDto loggedUser;
	
	private String password;
	private boolean isAccountExpired;
	private boolean isAccountLocked;
	private boolean isCredentialsExpired;
	private boolean isAccountActive;

	public UserDetailsImpl(User user) {
		super();
		
		if (user instanceof Applicant) {
			this.loggedUser = mapper.toApplicantDto((Applicant) user);
			result.add(ROLE_APPLICANT);
		}
		
		password = user.getPassword();
		isAccountExpired = user.isAccountExpired();
		isAccountLocked = user.isAccountLocked();
		isCredentialsExpired = user.isCredentialsExpired();
		isAccountActive = user.isAccountActive();
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return result;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return loggedUser.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return !isAccountExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !isAccountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !isCredentialsExpired;
	}

	@Override
	public boolean isEnabled() {
		return isAccountActive;
	}

	@Override
	public String toString() {
		return "UserDetailsImpl [username=" + loggedUser.getEmail() + "]";
	}

	public ApplicantDto getUser() {
		return loggedUser;
	}

}
