import java.io.*;
import java.net.*;

class TCPClient {

  public static void main(String argv[]) throws Exception {
    String sentence;
    String modifiedSentence;

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    Socket receiverSocket = new Socket("localhost", 6789);
    Socket senderSocket = new Socket("localhost", 6788);

    DataOutputStream outSender = new DataOutputStream(senderSocket.getOutputStream());
    DataOutputStream outReceiver = new DataOutputStream(receiverSocket.getOutputStream());

    BufferedReader inSender = new BufferedReader(new InputStreamReader(senderSocket.getInputStream()));
    BufferedReader inReceiver = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));

    while (true) {
      System.out.print("Write your username: ");
      sentence = inFromUser.readLine();
      // inFromUser.readLine();

      outSender.writeBytes("REGISTER TOSEND " + sentence + "\n\n");
      modifiedSentence = inSender.readLine();
      inSender.readLine();
      // System.out.println("\n"+modifiedSentence);
      if (!modifiedSentence.substring(0, 3).equals("REG")) {
        System.out.println("Bad Username");
        continue;
      }

      outReceiver.writeBytes("REGISTER TORECV " + sentence + "\n\n");
      modifiedSentence = inReceiver.readLine();
      inReceiver.readLine();
      if (!modifiedSentence.substring(0, 3).equals("REG")) {
        System.out.println("Bad Username");
        continue;
      }

      System.out.println("connection established");
      break;
    }

  }
}
