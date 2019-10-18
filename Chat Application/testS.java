// import java.io.*; 
// import java.net.*; 

// class Server { 
//   public static void main(String argv[]) throws Exception{ 
//         ServerSocket welcomeSocket = new ServerSocket(6789); 

//         while(true) { 
//             Socket connectionSocket = welcomeSocket.accept(); 
//             DataInputStream inFromClient = 
//             new DataInputStream(connectionSocket.getInputStream()); 

//             DataOutputStream outToClient = 
//             new DataOutputStream(connectionSocket.getOutputStream()); 

//             SocketThread socketThread = new SocketThread(connectionSocket, inFromClient, outToClient);
//             Thread thread = new Thread(socketThread);
//             thread.start();  
//         }
//     } 
// } 
 

// class SocketThread implements Runnable {

//     String clientSentence; 
//     String capitalizedSentence; 
//     Socket connectionSocket;
//     DataInputStream inFromClient;
//     DataOutputStream outToClient;
   
//     SocketThread (Socket connectionSocket, DataInputStream inFromClient, DataOutputStream outToClient) {
// 	    this.connectionSocket = connectionSocket;
//         this.inFromClient = inFromClient;
//         this.outToClient = outToClient;
//     } 

//     public void run() {
//         while(true) { 
//             try {
//                 clientSentence = inFromClient.readLine(); 

//                 System.out.println(clientSentence);
//                 outToClient.writeUTF(clientSentence.substring(3));
//                 System.out.println("here");     
//             } 
//             catch(Exception e) {
//                 System.out.println(e);
//             }
//         } 
//     }
// }