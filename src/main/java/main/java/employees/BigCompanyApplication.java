package main.java.employees;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;

public class BigCompanyApplication {
    public static void main(String[] args) throws Exception {
//           "C:\Users\harsh\OneDrive\Documents\Employees.csv"
        System.out.println("Enter the file name and path. Eg: C:\\Documents\\Employees.csv");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String filePath = reader.readLine();

        if (Objects.nonNull(filePath) && !filePath.isEmpty()) {

            /*Extract employees data from csv*/
            List<Employee> employees = extractEmployeeData(filePath);
            if (employees.isEmpty()) {
                System.err.println("No data in csv");
                throw new Exception();
            }

            /*Create an employee directory with empId for reference*/
            Map<Long, Employee> empDir = employees.stream().collect(Collectors.toMap(Employee::getEmpId, employee -> employee));

            /*Determine CEO of the company*/
            Employee ceo = findCeo(employees);

            /*Organise the employees - company hierarchy*/
            List<Employee> employeeHierarchy = organiseEmployees(empDir, employees, ceo);

            /*Evaluate manager salary against their team*/
            evaluateManagerSalary(employeeHierarchy);

            /*Employees with more than 4 managers*/
            evaluateEmployeeReporting(ceo);
        } else {
            System.err.println("Invalid File Path or cannot find the File");
        }

    }


    /*
     * This method extracts the CEO of the company from the employees list
     * */
    public static Employee findCeo(List<Employee> employees) throws Exception {
        List<Employee> companyCEO = employees.stream()
                .filter(employee -> employee.getManagerId() == 0)
                .toList();
        if (companyCEO.isEmpty()) {
            System.err.println("No CEO found for the organisation");
            throw new Exception();
        } else if (companyCEO.size() > 1) {
            System.err.println("Organisation has more than 1 CEO");
            throw new Exception();
        }
        return companyCEO.get(0);
    }


    /*
     * This method organises the employees as manager and reportees for company hierarchy
     * */
    public static List<Employee> organiseEmployees(Map<Long, Employee> empDir, List<Employee> employees, Employee ceo) {
        List<Employee> organisedEmp = new ArrayList<>();

        Map<Long, List<Employee>> managerTeams = employees.stream()
                .filter(employee -> employee.getManagerId() != 0)
                .collect(Collectors.groupingBy(Employee::getManagerId));

        managerTeams.forEach((empId, reportees) -> {
            Employee manager = empDir.get(empId);
            manager.setReportees(reportees);
            empDir.put(empId, manager); // Update employee directory

            organisedEmp.add(new Employee(manager.getEmpId(), manager.getFirstName(), manager.getLastName(), manager.getSalary(), manager.getManagerId(), reportees));
        });

        ceo.setReportees(managerTeams.get(ceo.getEmpId())); // managers reporting to CEO

        return organisedEmp;
    }


    /*
     * This method determines the list of employees with reporting line of more than 4 managers between them and CEO
     * */
    private static void evaluateEmployeeReporting(Employee ceo) {
        List<Employee> employeesWithMoreThanFourManagers = new ArrayList<>();

        // Perform a depth-first search to identify employees with more than 4 managers
        findEmployees(ceo, employeesWithMoreThanFourManagers, 0);

        System.out.println(" Employees with than 4 managers between them and the CEO.");
        for (Employee employee : employeesWithMoreThanFourManagers) {
            System.out.println("EmpId:" + employee.getEmpId() + ", name: " + employee.getFirstName() + " " + employee.getLastName());
        }
    }


    private static void findEmployees(Employee manager, List<Employee> result, int depth) {
        if (Objects.nonNull(manager.getReportees()) && !manager.getReportees().isEmpty()) {
            for (Employee reportee : manager.getReportees()) {
                if (depth > 4) {
                    result.add(reportee);
                }
                findEmployees(reportee, result, depth + 1);
            }
        }
    }


    /*
     * This method calculates the average salary of a given manager's team to compare and categorise managers having salary
     * less or more than their team's average salary
     * */
    private static void evaluateManagerSalary(List<Employee> managerTeams) {

        Map<Long, Double> avgTeamSalary = managerTeams.stream()
                .filter(mgr -> !mgr.getReportees().isEmpty())
                .collect(Collectors.toMap(Employee::getEmpId,
                        manager -> manager.getReportees().stream()
                                .mapToDouble(Employee::getSalary)
                                .average().getAsDouble()));

        Map<Long, Double> highMgrSalary = new HashMap<>();
        Map<Long, Double> lowMgrSalary = new HashMap<>();

        managerTeams.forEach(employee -> {

            if (!employee.getReportees().isEmpty()) {
                Long managerId = employee.getEmpId();
                double managerSalary = employee.getSalary();
                double teamAvgSalary = avgTeamSalary.get(managerId);

                double MIN_SALARY_PERCENT = teamAvgSalary * 1.2; // 20% more than the average
                double MAX_SALARY_PERCENT = teamAvgSalary * 1.5; // 50% more than the average

                if (managerSalary < MIN_SALARY_PERCENT) {
                    lowMgrSalary.put(managerId, MIN_SALARY_PERCENT- managerSalary);
                } else if (managerSalary > MAX_SALARY_PERCENT) {
                    highMgrSalary.put(managerId, (managerSalary - MAX_SALARY_PERCENT));
                }

            }

        });

        System.out.println("List of Manager Employee Ids with high salary and their salary reduction amount: \n ");
        highMgrSalary.forEach((mgrId, salaryPer) -> System.out.println("Employee Id:" + mgrId + ", reduction by: " + salaryPer + "\n"));

        System.out.println("List of Manager Employee Ids with less salary and their salary hike amount: \n");
        lowMgrSalary.forEach((mgrId, salaryPer) -> System.out.println("Employee Id:" + mgrId + ", hike by: " + salaryPer + "\n"));

    }


    /*
     * This method extracts the data of all employees from CSV file */
    private static List<Employee> extractEmployeeData(String filePath) throws Exception {

        List<Employee> employees = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                Employee employee = new Employee();

                String empId = record.get("Id");
                if (isNotEmpty(empId)) {
                    employee.setEmpId(Long.valueOf(empId));
                    employee.setFirstName(record.get("firstName"));
                    employee.setLastName(record.get("lastName"));
                    employee.setSalary(record.get("salary").isEmpty() ? 0 : Double.parseDouble(record.get("salary")));
                    employee.setManagerId(record.get("managerId").isEmpty() ? 0 : Long.parseLong(record.get("managerId")));

                    employees.add(employee);
                }
            }

        } catch (IOException e) {
            System.err.println("Error while reading CSV file");
            throw new Exception();
        }
        return employees;
    }


}