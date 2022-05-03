import java.util.Properties;
import java.util.Scanner;
import javax.mail.*;

/**
 * Das Programm liefert beim erstmaligen ausführen genau das in Aufgabenteil 3 gefragte Resultat,
 * (sofern die Aufgabenstellung richtig verstanden wurde)
 * alles weitere sind spielereien die entstanden sind, weil ein Teammitglied mal wieder gefallen an Java gefunden hat,
 * diese können außer acht gelassen werden.
 */
public class Receive_Mail {
    public static void main(String[] args) throws Exception {
        fetchMail();
    }

    public static void fetchMail() {
        try {
            String host = "localhost";
            String mailStoreType = "pop3";
            String username = "labrat";
            String password = "kn1lab";

            Properties properties = new Properties();
            properties.put("mail.pop3.host", host);

            // Standard-Session mit vorher festgelegten Eigenschaften eröffnen
            Session session = Session.getDefaultInstance(properties);
            // Neue "Ablage" zum empfangen von pop3 Nachrichten
            Store store = session.getStore("pop3");
            store.connect(host, username, password);
            // Standard-Ordner anlegen um Mails dort zu speichern
            Folder folder = store.getFolder("INBOX");
            // Ordner öffnen um Nachrichten lesen und löschen zu können
            folder.open(Folder.READ_WRITE);
            // Nachrichten in Array legen
            Message messages[] = folder.getMessages();
            // Neuste Nachricht einer Variable zuordnen für leichten Zugriff

            boolean firstAccess = true;
            Scanner scanner = new Scanner(System.in);
            String userInput;

            int currentMsgNr = messages.length-1;
            do {
                if (firstAccess && currentMsgNr > 0) {
                    System.err.println("Disclaimer: Emails will be deleted after they have been viewed!");
                    System.out.println("Your latest Email is:\n");
                }
                if (currentMsgNr < 0){
                    System.out.println("No new Mails. Exiting...");
                    return;
                }
                Message currentMsg = messages[currentMsgNr];
                System.out.println("Email Nr: " + (currentMsgNr+1));
                System.out.println("From: " + currentMsg.getFrom()[0]);
                System.out.println("Subject: " + currentMsg.getSubject());
                System.out.println("Date: " + currentMsg.getSentDate());
                System.out.println("Content: " + currentMsg.getContent());
                firstAccess = false;
                currentMsg.setFlag(Flags.Flag.DELETED, true);
                if (currentMsgNr <= 0) {
					System.out.println("Last available Mail reached.");
					break;
                } else {
					System.out.println("Type 'n' to read the next Mail in line or 'e' to exit.");
					userInput = scanner.nextLine();
                }
                while (!(userInput.equals("e") || userInput.equals("n"))){
                    System.err.println("'" + userInput +"'" + " is not a valid option. Please try again.");
                    userInput = scanner.nextLine();
                }
                if (userInput.equals("n")) {
                    currentMsgNr--;
                }
            } while (!userInput.equals("e"));
            System.out.println("Exiting...");
            folder.close(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
