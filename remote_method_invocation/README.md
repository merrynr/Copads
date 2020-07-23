## Remote Method Invocation
Note, the RMI Registry is on the server program in this example. 

More information on RMI, please see https://docs.oracle.com/javase/tutorial/rmi/TOC.html

### Launching docker containers for the RMI example
To start rmiserver and rmiclient and rebuild the docker image
```bash
docker-compose up --build rmiserver rmiclient 
```
After awhile you should see the following output
```bash
...
...
Successfully tagged csci251:latest
Recreating peer1 ... done
Recreating rmiserver ... done
Recreating rmiclient ... done
Attaching to rmiserver, rmiclient
rmiserver    | Initialize rmiserver...done!
rmiclient    | Initialize rmiclient...done!
```

### Start RMI server
Attach to the rmiserver container
```bash
docker exec -it rmiserver bash
```

Run the rmiserver program
```bash
java -cp target/remote_method_invocation-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy  edu.rit.cs.rmi.EmployeeListServer
```

Expected output
```bash
java RMI registry created.
EmployeeList server ready
```

### Start RMI client
Attach to the rmiclient container
```bash
docker exec -it rmiclient bash
```

Run the rmiclient program
```bash
java -cp target/remote_method_invocation-1.0-SNAPSHOT.jar -Djava.security.policy=rmi.policy  edu.rit.cs.rmi.EmployeeListClient
```

Expected output
```bash
Total number of employee: 3
```


