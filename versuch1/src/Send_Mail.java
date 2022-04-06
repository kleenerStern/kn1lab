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

			// Neues Eigenschaften-objekt anlegen
			Properties properties = new Properties();
			// Neue Session-Instanz anhand der Eigenschaften erstellen
			Session session = Session.getInstance(properties,null);

			// Neue Mimemessage erstellen, anschließend wird diese "gefüllt"
			MimeMessage msg = new MimeMessage(session);

			// Ein paar Eingaben fordern um identifizierbare Test-Mails zu erstellen
			System.out.println("Input your data to contact labrat@localhost");
			System.out.println("Username / Firstname:");
			String from = scanner.nextLine();
			while (from.contains(" ")){
				System.err.println("No whitespaces allowed. Please try again.");
				from = scanner.nextLine();
			}
			// Absende-Adresse zusammenbauen
			msg.setFrom(from + "@localhost");
			// Empfänger festlegen
			msg.setRecipients(Message.RecipientType.TO,"labrat@localhost");

			System.out.println("Subject:");
			String subject = scanner.nextLine();
			// Betreff setzen
			msg.setSubject(subject);

			// Aktuelles Datum holen und als versandt-Datum setzen
			Date currentDate = new Date();
			msg.setSentDate(currentDate);

			// Mailtext von nutzer fordern und setzen
			System.out.println("Mail Text:");
			String text = scanner.nextLine();
			msg.setText(text);

			// Nachricht absenden
			Transport.send(msg);
			System.out.println("Thank you, " + from + ", your mail has been sent to labrat@localhost at " + currentDate + ".");

			// your code here
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
