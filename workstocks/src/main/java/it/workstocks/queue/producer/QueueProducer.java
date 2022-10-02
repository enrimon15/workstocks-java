package it.workstocks.queue.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.queue.message.EmailMessageJob;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueueProducer {

	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private WorkstocksProperties properties;

	public void send(EmailMessageJob message) {
		log.debug("QUEUE:: sending message='{}'", message);
		jmsTemplate.convertAndSend(properties.getQueueName(), message);
	}
}