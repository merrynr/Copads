package edu.rit.cs.rmi;

import java.rmi.*;
import java.util.List;



public interface EmployeeList extends Remote {

    Employee newPerson(Person p, Employee.Role r) throws RemoteException;

    List<Employee> getEmployeeList() throws RemoteException;

    int getEmployeeCount() throws RemoteException;

}
