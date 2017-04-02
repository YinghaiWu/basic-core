package cn.yihai.basic.core.mail;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SimpleMailSender {

	public void sendMail(MailSenderInfo mailSenderInfo) throws Exception {
		//是否需要验证器
		MyAuthenticator authenticator = null;
		Properties prop = mailSenderInfo.getProperties();
		if (mailSenderInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailSenderInfo.getUserName(), mailSenderInfo.getPassword());
		}
		//构造发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(prop, authenticator);
		//根据session创建一个邮件消息,设置发送者 
		Message mailMessage = new MimeMessage(sendMailSession);
		Address from = new InternetAddress(mailSenderInfo.getFromAddress());
		mailMessage.setFrom(from);
		//设置接收者
		String[] toAddresses = mailSenderInfo.getToAddress().split(",");
		for (String toAddress : toAddresses) {
			toAddress = toAddress.trim();
			if (toAddress.equals("")) {
				continue;
			}
			Address to = new InternetAddress(toAddress);
			mailMessage.addRecipient(Message.RecipientType.TO, to);
		}
		//设置主题与发送时间
		mailMessage.setSubject(mailSenderInfo.getSubject());
		mailMessage.setSentDate(new Date());
		//设置文本内容，以及附件
		if (mailSenderInfo.getAttachFileParentPath()==null || mailSenderInfo.getAttachFileNames()==null) {
			mailMessage.setContent(mailSenderInfo.getContent(), "text/html; charset=utf-8");
		} else {
			//向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent(mailSenderInfo.getContent(), "text/html; charset=utf-8");
			multipart.addBodyPart(contentPart);
			//添加附件
			for (String fileName : mailSenderInfo.getAttachFileNames()) {
				BodyPart messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(mailSenderInfo.getAttachFileParentPath() + File.separator + fileName);
				messageBodyPart.setDataHandler(new DataHandler(source));
				fileName = MimeUtility.encodeText(fileName, "utf-8", "B");
				messageBodyPart.setFileName(fileName);
				multipart.addBodyPart(messageBodyPart);
			}
			mailMessage.setContent(multipart);
			//保存邮件
			mailMessage.saveChanges();
		}
		//发送邮件
		Transport.send(mailMessage);
	}

}