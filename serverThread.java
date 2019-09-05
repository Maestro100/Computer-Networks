import java.io.*;
import java.net.*;
import java.util.*;

class TCPServerThread {

    static int nUsers;
    static HashMap<String, Integer> mapReceiverPorts;
    static HashMap<String, String> mapUserKey;
    static int[] rPorts = { 6001, 6002, 6003, 6004, 6005 };
    static int[] sPorts = { 7001, 7002, 7003, 7004, 7005 };
    static ServerSocket[] receiverSocket;
    static ServerSocket[] senderSocket;
    static Socket[] receiverConnectionSocket;
    static Socket[] senderConnectionSocket;
    static BufferedReader[] inReceiver;
    static BufferedReader[] inSender;
    static DataOutputStream[] outReceiver;
    static DataOutputStream[] outSender;

    public static void main(String argv[]) throws Exception {
        nUsers = 2;
        mapReceiverPorts = new HashMap<>();
        mapUserKey = new HashMap<>();
        System.out.println(nUsers + " Users\n");
        receiverSocket = new ServerSocket[nUsers];
        senderSocket = new ServerSocket[nUsers];
        receiverConnectionSocket = new Socket[nUsers];
        senderConnectionSocket = new Socket[nUsers];
        inReceiver = new BufferedReader[nUsers];
        inSender = new BufferedReader[nUsers];
        outReceiver = new DataOutputStream[nUsers];
        outSender = new DataOutputStream[nUsers];
        // System.out.println("ser start");
        // int i;
        for (int i = 0; i < nUsers; i++) {
            receiverSocket[i] = new ServerSocket(rPorts[i]);
            senderSocket[i] = new ServerSocket(sPorts[i]);

            // System.out.println("con estab");

            receiverConnectionSocket[i] = receiverSocket[i].accept();
            senderConnectionSocket[i] = senderSocket[i].accept();

            inReceiver[i] = new BufferedReader(new InputStreamReader(receiverConnectionSocket[i].getInputStream()));
            inSender[i] = new BufferedReader(new InputStreamReader(senderConnectionSocket[i].getInputStream()));

            outReceiver[i] = new DataOutputStream(receiverConnectionSocket[i].getOutputStream());
            outSender[i] = new DataOutputStream(senderConnectionSocket[i].getOutputStream());

            // threadReceiverClass socketThreadReceiverClass = new
            // threadReceiverClass(receiverConnectionSocket, inReceiver, outReceiver,
            // rPorts[i], mapReceiverPorts);
            // threadSenderClass socketThreadSenderClass = new
            // threadSenderClass(senderConnectionSocket, inSender, outSender, sPorts[i],
            // mapReceiverPorts);
            Thread threadReceiver = new Thread(
                    new threadReceiverClass(receiverConnectionSocket[i], inReceiver[i], outReceiver[i], i));
            Thread threadSender = new Thread(
                    new threadSenderClass(senderConnectionSocket[i], inSender[i], outSender[i], i));

            threadReceiver.start();
            threadSender.start();
        }
    }

    static class threadReceiverClass implements Runnable {
        String clientSentence;
        String modifiedSentence;
        Socket connectionSocket;
        BufferedReader inFromClient;
        DataOutputStream outToClient;
        int index;

        threadReceiverClass(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient,
                int index) {
            this.connectionSocket = connectionSocket;
            this.inFromClient = inFromClient;
            this.outToClient = outToClient;
            this.index = index;
        }

        public boolean usernameChecker(String usr) {
            for (int i = 0; i < usr.length(); i++) {
                Character c = usr.charAt(i);
                int v = c;

                if (!((v >= 65 && v <= 90) || (v >= 97 && v <= 122) || (v >= 48 && v <= 57)))
                    return false;
            }
            return true;
        }

