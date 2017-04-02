package cn.yihai.basic.core.mail;

public class Sender {
	
	public static void send(Email email, boolean validate) throws Exception {
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(email.getServer());
		mailInfo.setMailServerPort(email.getPort());
		mailInfo.setUserName(email.getSender());
		mailInfo.setPassword(email.getPassword());
		mailInfo.setFromAddress(email.getSender());
		mailInfo.setToAddress(email.getTo());
		mailInfo.setValidate(validate);
		mailInfo.setSubject(email.getSubject());
		mailInfo.setContent(email.getContent());
		mailInfo.setAttachFileParentPath(email.getAttachFileParentPath());
		mailInfo.setAttachFileNames(email.getAttachFileNames());
		//发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendMail(mailInfo);
	}
	
	public static void main(String[] args) {
		Email email = Email.BuildDefaultEmail("abc@test.com","subject", "content");
		try{
			send(email, true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
