package it.workstocks.entity.review;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ReviewKey implements Serializable {
	
	private static final long serialVersionUID = 814869108603268844L;

	private Long applicantId;
	
	private Long companyId;

}