        public void run() {
            while (true) {
                try {

                    clientSentence = inFromClient.readLine();
                    // inFromClient.readLine();

                    // System.out.println("client sentence on rec to sev: " + clientSentence);

                    modifiedSentence = clientSentence.substring(16).split(" ")[0];
                    String pubKey = clientSentence.substring(16).split(" ")[1];

                    if (usernameChecker(modifiedSentence)) {
                        outToClient.writeBytes("REGISTERED TORECV " + modifiedSentence + "\n");
                        // System.out.println("REGISTERED TORECV " + modifiedSentence + "\n"+pubKey+"\n");
                        mapReceiverPorts.put(modifiedSentence, rPorts[index]);
                        mapUserKey.put(modifiedSentence, pubKey);
                        // connectionSocket.close();
                        return;
                    } else {
                        outToClient.writeBytes("ERROR 100 Malformed username\n");
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    static class threadSenderClass implements Runnable {
        String clientSentence;
        String modifiedSentence;
        Socket connectionSocket;
        String senderUsername;
        BufferedReader inFromClient;
        DataOutputStream outToClient;
        int index;
        int contLen;

        threadSenderClass(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient,
                int index) {
            this.connectionSocket = connectionSocket;
            this.inFromClient = inFromClient;
            this.outToClient = outToClient;
            this.index = index;
        }

        public boolean usernameChecker(String usr) {
            for (int i = 0; i < usr.length(); i++) {
                Character c = usr.charAt(i);
                int v = c;

                if (!((v >= 65 && v <= 90) || (v >= 97 && v <= 122) || (v >= 48 && v <= 57)))
                    return false;
            }
            return true;
        }

        public void run() {
            while (true) {
                try {

                    clientSentence = inFromClient.readLine();
                    // inFromClient.readLine();

                    modifiedSentence = clientSentence.substring(16);

                    // System.out.println("\n" + modifiedSentence);
                    if (usernameChecker(modifiedSentence)) {
                        senderUsername = modifiedSentence;
                        modifiedSentence = "REGISTERED TOSEND " + modifiedSentence + "\n";
                        // System.out.println("swe:"+modifiedSentence);
                        outToClient.writeBytes(modifiedSentence);
                        // connectionSocket.close();
                        break;
                    } else {
                        // System.out.println("swe:err");
                        outToClient.writeBytes("ERROR 100 Malformed username\n");// \n
                    }
                } catch (Exception e) {

                }
            }
            /*
             * Get the Send Message, parse it if its headers are correct then Forward
             * Messages to receiver by looking up the hash table for connection and send the
             * ack(when received from rec) to sender
             */
            while (true) {
                try {
                    // System.out.println("Forwarder Thread @ Server");
                    String fetchKey= inFromClient.readLine();
                    fetchKey= fetchKey.substring(9);
                    // System.out.println(fetchKey);
                    outToClient.writeBytes(mapUserKey.get(fetchKey)+"\n");
                    clientSentence = inFromClient.readLine();
                    // System.out.println("l1:"+clientSentence);
                    String contSentence = inFromClient.readLine();
                    // System.out.println("l2:"+contSentence);
                    String content = inFromClient.readLine();
                    // System.out.println("l4:"+content);
                    // inFromClient.readLine();
                    // System.out.println("Msg Rec From Client: " + clientSentence +
                    // "\n"+contSentence+"\n"+content+"\n");
                    int flag = 1;
                    if (!clientSentence.substring(0, 4).equals("SEND") || clientSentence.charAt(5) == ' '
                            || !contSentence.substring(0, 16).equals("Content-length: ")
                            || contSentence.charAt(16) == ' ') {
                        flag = 0;
                    } else {

                        modifiedSentence = clientSentence.substring(5);
                        contSentence = contSentence.substring(16);
                        contLen = Integer.parseInt(contSentence);
                        content = content.substring(0, contLen);
                    }
                    if (flag != 0) {
                        int rPort = 0;
                        // System.out.println("T1");
                        if (mapReceiverPorts.containsKey(modifiedSentence)) {
                            // System.out.println("T2");
                            rPort = mapReceiverPorts.get(modifiedSentence);
                            BufferedReader inRecipent = inReceiver[rPort - 6001];
                            DataOutputStream outRecipent = outReceiver[rPort - 6001];
                            outRecipent.writeBytes("FORWARD " + senderUsername + "\n" + "Content-length: " + contLen
                                    + "\n" + content + "\n");
                                    // System.out.println("this "+modifiedSentence);
                            // System.out.println("awe  FORWARD " + senderUsername + "\n" + "Content-length: " + contLen + "\n" + content + "\n");
                            clientSentence = inRecipent.readLine();
                            if (!clientSentence.substring(0, 9).equals("RECEIVED ")) {
                                // System.out.println("Msg Rec From Client: " + clientSentence);
                                outToClient.writeBytes("ERROR 102 Unable to send\n");
                            } else {
                                outRecipent.writeBytes(mapUserKey.get(senderUsername)+"\n");
                                // System.out.
                                outToClient.writeBytes("SENT " + modifiedSentence + "\n");
                            }
                        } else {
                            outToClient.writeBytes("ERROR 102 Unable to send\n");
                        }
                    } else {
                        outToClient.writeBytes("ERROR 103 Header Incomplete\n");
                    }
                } catch (Exception e) {

                }
            }
        }
    }

}
