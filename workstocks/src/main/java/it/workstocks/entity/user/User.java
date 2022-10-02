package it.workstocks.entity.user;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;

import it.workstocks.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends BaseEntity<Long>{
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String surname;
	
	@Column
	private String password;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Lob
	@Basic(fetch=FetchType.LAZY)
	private byte[] avatar;
	
	//////
	
	@Column(nullable = false)
    private boolean accountActive = true;

    @Column(nullable = false)
    private boolean accountExpired = false;

    @Column(nullable = false)
    private boolean accountLocked = false;

    @Column(nullable = false)
    private boolean credentialsExpired = false;
}
