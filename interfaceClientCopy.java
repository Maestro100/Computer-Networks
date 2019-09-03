// copy before encryption decryption

// import java.io.*;
// import java.net.*;

// class TCPClient {

//   public static void main(String argv[]) throws Exception {
//     String sentence;
//     String modifiedSentence;

//     BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

//     Socket receiverSocket = new Socket("localhost", 6001);
//     Socket senderSocket = new Socket("localhost", 7001);

//     DataOutputStream outSender = new DataOutputStream(senderSocket.getOutputStream());
//     DataOutputStream outReceiver = new DataOutputStream(receiverSocket.getOutputStream());

//     BufferedReader inSender = new BufferedReader(new InputStreamReader(senderSocket.getInputStream()));
//     BufferedReader inReceiver = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));

//     while (true) {
//       System.out.print("Write your username: ");
//       sentence = inFromUser.readLine();
//       // inFromUser.readLine();

//       outSender.writeBytes("REGISTER TOSEND " + sentence + "\n\n");
//       modifiedSentence = inSender.readLine();
//       inSender.readLine();
//       // System.out.println("\n"+modifiedSentence);
//       if (!modifiedSentence.substring(0, 3).equals("REG")) {
//         System.out.println("Bad Username");
//         continue;
//       }

//       outReceiver.writeBytes("REGISTER TORECV " + sentence + "\n\n");
//       modifiedSentence = inReceiver.readLine();
//       inReceiver.readLine();
//       if (!modifiedSentence.substring(0, 3).equals("REG")) {
//         System.out.println("Bad Username");
//         continue;
//       }

//       System.out.println("connection established");
//       break;
//     }
//     Thread threadReceiver = new Thread(new threadReceiverClass(receiverSocket, inReceiver, outReceiver));
//     Thread threadSender = new Thread(new threadSenderClass(senderSocket, inFromUser, inSender, outSender));

//     threadReceiver.start();
//     threadSender.start();
//   }

//   static class threadReceiverClass implements Runnable {
//     String serverSentence;
//     String modifiedSentence;
//     String contSentence;
//     String content;
//     Socket connectionSocket;
//     BufferedReader inFromServer;
//     DataOutputStream outToServer;

//     threadReceiverClass(Socket connectionSocket, BufferedReader inFromServer, DataOutputStream outToServer) {
//       this.connectionSocket = connectionSocket;
//       this.inFromServer = inFromServer;
//       this.outToServer = outToServer;
//     }

//     public void run() {
//       while (true) {
//         try {
//           int flag = 1;
//           serverSentence = inFromServer.readLine();
//           contSentence = inFromServer.readLine();
//           content = inFromServer.readLine();
//           inFromServer.readLine();
//           // System.out.println("Fowarded Msg Rec From Server: " + serverSentence +
//           // "\n"+contSentence+"\n"+content+"\n");
//           if (!serverSentence.substring(0, 8).equals("FORWARD ") || serverSentence.charAt(8) == ' '
//               || !contSentence.substring(0, 16).equals("Content-length: ") || contSentence.charAt(16) == ' ')
//             flag = 0;
//           else {
//             modifiedSentence = serverSentence.substring(8);
//             contSentence = contSentence.substring(16);
//             content = content.substring(0, Integer.parseInt(contSentence));
//           }
//           if (flag != 0) {
//             System.out.println("Message Received From " + modifiedSentence + " : " + content);
//             outToServer.writeBytes("RECEIVED " + modifiedSentence + "\n\n");
//           } else {
//             outToServer.writeBytes("ERROR 103 Header Incomplete\n\n");
//           }
//         } catch (Exception e) {

//         }
//       }
//     }
//   }

//   static class threadSenderClass implements Runnable {
//     String userMessage;
//     String desiredMessage;
//     String recUsername;
//     String serverSentance;
//     int contLen;
//     Socket connectionSocket;
//     BufferedReader inFromUser;
//     BufferedReader inFromServer;
//     DataOutputStream outToServer;

//     threadSenderClass(Socket connectionSocket, BufferedReader inFromUser, BufferedReader inFromServer,
//         DataOutputStream outToServer) {
//       this.connectionSocket = connectionSocket;
//       this.inFromServer = inFromServer;
//       this.outToServer = outToServer;
//       this.inFromUser = inFromUser;
//     }

//     public int messageChecker(String usr) {
//       // Make a function to check the user's Message.
//       if (usr.charAt(0) != '@')
//         return 0;
//       int f = 0, ret = 0;
//       for (int i = 0; i < usr.length(); i++) {
//         Character c = usr.charAt(i);
//         if (c == ' ' && f == 0) {
//           f++;
//           ret = i;
//         }

//       }
//       return ret;
//     }

//     public void run() {
//       while (true) {
//         try {
//           System.out.println("Enter @[Username] [Message] ");
//           userMessage = inFromUser.readLine();
//           int sub = messageChecker(userMessage);
//           if (sub != 0) {
//             recUsername = userMessage.substring(1, sub);
//             desiredMessage = userMessage.substring(sub + 1);
//             contLen = userMessage.length() - sub - 1;
//             outToServer.writeBytes(
//                 "SEND " + recUsername + "\n" + "Content-length: " + contLen + "\n" + desiredMessage + "\n\n");
//             serverSentance = inFromServer.readLine();
//             inFromServer.readLine();
//             if (serverSentance.substring(0, 4).equals("SENT")) {
//               System.out.println("Message Delivered to " + serverSentance.substring(5));
//             } else if (serverSentance.substring(0, 9).equals("ERROR 102")) {
//               System.out.println("Unable to Send Message");
//             } else if (serverSentance.substring(0, 9).equals("ERROR 103")) {
//               // This means that you are writing wrong headers, or the server's header parser
//               // is wrong.
//               System.out.println("Wrong Headers");
//             } else {
//               System.out.println("Unknown Response From Server :" + serverSentance);
//             }
//           } else {
//             System.out.println("Incorrect Format, Please re-enter your message");
//           }

//         } catch (Exception e) {

//         }
//       }
//     }
//   }

// }
