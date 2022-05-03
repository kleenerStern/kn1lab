import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Timer;
import java.time.Duration;
import java.util.concurrent.*;

/**
 * Die "Klasse" Sender liest einen String von der Konsole und zerlegt ihn in einzelne Worte. Jedes Wort wird in ein
 * einzelnes {@link Packet} verpackt und an das Medium verschickt. Erst nach dem Erhalt eines entsprechenden
 * ACKs wird das nächste {@link Packet} verschickt. Erhält der Sender nach einem Timeout von einer Sekunde kein ACK,
 * überträgt er das {@link Packet} erneut.
 */
public class Sender {
    /**
     * Hauptmethode, erzeugt Instanz des {@link Sender} und führt {@link #send()} aus.
     * @param args Argumente, werden nicht verwendet.
     */
    public static void main(String[] args) {
        Sender sender = new Sender();
        try {
            sender.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Erzeugt neuen Socket. Liest Text von Konsole ein und zerlegt diesen. Packt einzelne Worte in {@link Packet}
     * und schickt diese an Medium. Nutzt {@link SocketTimeoutException}, um eine Sekunde auf ACK zu
     * warten und das {@link Packet} ggf. nochmals zu versenden.
     * @throws IOException Wird geworfen falls Sockets nicht erzeugt werden können.
     */
    private void send() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String userInput;
        String[] words;
        int seq;
        int ackNum = 0;
        int exeptedAckNum;
        boolean ackFlag = false;
        int timeoutTime = 1000;
        int wordIdx = 0;
        String receivedString = "";
   	    //Text einlesen und in Worte zerlegen
        userInput = scanner.nextLine();
        userInput += " EOT";
        words = userInput.split(" ");
        // Socket erzeugen auf Port 9998 und Timeout auf eine Sekunde setzen
        DatagramSocket socket = new DatagramSocket(9998);
        socket.setSoTimeout(timeoutTime);
        // Iteration über den Konsolentext

        while (wordIdx < words.length) {

            seq = ackNum;
            Packet packetOut = new Packet(seq, ackNum, ackFlag, words[wordIdx].getBytes());

            // serialize Packet for sending
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(packetOut);
            byte[] buf = b.toByteArray();


            // Paket an Port 9997 senden
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buf, buf.length,
                    address, 9997);
            socket.send(packet);
            exeptedAckNum = seq + words[wordIdx].length();
            try {
                // Auf ACK warten und erst dann Schleifenzähler inkrementieren
                byte[] bufIn = new byte[256];
                DatagramPacket rcvPacketRaw = new DatagramPacket(bufIn, bufIn.length);
                socket.receive(rcvPacketRaw);
                TimeUnit.MILLISECONDS.sleep(timeoutTime);
                // deserialize Packet
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(rcvPacketRaw.getData()));
                Packet packetIn = (Packet) is.readObject();
                String payload = new String(packetIn.getPayload());
                /*
                System.out.println("Received packet");
                System.out.println("Sequence: " + packetIn.getSeq());
                System.out.println("Ack: " + packetIn.getAckNum());
                System.out.println("IsAckFlag: " + packetIn.isAckFlag());
                System.out.println("Payload: " + payload);
                */

                if(payload.equals("EOT")) {
                    System.out.println("Received all packages\nReceived String: " + receivedString);
                    break;
                }
                // kein Paket angekommen
                if(packetIn == null) {
                    throw new SocketTimeoutException("SocketTimeoutException");
                }
                // uebertragung erfolgreich
                if(packetIn.isAckFlag() && exeptedAckNum == packetIn.getAckNum()) {
                    ackNum = packetIn.getAckNum();
                    receivedString += payload + " ";
                    wordIdx++;
                }

                // TODO: if (packetIn.isAckFlag() == false)

                if (false) {
                    throw new ClassNotFoundException("ClassNotFoundException");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                System.out.println("Receive timed out, retrying...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        
        // Wenn alle Packete versendet und von der Gegenseite bestätigt sind, Programm beenden
        socket.close();
        
        if(System.getProperty("os.name").equals("Linux")) {
            socket.disconnect();
        }

        System.exit(0);
    }
}
