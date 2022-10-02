package it.workstocks.queue;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.queue.message.EmailMessageJob;
import it.workstocks.service.EmailService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class QueueConsumer {

	@Autowired
	private EmailService emailService;

	@JmsListener(destination = "${workstocks.queue-name}", containerFactory = "workstocksQueue")
	public void receive(EmailMessageJob message) throws WorkstocksBusinessException, MessagingException {
		log.debug("QUEUE RECEIVE:: " + message);
		if (message != null) {
			switch (message.getEmailType()) {
			case APPLICATION:
				emailService.sendApplicationEmails(message);				
				break;
			case JOB_ALERT:
				emailService.sendJobAlert(message);
				break;
			default:
				throw new WorkstocksBusinessException(String.format("Message Type \"%s\" not recognized!", message.getEmailType()));
			}

		}

	}

}