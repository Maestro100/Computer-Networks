// import java.io.*; 
// import java.net.*; 
// class Client { 

//     public static void main(String argv[]) throws Exception { 
        
//         String sentence; 
//         String modifiedSentence; 

//         DataInputStream inFromUser = 
//           new DataInputStream(System.in); 

//         Socket clientSocket = new Socket("localhost", 6789); 

//         DataOutputStream outToServer = 
//           new DataOutputStream(clientSocket.getOutputStream()); 
       
//         DataInputStream inFromServer = 
//           new DataInputStream(clientSocket.getInputStream()); 

//         while(true){
//              sentence = inFromUser.readLine(); 
//              outToServer.writeUTF(sentence + '\n'); 
//              modifiedSentence = inFromServer.readUTF(); 
//              System.out.println("FROM SERVER: " + modifiedSentence); 
//         }          
//     } 
// } 