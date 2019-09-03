import java.io.*;
import java.net.*;

class TCPClient {

  public static void main(String argv[]) throws Exception {
    String sentence;
    String modifiedSentence;

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    Socket receiverSocket = new Socket("localhost", 6001);
    Socket senderSocket = new Socket("localhost", 7001);

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
    Thread threadReceiver = new Thread(new threadReceiverClass(receiverSocket,inReceiver, outReceiver));
    Thread threadSender = new Thread(new threadSenderClass(senderSocket,inFromUser, inSender, outSender));

    threadReceiver.start();
    threadSender.start();
  }
}
class threadReceiverClass implements Runnable {
  String serverSentence;
  String modifiedSentence;
  Socket connectionSocket;
  BufferedReader inFromServer;
  DataOutputStream outToServer;

  threadReceiverClass(Socket connectionSocket, BufferedReader inFromServer, DataOutputStream outToServer) {
      this.connectionSocket = connectionSocket;
      this.inFromServer = inFromServer;
      this.outToServer = outToServer;
  }
  public boolean headerChecker(String usr) {
    //Make a function to check the headers for forwarded Message.
    return true;
}

  public void run() {
      while (true) {
          try {
              //Check how to read the message from the server
              serverSentence = inFromServer.readLine();
              inFromServer.readLine();

              System.out.println("Fowarded Msg Rec From Server: " + serverSentence);

              if (headerChecker(modifiedSentence)) {
                //Print the Incoming Message for the User and then Send back an ack to Server
                  outToServer.writeBytes("RECEIVED " + modifiedSentence + "\n\n");                  
              } else {
                  outToServer.writeBytes("ERROR 103 Header Incomplete\n\n");
              }
          } catch (Exception e) {

          }
      }
  }
}

class threadSenderClass implements Runnable {
  String userMessage;
  String desiredMessage;
  String recUsername;
  String serverSentance;
  int contLen;
  Socket connectionSocket;
  BufferedReader inFromUser;
  BufferedReader inFromServer;
  DataOutputStream outToServer;

  threadSenderClass(Socket connectionSocket,BufferedReader inFromUser, BufferedReader inFromServer, DataOutputStream outToServer) {
      this.connectionSocket = connectionSocket;
      this.inFromServer = inFromServer;
      this.outToServer = outToServer;
      this.inFromUser= inFromServer;
  }
  public boolean messageChecker(String usr) {
    //Make a function to check the user's Message.
    return true;
}
  public void run() {
      while (true) {
          try {

              userMessage = inFromUser.readLine();
              if (messageChecker(userMessage)) {
              //get the desiredMessage substring from userMessage
              //get recUsername substring from userMessage
              //get contLen from UserMessage
              outToServer.writeBytes("SEND " + recUsername+"\n"+"Content-Length: "+contLen+"\n"+desiredMessage+"\n");
              serverSentance = inFromServer.readLine();
              inFromServer.readLine(); 
              if(serverSentance.substring(0,4).equals("SENT"))
                {
                  System.out.println("Message Delivered to " + serverSentance.substring(5));
                }
              else if(serverSentance.substring(0,9).equals("ERROR 102"))
                {
                  System.out.println("Unable to Send Message");
                }
              else if(serverSentance.substring(0,9).equals("ERROR 103"))
                {
                  //IDK what to do when header is wrong
                }
              else
              {
                System.out.println("Unknown Response From Server :" + serverSentance);
              }
              }
              else
              {
                System.out.println("Incorrect Format, Please re-enter your message");
              }

          } catch (Exception e) {

          }
      }
  }
}