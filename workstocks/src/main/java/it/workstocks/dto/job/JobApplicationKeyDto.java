package it.workstocks.dto.job;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationKeyDto implements Serializable {
	private static final long serialVersionUID = -3340248329051444565L;
	private Long applicantId;
	private Long jobOfferId;
}
