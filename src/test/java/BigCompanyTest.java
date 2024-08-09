import main.java.employees.BigCompanyApplication;
import main.java.employees.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BigCompanyTest {

    private Employee ceo;
    private Employee manager1;
    private Employee manager2;
    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    public void setUp() {
// Initialize employees
        ceo = new Employee(123L, "CEO", "Joe", 150000.00, 0L);
        manager1 = new Employee(124L, "Manager1", "test", 100000.00, 123L);
        manager2 = new Employee(125L, "Manager2", "test", 95000.00, 123L);
        employee1 = new Employee(127L, "Employee1", "test", 80000.00, 124L);
        employee2 = new Employee(300L, "Employee2", "test", 75000.00, 125L);
    }

    @Test
    public void testOrganiseEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(ceo, manager2, manager1, employee1, employee2);
        Map<Long, Employee> empDir = employees.stream().collect(Collectors.toMap(Employee::getEmpId, employee -> employee));

        Employee testCeo = BigCompanyApplication.findCeo(employees);

        assertTrue(testCeo.getEmpId().equals(123L));
        assertFalse(testCeo.getSalary().equals(15000.00));

        List<Employee> orgEmpl = BigCompanyApplication.organiseEmployees(empDir, employees, testCeo);

        assertFalse(orgEmpl.contains(employee1));

    }


}
