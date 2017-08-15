package edu.umich.its.spe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// Make this visible to Spring.
@Component

public class SpringEmailSender {

	static final Logger M_log = LoggerFactory.getLogger(SpringEmailSender.class);

	// Let spring inject the mail sender.  If there are problems make sure that
	// the mail configuration properties are set in application.properties.
	@Autowired JavaMailSender sender;

	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		sender.send(message);
	}

}
