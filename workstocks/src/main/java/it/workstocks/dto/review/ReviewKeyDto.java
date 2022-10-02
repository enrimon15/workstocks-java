package it.workstocks.dto.review;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewKeyDto implements Serializable {

	private static final long serialVersionUID = 814869108603268844L;

	private Long applicantId;

	@NotNull
	private Long companyId;

}
