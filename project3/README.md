# **Project 3**
Raft protocol implementation with a cluster of 5 node and a client.

Compile your code
```
mvn package
```

## **Executables**
If not already in project1 directory, cd project3

Start the server cluster

```
java -cp target/project3-1.0-SNAPSHOT.jar Server/Main
```

Run the client

```
java -cp target/project3-1.0-SNAPSHOT.jar Client/Client
```

(change the localhost to hostname or IP address of the machine where you run the server)

## **Notes**

A copy of each cluster node's log can be found at:

```
src/main/resources/results
```

(Project3's design architecture will be located next to this readme upon project completion.
Architecture.png)
