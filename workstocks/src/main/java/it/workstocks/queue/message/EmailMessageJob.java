package it.workstocks.queue.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EmailMessageJob {
	private EmailType emailType;
	private long JobOfferId;
	private long applicantId;
}
