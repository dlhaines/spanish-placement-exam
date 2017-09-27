package edu.umich.its.spe;

/*
 * Create generator of java mail sender objects.  Use this to deal with custom javamail property handling.
 */

import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class SimpleJavaEmail {

	static final Logger M_log = LoggerFactory.getLogger(SimpleJavaEmail.class);

	Properties mailProperties;

	// Convert hashmap into properties after selecting the right ones.

	// Specify prefix for the mail properties
	SimpleJavaEmail(HashMap<String,String> externalProperties) {
		this(externalProperties,"mail.");
	}

	// Property name prefix is the part of the property name up to the first dot
	SimpleJavaEmail(HashMap<String,String> externalProperties,String propertyNamePrefix) {
		mailProperties = extractPrefixedProperties(propertyNamePrefix,externalProperties);
		M_log.debug("constructor: mailProperties: {}",mailProperties);
	}

	// Utility to pull out the properties with specific prefix.
	private Properties extractPrefixedProperties(String propertyNamePrefix,HashMap<String,String> propertyMap) {
		Properties extractedProperties = new Properties();

		for (String key : propertyMap.keySet()) {
			if (key.startsWith(propertyNamePrefix)) {
				extractedProperties.put(key, propertyMap.get(key));
				M_log.debug("extracted key: {} value: {}",key,propertyMap.get(key));
			}
		}

		M_log.info("final extracted properties: {}",extractedProperties);
		return extractedProperties;
	}

	// Just return a new mail sender with the right properties.
	public JavaMailSender getJavaMailSender() {

		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setJavaMailProperties(mailProperties);

		return sender;
	}

	public void sendSimpleMessage(String from, String to, String subject, String text) {

		M_log.debug("send message: to: {} subject: {}",to,subject);
		SimpleMailMessage message = new SimpleMailMessage();
		JavaMailSender sender = getJavaMailSender();

		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		sender.send(message);
	}

}
