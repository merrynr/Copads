# **Project 1**
Send a parsed file from one machine (client) to another machine (server) over a socket to do word count.
Then return results from server back to client.

Compile your code
```
mvn package
```

## **Executables**
If not already in project1 directory, cd project1

Start the server

```
java -cp target/project1-1.0-SNAPSHOT.jar Server
```

Run the client

```
java -cp target/project1-1.0-SNAPSHOT.jar Client <filename> <server IP address>
```

(change the localhost to hostname or IP address of the machine where you run the server)

## **Notes**
Please ensure your inputted csv file is in the resources directory:

```
project1/src/main/resources/
```
In addition, a copy of the results on client-side can also be found in:

```
src/main/resources/results
```

Project1's design architecture is located next to this README.md as:

```
Architecture.png
```
