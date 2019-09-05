import java.io.*;
import java.net.*;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.MessageDigest;
import java.util.*;
import javax.crypto.Cipher;

class TCPClient2 {
  static String pubKey;
  static String pvtKey;

  public static void main(String argv[]) throws Exception {
    String sentence;
    String modifiedSentence;

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    Socket receiverSocket = new Socket("localhost", 6002);
    Socket senderSocket = new Socket("localhost", 7002);

    DataOutputStream outSender = new DataOutputStream(senderSocket.getOutputStream());
    DataOutputStream outReceiver = new DataOutputStream(receiverSocket.getOutputStream());

    BufferedReader inSender = new BufferedReader(new InputStreamReader(senderSocket.getInputStream()));
    BufferedReader inReceiver = new BufferedReader(new InputStreamReader(receiverSocket.getInputStream()));

    KeyPair generateKeyPair = generateKeyPair();
    byte[] publicKey = generateKeyPair.getPublic().getEncoded();
    byte[] privateKey = generateKeyPair.getPrivate().getEncoded();

    pubKey = Base64.getEncoder().encodeToString(publicKey);////////////
    pvtKey = Base64.getEncoder().encodeToString(privateKey);///////////

    while (true) {
      System.out.print("Write your username: ");
      sentence = inFromUser.readLine();
      // inFromUser.readLine();

      outSender.writeBytes("REGISTER TOSEND " + sentence + "\n");
      modifiedSentence = inSender.readLine();
      // inSender.readLine();
      // System.out.println("\n"+modifiedSentence);
      if (!modifiedSentence.substring(0, 3).equals("REG")) {
        System.out.println("Bad Username");
        continue;
      }

      outReceiver.writeBytes("REGISTER TORECV " + sentence + " " + pubKey + "\n");/////////////////

      modifiedSentence = inReceiver.readLine();
      // inReceiver.readLine();
      if (!modifiedSentence.substring(0, 3).equals("REG")) {
        System.out.println("Bad Username");
        continue;
      }
      System.out.println("connection established");
      break;
    }
    Thread threadReceiver = new Thread(new threadReceiverClass(receiverSocket, inReceiver, outReceiver));
    Thread threadSender = new Thread(new threadSenderClass(senderSocket, inFromUser, inSender, outSender));

    threadReceiver.start();
    threadSender.start();
  }

  static class threadReceiverClass implements Runnable {
    String serverSentence;
    String modifiedSentence;
    String contSentence;
    String content;
    Socket connectionSocket;
    BufferedReader inFromServer;
    DataOutputStream outToServer;

    threadReceiverClass(Socket connectionSocket, BufferedReader inFromServer, DataOutputStream outToServer) {
      this.connectionSocket = connectionSocket;
      this.inFromServer = inFromServer;
      this.outToServer = outToServer;
    }

    public void run() {
      while (true) {
        try {
          int flag = 1;
          serverSentence = inFromServer.readLine();
          contSentence = inFromServer.readLine();
          content = inFromServer.readLine();
          // inFromServer.readLine();
          // System.out.println("Fowarded Msg Rec From Server: " + serverSentence +
          // "\n"+contSentence+"\n"+content+"\n");
          if (!serverSentence.substring(0, 8).equals("FORWARD ") || serverSentence.charAt(8) == ' '
              || !contSentence.substring(0, 16).equals("Content-length: ") || contSentence.charAt(16) == ' ')
            flag = 0;
          else {
            modifiedSentence = serverSentence.substring(8);
            contSentence = contSentence.substring(16);
            content = content.substring(0, Integer.parseInt(contSentence));
          }
          if (flag != 0) {

            outToServer.writeBytes("RECEIVED " + modifiedSentence + "\n");
            String pubKeySender = inFromServer.readLine();
            // boolean tamper= recieverTamperCheck(pubKeySender, pvtKey, content.split("
            // ")[0], content.split(" ")[1]);
            boolean tamper = true;

            content = recieverGenerate(pubKeySender, pvtKey, content.split(" ")[0], content.split(" ")[1]);

            System.out.println("Message Received From " + modifiedSentence + " : " + content);

          } else {
            outToServer.writeBytes("ERROR 103 Header Incomplete\n");
          }
        } catch (Exception e) {

        }
      }
    }
  }

  private static final String ALGORITHM = "RSA";

