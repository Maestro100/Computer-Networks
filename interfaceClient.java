import java.io.*; 
import java.net.*; 
class TCPClient { 

    public static void main(String argv[]) throws Exception 
    { 
        String sentence; 
        String modifiedSentence; 

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 

        Socket clientSocket = new Socket("localhost", 6789); 

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream()); 
 
        
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        while(true) {

                  
             sentence = inFromUser.readLine(); 
<<<<<<< Updated upstream
             

=======
            
>>>>>>> Stashed changes
             outToServer.writeBytes(sentence + '\n'); 

             modifiedSentence = inFromServer.readLine(); 

             System.out.println("FROM SERVER: " + modifiedSentence); 

        }
<<<<<<< Updated upstream
=======

//        clientSocket.close(); 
//hsnd aj dmnasbdhas dnasbldna sdj asld asd

// extra
//git status
//git pull+
//git add .
//git commit -m "messagefsjdbf"
//git push
//dhasjbdmn asdvasjdbsavid
                   
>>>>>>> Stashed changes
    } 
    String reg
} 

