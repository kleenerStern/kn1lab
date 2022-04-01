import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage; 

public class Send_Mail {
	public static void main(String[] args) {
		sendMail();   
	}
	
	public static void sendMail() {
		try {
			Properties props = new Properties();
			Session session = Session.getInstance(props,null);

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom("testSender@localhost");
			msg.setRecipients(Message.RecipientType.TO,"labrat@localhost");
			msg.setSubject("TestSubject");
			msg.setSentDate(new Date());
			msg.setText("TestText");
			Transport.send(msg);

			// your code here
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
