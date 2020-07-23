package edu.rit.cs.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Employee extends Remote, Serializable {
    enum Role {
        Researcher, Engineer, Sale, Undefined
    }

    int getEmployeeID() throws RemoteException;

    Role getEmployeeRole() throws RemoteException;

    Person getPersonObject() throws RemoteException;
}
