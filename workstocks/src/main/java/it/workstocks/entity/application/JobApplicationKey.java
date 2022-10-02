package it.workstocks.entity.application;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class JobApplicationKey implements Serializable {
	
	private static final long serialVersionUID = 7538611871513724068L;
	
	private Long applicantId;
	
	private Long jobOfferId;

}
