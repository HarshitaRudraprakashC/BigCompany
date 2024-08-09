package main.java.employees;

import java.util.List;


public class Employee {

    private Long empId;
    private String firstName;
    private String lastName;
    private Double salary;
    private Long managerId;
    private List<Employee> reportees;

    public Employee(Long empId, String firstName, String lastName, Double salary, List<Employee> reportees) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.reportees = reportees;
    }

    public Employee(Long empId, String firstName, String lastName, Double salary,Long managerId, List<Employee> reportees) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.reportees = reportees;
        this.managerId = managerId;
    }

    public Employee() {
    }

    public Employee(Employee employee) {
    }

    public Employee(Long empId, String firstName, String lastName, Double salary,Long managerId) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.managerId = managerId;
    }

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public List<Employee> getReportees() {
        return reportees;
    }

    public void setReportees(List<Employee> reportees) {
        this.reportees = reportees;
    }


    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", salary=" + salary +
                ", managerId=" + managerId +
                ", reportees=" + reportees +
                '}';
    }
}
