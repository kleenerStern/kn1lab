import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
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
   	    //Text einlesen und in Worte zerlegen
        System.out.println("Please input sentence to send: ");
        Scanner scanner = new Scanner(System.in);
        String inputString = scanner.nextLine();
        ArrayList<String> wordArrayList = new ArrayList<>(Arrays.asList(inputString.split(" ")));
        wordArrayList.add("EOT");
        String[] words = wordArrayList.toArray(new String[0]);

        // Socket erzeugen auf Port 9998 und Timeout auf eine Sekunde setzen
        DatagramSocket clientSocket = new DatagramSocket(9998);
        clientSocket.setSoTimeout(1000);

        // Iteration über den Konsolentext
        int seq = 0;
        int acknum = 0;
        int payloadLength;
        int i = 0;

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        while (true) {

            byte[] payload = words[i].getBytes();
            payloadLength = payload.length;

            Packet packetOut = new Packet(seq,acknum,false,payload);

            // Paket serialisieren
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(packetOut);
            byte[] buf = b.toByteArray();

        	// Paket an Port 9997 senden
            DatagramPacket packet = new DatagramPacket(buf, buf.length,InetAddress.getLocalHost(),9997);
        	clientSocket.send(packet);

            try {
                // Auf ACK warten und erst dann Schleifenzähler inkrementieren
                clientSocket.receive(receivePacket);
                ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()));
                Packet packetIn = (Packet)inputStream.readObject();

                if (packetIn.getAckNum()==seq+payloadLength){
                    seq = seq+payloadLength;
                    i++;
                    if (i==words.length){
                        break;
                    }
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
            	System.out.println("Receive timed out, retrying...");
            }
        }
        
        // Wenn alle Packete versendet und von der Gegenseite bestätigt sind, Programm beenden
        clientSocket.close();
        
        if(System.getProperty("os.name").equals("Linux")) {
            clientSocket.disconnect();
        }

        System.exit(0);
    }
}
