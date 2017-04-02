package cn.yihai.basic.core.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Email {
	
	private static String DEFAULT_SENDER;
	private static String DEFAULT_PASSWORD;
	private static String DEFAULT_SERVER;
	private static String DEFAULT_PORT;
	
	private static final String CONFIG_FILE = "/email.properties";
	
	static{
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = Email.class.getResourceAsStream(CONFIG_FILE);
			properties.load(in);
			DEFAULT_SENDER = properties.getProperty("sender");
			DEFAULT_PASSWORD = properties.getProperty("password");
			DEFAULT_SERVER = properties.getProperty("server");
			DEFAULT_PORT = properties.getProperty("port");
		} catch (IOException e) {
			throw new RuntimeException("加载email配置文件失败");
		} finally {
			try {
				if(in!=null){
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 发送者
	 */
	private String sender;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 邮件服务器
	 */
	private String server;
	
	/**
	 * 邮件服务器端口
	 */
	private String port;
	
	/**
	 * 接收者
	 */
	private String to;
	
	/**
	 * 主题
	 */
	private String subject;
	
	/**
	 * 正文
	 */
	private String content;
	
	/**
	 * 附件列表文件路径
	 */
	private String attachFileParentPath;
	
	/**
	 * 附件列表文件名
	 */
	private List<String> attachFileNames;

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
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

	public void setContent(String content) {
		this.content = content;
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

	public void setAttachFileNames(List<String> attachFileNames) {
		this.attachFileNames = attachFileNames;
	}

	/**
	 * 
	 * @param addresses 邮件接收人地址,多个地址用逗号分隔
	 * @param subject 主题
	 * @param content 正文
	 * @return 使用默认发送者的不带附件的Email对象
	 */
	public static Email BuildDefaultEmail(String addresses, String subject, String content) {
		return BuildAttachmentEmail(addresses, subject, content, null, null);
	}
	
	/**
	 * 
	 * @param addresses 邮件接收人地址,多个地址用逗号分隔
	 * @param subject 主题
	 * @param content 正文
	 * @param attachFileParentPath 附件列表文件路径 
	 * @param attachFileNames 附件列表文件名集合
	 * @return 使用默认发送者的带附件的Email对象
	 */
	public static Email BuildAttachmentEmail(String addresses, String subject, String content, String attachFileParentPath, List<String> attachFileNames) {
		Email email = new Email();
		email.setSender(DEFAULT_SENDER);
		email.setPassword(DEFAULT_PASSWORD);
		email.setServer(DEFAULT_SERVER);
		email.setPort(DEFAULT_PORT);
		email.setTo(addresses);
		email.setSubject(subject);
		email.setContent(content);
		if (attachFileParentPath != null && !attachFileParentPath.isEmpty()) {
			email.setAttachFileParentPath(attachFileParentPath);
		}
		if (attachFileNames != null && !attachFileNames.isEmpty()) {
			email.setAttachFileNames(attachFileNames);
		}
		return email;
	}

}
