# **Project 1**
Send a parsed file from one machine (client) to another machine (server) over a socket to do word count.
Then return results from server back to client.

Compile your code
```
mvn package
```

## **Executables**
... are a service based method

Start the server
```
java -cp target/project1-1.0-SNAPSHOT.jar Server
```

Run the client (change the localhost to hostname or IP address of the machine where you run the server)
```
java -cp target/project1-1.0-SNAPSHOT.jar Client <filename> <server IP address>
```
For ex, on my computer this would be:
java -cp target/project1-1.0-SNAPSHOT.jar Client affr.csv 172.18.0.2