  public static byte[] encrypt(byte[] publicKey, byte[] inputData) throws Exception {
    PublicKey key = KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKey));

    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);

    byte[] encryptedBytes = cipher.doFinal(inputData);

    return encryptedBytes;
  }

  public static byte[] decrypt(byte[] privateKey, byte[] inputData) throws Exception {

    PrivateKey key = KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(privateKey));

    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);

    byte[] decryptedBytes = cipher.doFinal(inputData);

    return decryptedBytes;
  }

  public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {

    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);

    SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

    // 512 is keysize
    keyGen.initialize(512, random);

    KeyPair generateKeyPair = keyGen.generateKeyPair();
    return generateKeyPair;
  }

  public static MessageDigest md;

  public static String senderGenerate(String message, String pubKeyB, String pvtKeyA) throws Exception {
    // A->B
    // System.out.println("sender start+ "+ message);
    byte[] publicKeyB = Base64.getDecoder().decode(pubKeyB);
    byte[] privateKeyA = Base64.getDecoder().decode(pvtKeyA);

    byte[] mDash = encrypt(publicKeyB, message.getBytes());// M'

    // byte[] shaMdash = md.digest(mDash);// H

    // byte[] hDash = encrypt(privateKeyA,
    // (Base64.getEncoder().encodeToString(shaMdash)).getBytes());// H'
    // =kvtA(64(H))

    String mDash64 = Base64.getEncoder().encodeToString(mDash);
    // String hDash64 = Base64.getEncoder().encodeToString(hDash);
    String hDash64 = "asdf";
    // System.out.println(mDash64+" "+hDash64);
    return hDash64 + " " + mDash64;
  }

  public static String recieverGenerate(String pubKeyA, String pvtKeyB, String hDash64, String mDash64)
      throws Exception {
    // A->B
    // byte[] publicKeyA = Base64.getDecoder().decode(pubKeyA);
    byte[] privateKeyB = Base64.getDecoder().decode(pvtKeyB);
    // byte[] hDash = Base64.getDecoder().decode(hDash64);
    byte[] mDash = Base64.getDecoder().decode(mDash64);
    // byte[] h = Base64.getDecoder().decode(decrypt(publicKeyA, hDash)); // H' =
    // pvtA(64(H))

    // byte[] shaMdash = md.digest(mDash);

    // return true (h==mDash);

    String message = new String(decrypt(privateKeyB, mDash));
    return message;
  }

  public static boolean recieverTamperCheck(String pubKeyA, String pvtKeyB, String hDash64, String mDash64)
      throws Exception {
    // A->B
    byte[] publicKeyA = Base64.getDecoder().decode(pubKeyA);
    // byte[] privateKeyB = Base64.getDecoder().decode(pvtKeyB);
    byte[] hDash = Base64.getDecoder().decode(hDash64);
    byte[] mDash = Base64.getDecoder().decode(mDash64);
    byte[] h = Base64.getDecoder().decode(decrypt(publicKeyA, hDash)); // H' = pvtA(64(H))

    byte[] shaMdash = md.digest(mDash);

    return Arrays.equals(h, shaMdash);

    // String message = new String(decrypt(privateKeyB, mDash));

  }

  static class threadSenderClass implements Runnable {
    String userMessage;
    String desiredMessage;
    String recUsername;
    String serverSentance;
    int contLen;
    Socket connectionSocket;
    BufferedReader inFromUser;
    BufferedReader inFromServer;
    DataOutputStream outToServer;

    threadSenderClass(Socket connectionSocket, BufferedReader inFromUser, BufferedReader inFromServer,
        DataOutputStream outToServer) {
      this.connectionSocket = connectionSocket;
      this.inFromServer = inFromServer;
      this.outToServer = outToServer;
      this.inFromUser = inFromUser;
    }

    public int messageChecker(String usr) {
      // Make a function to check the user's Message.
      if (usr.charAt(0) != '@')
        return 0;
      int f = 0, ret = 0;
      for (int i = 0; i < usr.length(); i++) {
        Character c = usr.charAt(i);
        if (c == ' ' && f == 0) {
          f++;
          ret = i;
        }

      }
      return ret;
    }

    public void run() {
      while (true) {
        try {
          System.out.println("Enter @[Username] [Message] ");
          userMessage = inFromUser.readLine();
          int sub = messageChecker(userMessage);
          // System.out.println("sub = "+sub);
          if (sub != 0) {
            recUsername = userMessage.substring(1, sub);
            desiredMessage = userMessage.substring(sub + 1);
            // contLen = userMessage.length() - sub - 1;
            // System.out.println("fet =");
            outToServer.writeBytes("FETCHKEY " + recUsername + "\n");
            String pubKeyRecepient = inFromServer.readLine();
            // System.out.println(pubKeyRecepient);
            //////////////////////////////
            desiredMessage = senderGenerate(desiredMessage, pubKeyRecepient, pvtKey);
            // System.out.println(desiredMessage);
            contLen = desiredMessage.length();
            outToServer
                .writeBytes("SEND " + recUsername + "\n" + "Content-length: " + contLen + "\n" + desiredMessage + "\n");
            serverSentance = inFromServer.readLine();
            // System.out.println(serverSentance);
            // inFromServer.readLine();
            if (serverSentance.substring(0, 4).equals("SENT")) {
              System.out.println("Message Delivered to " + serverSentance.substring(5));
            } else if (serverSentance.substring(0, 9).equals("ERROR 102")) {
              System.out.println("Unable to Send Message");
            } else if (serverSentance.substring(0, 9).equals("ERROR 103")) {
              // This means that you are writing wrong headers, or the server's header parser
              // is wrong.
              System.out.println("Wrong Headers");
            } else {
              System.out.println("Unknown Response From Server :" + serverSentance);
            }
          } else {
            System.out.println("Incorrect Format, Please re-enter your message");
          }

        } catch (Exception e) {

        }
      }
    }
  }

}
