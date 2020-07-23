# **Socket Programming Examples**
Send a message from one machine (client) to another machine (server) over a socket.

Compile your code
```
mvn package
```

## **TCP Socket**
Connection based method

Start the server
```
java -cp target/socket_programming-1.0-SNAPSHOT.jar edu.rit.cs.TCPServer
```

Run the client (change the localhost to hostname or IP address of the machine where you run the server)
```
java -cp target/socket_programming-1.0-SNAPSHOT.jar edu.rit.cs.TCPClient HelloWord localhost
```


Server Output
```
TCP Server is running and accepting client connections...
Received "HelloWord", echo back now...
```

Client Output
```
Sent: HelloWord
Received: HelloWord
```


## UDP Socket

Start the server
```
java -cp target/socket_programming-1.0-SNAPSHOT.jar edu.rit.cs.socket.UDPServer
```

Run the client (change the localhost to hostname or IP address of the machine where you run the server)
```
java -cp target/socket_programming-1.0-SNAPSHOT.jar edu.rit.cs.socket.UDPClient HelloWord localhost
```