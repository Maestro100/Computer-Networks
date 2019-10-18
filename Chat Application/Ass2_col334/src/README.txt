Usage Server Application
1)Enter the Encryption Mode.
2)The server prints a copy of each forwarded message.

Usage Client Application(Run Only After starting the server application and entering encryption mode)
1)Enter the username.
2)You will get a "Connection Established" message on succesful registration of username 
  or "Bad Username" message for a malformed username.
3)Sending message format: @[Username][Your Message]
  Eg. @User1 Hello world
4)If user doesn't exist, you will get a "User not found" message or a "Message Delivered" message for a successfully delivered message.


Steps For Adding More Clients (Users)
1) Make a copy of Client File
2) Change base Class name for eg. TCPClient3 for 3rd client, TCPClient4 for 4th client and so on. (in Lines 17)
3) Change the Port numbers for ReceiverPort and SenderPort (in lines 28,29), to 6003,7003 respectively for 3rd client etc 
   (use Port numbers given in the server's ports list in the server code).
4) And Run.

Steps to Add Ports/Users on Server (Current Code can support upto 5 users)
1)Increase the number of ports by changing variable nUsers (in Line 23).
2)Add the receiver and sender ports in the arrays rPorts & sPorts ( lines 17,18 respectively).

