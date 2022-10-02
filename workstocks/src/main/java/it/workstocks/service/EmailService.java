package it.workstocks.service;

import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.queue.message.EmailMessageJob;

public interface EmailService {
	void sendApplicationEmails(EmailMessageJob emailMessage) throws WorkstocksBusinessException;
	void sendJobAlert(EmailMessageJob emailMessage) throws WorkstocksBusinessException;
	void sendResetToken(String emailTo, String token) throws WorkstocksBusinessException;
	void sendContactRequest(String emailTo, String emailFrom, String sourceTextMessage) throws WorkstocksBusinessException;
}
