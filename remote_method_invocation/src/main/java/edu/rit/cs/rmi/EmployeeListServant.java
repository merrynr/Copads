package edu.rit.cs.rmi;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

class EmployeeServant implements Employee {
    Person _p;
    int _id;
    Employee.Role _role;

    public EmployeeServant(Person p, int id, Employee.Role r) {
        this._p=p;
        this._id=id;
        this._role=r;
    }

    @Override
    public int getEmployeeID() throws RemoteException {
        return this._id;
    }

    @Override
    public Employee.Role getEmployeeRole() throws RemoteException {
        return this._role;
    }

    @Override
    public Person getPersonObject() throws RemoteException {
        return null;
    }
}

public class EmployeeListServant extends UnicastRemoteObject implements EmployeeList {
    private List<Employee> employeeList = new ArrayList<Employee>();		 // contains the list of Employee

    public EmployeeListServant()throws RemoteException{

    }

    @Override
    public Employee newPerson(Person p, Employee.Role r) throws RemoteException {
        Employee employee = new EmployeeServant(p, employeeList.size()+1, r);
        employeeList.add(employee);
        return employee;
    }

    @Override
    public List<Employee> getEmployeeList() throws RemoteException {
        return this.employeeList;
    }

    @Override
    public int getEmployeeCount() throws RemoteException {
        return this.employeeList.size();
    }
}
