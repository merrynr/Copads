package edu.rit.cs.rmi;

import java.rmi.*;

public class EmployeeListClient {

    public static void main(String args[]){
        System.setSecurityManager(new SecurityManager());

        EmployeeList aEmployeeList = null;
        try{
            aEmployeeList  = (EmployeeList) Naming.lookup("rmi://rmiserver:1099/EmployeeList");

            aEmployeeList.newPerson(new Person("alice", 23), Employee.Role.Engineer);

            aEmployeeList.newPerson(new Person("bob", 25), Employee.Role.Researcher);

            aEmployeeList.newPerson(new Person("charlie", 27), Employee.Role.Sale);

            System.out.println("Total number of employee: " + aEmployeeList.getEmployeeCount());
        } catch(RemoteException e) {
            System.out.println(e.getMessage());
        }catch(Exception e) {
            System.out.println("Client: " + e.getMessage());
        }
    }

}

