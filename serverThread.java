import java.io.*;
import java.net.*;

class TCPServerThread {

    public static void main(String argv[]) throws Exception {

        System.out.println("\n");

        System.out.println("ser start");

        ServerSocket receiverSocket = new ServerSocket(6789);
        ServerSocket senderSocket = new ServerSocket(6788);

        System.out.println("con estab");

        //while (true) {

            Socket receiverConnectionSocket = receiverSocket.accept();
            Socket senderConnectionSocket = senderSocket.accept();

            BufferedReader inReceiver = new BufferedReader(
                    new InputStreamReader(receiverConnectionSocket.getInputStream()));
            BufferedReader inSender = new BufferedReader(
                    new InputStreamReader(senderConnectionSocket.getInputStream()));

            DataOutputStream outReceiver = new DataOutputStream(receiverConnectionSocket.getOutputStream());
            DataOutputStream outSender = new DataOutputStream(senderConnectionSocket.getOutputStream());

            // SocketThread socketThread = new SocketThread(connectionSocket, inFromClient,
            // outToClient);
            Thread threadReceiver = new Thread(
                    new threadReceiverClass(receiverConnectionSocket, inReceiver, outReceiver));
            Thread threadSender = new Thread(new threadSenderClass(senderConnectionSocket, inSender, outSender));

            threadReceiver.start();
            threadSender.start();

            

        //}

        //receiverSocket.close();
        //senderSocket.close();
    }
}

// REGISTERED TORECV [username]\n \n
// ERROR 100 Malformed username\n \n

class threadReceiverClass implements Runnable {
    String clientSentence;
    String modifiedSentence;
    Socket connectionSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    threadReceiverClass(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) {
        this.connectionSocket = connectionSocket;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
    }

    public boolean usernameChecker(String usr) {
        for (int i = 0; i < usr.length(); i++) {
            Character c = usr.charAt(i);
            int v = c;
            if (!((v >= 65 && v <= 90) || (v >= 65 && v <= 90) || (v >= 48 && v <= 57)))
                return false;
        }
        return true;
    }

    public void run() {
        while (true) {
            try {

                clientSentence = inFromClient.readLine();

                System.out.println("client sentence on rec to sev: " + clientSentence);

                modifiedSentence = clientSentence.substring(16, modifiedSentence.length() - 2);
                // REGISTERED TORECV [username]\n \n

                if (usernameChecker(modifiedSentence))
                    outToClient.writeBytes("REGISTERED TORECV " + modifiedSentence + "\n\n");
                else
                    outToClient.writeBytes("ERROR 100 Malformed username\n\n");
            } catch (Exception e) {
                try {
                    connectionSocket.close();
                } catch (Exception ee) {
                }
                break;
            }
        }
    }
}

class threadSenderClass implements Runnable {
    String clientSentence;
    String modifiedSentence;
    Socket connectionSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    threadSenderClass(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) {
        this.connectionSocket = connectionSocket;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
    }

    public boolean usernameChecker(String usr) {
        for (int i = 0; i < usr.length(); i++) {
            Character c = usr.charAt(i);
            int v = c;
            if (!((v >= 65 && v <= 90) || (v >= 65 && v <= 90) || (v >= 48 && v <= 57)))
                return false;
        }
        return true;
    }

    public void run() {
        while (true) {
            try {

                clientSentence = inFromClient.readLine();

                System.out.println("client sentence on rec to sev: " + clientSentence);

                modifiedSentence = clientSentence.substring(16, modifiedSentence.length() - 2);
                // REGISTERED TORECV [username]\n \n

                if (usernameChecker(modifiedSentence))
                    outToClient.writeBytes("REGISTERED TOSEND " + modifiedSentence + "\n\n");
                else
                    outToClient.writeBytes("ERROR 100 Malformed username\n\n");
            } catch (Exception e) {
                try {
                    connectionSocket.close();
                } catch (Exception ee) {
                }
                break;
            }
        }
    }
}

class SocketThread implements Runnable {
    String clientSentence;
    String capitalizedSentence;
    Socket connectionSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    SocketThread(Socket connectionSocket, BufferedReader inFromClient, DataOutputStream outToClient) {
        this.connectionSocket = connectionSocket;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
    }

    public void run() {
        while (true) {
            try {

                clientSentence = inFromClient.readLine();

                System.out.println(clientSentence);

                capitalizedSentence = clientSentence.toUpperCase() + '\n';

                outToClient.writeBytes(capitalizedSentence);
            } catch (Exception e) {
                try {
                    connectionSocket.close();
                } catch (Exception ee) {
                }
                break;
            }
        }
    }
}
