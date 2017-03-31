package cn.yihai.basic.core.mail;

import java.util.List;
import java.util.Properties;

public class MailSenderInfo {
	
	/**
	 * 发送邮件的服务器地址
	 */
	private String mailServerHost;
	
	/**
	 * 发送邮件的服务器端口
	 */
	private String mailServerPort;
	
	/**
	 * 发送者邮箱地址
	 */
	private String fromAddress;
	
	/**
	 * 接收者邮箱地址,多个地址用逗号分隔
	 */
	private String toAddress;
	
	/**
	 * 发送者邮箱账号
	 */
	private String userName;
	
	/**
	 * 发送者邮箱密码
	 */
	private String password;
	
	/**
	 * 是否需要邮箱登录验证
	 */
	private boolean validate = false;
	
	/**
	 * 邮件主题
	 */
	private String subject;
	
	/**
	 * 邮件正文
	 */
	private String content;
	
	/**
	 * 邮件附件列表文件路径
	 */
	private String attachFileParentPath;
	
	/**
	 * 邮件附件列表文件名
	 */
	private List<String> attachFileNames;

	/**
	 * 获得邮件会话属性
	 */
	public Properties getProperties() {
		Properties prop = new Properties();
		prop.put("mail.smtp.host", this.mailServerHost);
		prop.put("mail.smtp.port", this.mailServerPort);
		prop.put("mail.smtp.auth", validate ? "true" : "false");
		return prop;
	}

	public String getMailServerHost() {
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String textContent) {
		this.content = textContent;
	}

	public String getAttachFileParentPath() {
		return attachFileParentPath;
	}

	public void setAttachFileParentPath(String attachFileParentPath) {
		this.attachFileParentPath = attachFileParentPath;
	}

	public List<String> getAttachFileNames() {
		return attachFileNames;
	}

	public void setAttachFileNames(List<String> fileNames) {
		this.attachFileNames = fileNames;
	}
}
