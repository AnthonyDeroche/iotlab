/**
 * This file is part of the IoTLab project
 * For further information, check out our github page 
 * https://github.com/AnthonyDeroche/iotlab/
**/

package iotlab.utils;

import iotlab.module.admin.Config;
import iotlab.module.admin.ConfigException;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 
 * @author Arthur Garnier
 *
 */
public class SendMailSSL {
	


	private static Config config;
	private static String[] parameters = {"mail_sender","mail_receiver","smtp_username","smtp_password","smtp_host","smtp_auth","smtp_port","smtp_starttls"};

	public static void sendAlert(String MoteIp, String label, double value) {
		
		
		final String username = config.get("smtp_username").getValue(); 
		final String password = config.get("smtp_password").getValue();

		Properties props = new Properties();
		props.put("mail.smtp.host", config.get("smtp_host"));
		props.put("mail.smtp.auth", config.get("smtp_auth"));
		props.put("mail.smtp.socketFactory.port", config.get("smtp_port"));
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.starttls.enable", config.get("smtp_starttls"));
		props.put("mail.smtp.auth", config.get("smtp_auth"));
		props.put("mail.smtp.port", config.get("smtp_port"));

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(config.get("mail_sender").getValue()));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(config.get("mail_receiver").getValue()));
			message.setSubject("Alert");
			message.setText("Warning : \n Mote : "+ MoteIp+" \n Motion : "+label+"\n Value : "+value); 
			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void setConfig(Config config) throws ConfigException{
		
		for(String param : parameters){
			if(config.get(param)==null){
				throw new ConfigException("Missing parameter : '"+param+"' used to send mails");
			}
		}
		SendMailSSL.config=config;
	}
}
