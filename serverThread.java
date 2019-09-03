import java.io.*;
import java.net.*;
import java.util.*;

class TCPServerThread {

    static int nUsers;
    static HashMap<String, Integer> mapReceiverPorts = new HashMap<>();

    public static void main(String argv[]) throws Exception {
        nUsers = 1;

        System.out.println(nUsers + ":\n");
        ServerSocket[] receiverSocket = new ServerSocket[nUsers];
        ServerSocket[] senderSocket = new ServerSocket[nUsers];
        int[] rPorts = { 6001, 6002, 6003, 6004, 6005 };
        int[] sPorts = { 7001, 7002, 7003, 7004, 7005 };

        // System.out.println("ser start");
        // int i;
        for (int i = 0; i < nUsers; i++) {
            receiverSocket[i] = new ServerSocket(rPorts[i]);
            senderSocket[i] = new ServerSocket(sPorts[i]);

            // System.out.println("con estab");

            Socket receiverConnectionSocket = receiverSocket[i].accept();
            Socket senderConnectionSocket = senderSocket[i].accept();

            BufferedReader inReceiver = new BufferedReader(
                    new InputStreamReader(receiverConnectionSocket.getInputStream()));
            BufferedReader inSender = new BufferedReader(
                    new InputStreamReader(senderConnectionSocket.getInputStream()));

            DataOutputStream outReceiver = new DataOutputStream(receiverConnectionSocket.getOutputStream());
            DataOutputStream outSender = new DataOutputStream(senderConnectionSocket.getOutputStream());

            // threadReceiverClass socketThreadReceiverClass = new
            // threadReceiverClass(receiverConnectionSocket, inReceiver, outReceiver,
            // rPorts[i], mapReceiverPorts);
            // threadSenderClass socketThreadSenderClass = new
            // threadSenderClass(senderConnectionSocket, inSender, outSender, sPorts[i],
            // mapReceiverPorts);
            Thread threadReceiver = new Thread(new threadReceiverClass(receiverConnectionSocket, inReceiver,
                    outReceiver, rPorts[i], mapReceiverPorts));
            Thread threadSender = new Thread(
                    new threadSenderClass(senderConnectionSocket, inSender, outSender, sPorts[i], mapReceiverPorts));

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
        HashMap<String, Integer> mapR;
        int rPort;

        threadReceiverClass(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient,
                int rPort, HashMap<String, Integer> mapR) {
            this.connectionSocket = connectionSocket;
            this.inFromClient = inFromClient;
            this.outToClient = outToClient;
            this.mapR = mapR;
            this.rPort = rPort;
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
                    inFromClient.readLine();

                    System.out.println("client sentence on rec to sev: " + clientSentence);

                    modifiedSentence = clientSentence.substring(16);

                    if (usernameChecker(modifiedSentence)) {
                        outToClient.writeBytes("REGISTERED TORECV " + modifiedSentence + "\n\n");
                        mapR.put(modifiedSentence, rPort);
                        // connectionSocket.close();
                        return;
                    } else {
                        outToClient.writeBytes("ERROR 100 Malformed username\n\n");
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
        BufferedReader inFromClient;
        DataOutputStream outToClient;
        HashMap<String, Integer> mapR;
        int sPort;

        threadSenderClass(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient, int sPort,
                HashMap<String, Integer> mapR) {
            this.connectionSocket = connectionSocket;
            this.inFromClient = inFromClient;
            this.outToClient = outToClient;
            this.mapR = mapR;
            this.sPort = sPort;
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
                    inFromClient.readLine();

                    modifiedSentence = clientSentence.substring(16);

                    // System.out.println("\n" + modifiedSentence);
                    if (usernameChecker(modifiedSentence)) {
                        modifiedSentence = "REGISTERED TOSEND " + modifiedSentence + "\n\n";
                        // System.out.println("swe:"+modifiedSentence);
                        outToClient.writeBytes(modifiedSentence);
                        // connectionSocket.close();
                        break;
                    } else {
                        // System.out.println("swe:err");
                        outToClient.writeBytes("ERROR 100 Malformed username\n\n");// \n
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

            }
        }
    }

}
