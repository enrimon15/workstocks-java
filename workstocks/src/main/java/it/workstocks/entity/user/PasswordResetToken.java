package it.workstocks.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.workstocks.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PasswordResetToken extends BaseEntity<Long>{
	
	@Id
	@GeneratedValue
    private Long id;
 
	@Column(unique = true, nullable = false)
    private String token;
 
    @ManyToOne
    private User user;
    
    @Column(nullable = false)
    private boolean active;
}
