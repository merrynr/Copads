package edu.rit.cs.rmi;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class EmployeeListServer {
    public static void main(String args[]) throws RemoteException {
        System.setSecurityManager(new SecurityManager());

        try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            LocateRegistry.getRegistry();
            System.err.println("java RMI registry already exists.");
        }

        try{
            EmployeeList aEmployeeList = new EmployeeListServant();
            Naming.rebind("EmployeeList", aEmployeeList );
            System.out.println("EmployeeList server ready");
        }catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
