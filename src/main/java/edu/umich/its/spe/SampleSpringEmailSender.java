package edu.umich.its.spe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.umich.its.spe.SpringEmailSender;

// Example of sending email from spring via our wrapper class.

/*  Spring boot will want to have properties in application.properties.
 * These assume a summary SMTP server running in localhost 1025.
 * If you have python installed then the following line should start a server using a standard library.
   python -m smtpd -d -n -c DebuggingServer localhost:1025 &

spring.mail.host=localhost
spring.mail.port=1025
spring.mail.protocol=smtp
spring.mail.defaultEncoding=UTF-8
spring.mail.test-connection=true
*/

// Allow picking up properties and injection
@EnableAutoConfiguration

@SpringBootApplication
public class SampleSpringEmailSender implements CommandLineRunner {

	static final Logger M_log = LoggerFactory.getLogger(SampleSpringEmailSender.class);

	@Autowired SpringEmailSender ses;

	private String testEmailText = "DEFAULT TEXT";

	private String to = "dlhaines@umich.edu";

	String home() {
		M_log.debug("using default text");
		return home(null);
	}

	String home(String text) {

		if (text == null) {
			text = testEmailText;
		}

		text = "["+text+"]";

		try {
			sendEmail(text);
			return "Email sent!";
		}
		catch (Exception e) {
			String msg = "Error in sending email: "+e;
			M_log.error(msg);
			return msg;
		}
	}

	private void sendEmail(String text)  {
		M_log.info("sendMail: text: {}",text);

		String subject = "SPE summary at "+getISO8601StringForDate(new Date());
		M_log.info("email msg: {}",text);
		ses.sendSimpleMessage(to,subject,text);
		M_log.info("send message to: {}",to);
	}

	private static String getISO8601StringForDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}

	// The 'run' method will be called to start application code.

	@Override
	public void run(String... args) throws Exception {
		M_log.error("starting run args: "+args.toString());
		home();
		home("How are you now? " + getISO8601StringForDate(new Date()));
	}

	// This is static entry point required for Spring Boot.
	public static void main(String[] args) {
		M_log.error("staring main");
		SpringApplication.run(SampleSpringEmailSender.class, args);
	}


}
