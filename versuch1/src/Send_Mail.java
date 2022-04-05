import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
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

			Scanner scanner = new Scanner(System.in);
			Properties properties = new Properties();
			Session session = Session.getInstance(properties,null);

			MimeMessage msg = new MimeMessage(session);
			System.out.println("Input your data to contact labrat@localhost");
			System.out.println("Username / Firstname:");
			String from = scanner.nextLine();
			while (from.contains(" ")){
				System.err.println("No whitespaces allowed. Please try again.");
				from = scanner.nextLine();
			}
			msg.setFrom(from + "@localhost");

			msg.setRecipients(Message.RecipientType.TO,"labrat@localhost");

			System.out.println("Subject:");
			String subject = scanner.nextLine();
			msg.setSubject(subject);

			Date currentDate = new Date();
			msg.setSentDate(currentDate);

			System.out.println("Mail Text:");
			String text = scanner.nextLine();
			msg.setText(text);

			Transport.send(msg);
			System.out.println("Thank you, " + from + ", your mail has been sent to labrat@localhost at " + currentDate + ".");

			// your code here

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
