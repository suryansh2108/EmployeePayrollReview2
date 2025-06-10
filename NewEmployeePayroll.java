import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// === Abstract Employee Class ===
abstract class Employee {
    private String name;
    private int id;

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() { return name; }
    public int getId() { return id; }

    public abstract double calculateSalary();

    @Override
    public String toString() {
        return String.format("Employee [Name: %s, ID: %d, Salary: %.2f]", name, id, calculateSalary());
    }
}

// === FullTimeEmployee ===
class FullTimeEmployee extends Employee {
    private double monthlySalary;

    public FullTimeEmployee(String name, int id, double monthlySalary) {
        super(name, id);
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}

// === PartTimeEmployee ===
class PartTimeEmployee extends Employee {
    private int hoursWorked;
    private double hourlyRate;

    public PartTimeEmployee(String name, int id, int hoursWorked, double hourlyRate) {
        super(name, id);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }
}

// === Payroll System ===
class PayrollSystem {
    private List<Employee> employeeList = new ArrayList<>();

    public void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public void removeEmployee(int id) {
        employeeList.removeIf(emp -> emp.getId() == id);
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public boolean employeeExists(int id) {
        return employeeList.stream().anyMatch(emp -> emp.getId() == id);
    }
}

// === GUI Application ===
public class EmployeePayrollGUI extends JFrame {
    private PayrollSystem payrollSystem = new PayrollSystem();
    private JTextArea displayArea;

    public EmployeePayrollGUI() {
        setTitle("Employee Payroll System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Buttons
        JButton addButton = new JButton("Add Employee");
        JButton removeButton = new JButton("Remove Employee");
        JButton displayButton = new JButton("Display Employees");

        JPanel topPanel = new JPanel();
        topPanel.add(addButton);
        topPanel.add(removeButton);
        topPanel.add(displayButton);
        add(topPanel, BorderLayout.NORTH);

        // Text Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // ActionListeners
        addButton.addActionListener(e -> showAddEmployeeDialog());
        removeButton.addActionListener(e -> showRemoveEmployeeDialog());
        displayButton.addActionListener(e -> displayEmployees());

        setVisible(true);
    }

    private void showAddEmployeeDialog() {
        String[] options = {"Full-Time", "Part-Time"};
        int choice = JOptionPane.showOptionDialog(this, "Select Employee Type", "Employee Type",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        try {
            String name = JOptionPane.showInputDialog(this, "Enter Name:");
            if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");

            String idStr = JOptionPane.showInputDialog(this, "Enter ID:");
            int id = Integer.parseInt(idStr);

            if (payrollSystem.employeeExists(id)) {
                JOptionPane.showMessageDialog(this, "Employee with this ID already exists.");
                return;
            }

            if (choice == 0) { // Full-Time
                String salaryStr = JOptionPane.showInputDialog(this, "Enter Monthly Salary:");
                double salary = Double.parseDouble(salaryStr);

                if (salary < 0) throw new IllegalArgumentException("Salary cannot be negative");

                payrollSystem.addEmployee(new FullTimeEmployee(name, id, salary));

            } else if (choice == 1) { // Part-Time
                String hoursStr = JOptionPane.showInputDialog(this, "Enter Hours Worked:");
                String rateStr = JOptionPane.showInputDialog(this, "Enter Hourly Rate:");

                int hours = Integer.parseInt(hoursStr);
                double rate = Double.parseDouble(rateStr);

                if (hours < 0 || rate < 0) throw new IllegalArgumentException("Values must be positive");

                payrollSystem.addEmployee(new PartTimeEmployee(name, id, hours, rate));
            }

            displayEmployees();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric input.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showRemoveEmployeeDialog() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Employee ID to Remove:");
            int id = Integer.parseInt(idStr);

            if (!payrollSystem.employeeExists(id)) {
                JOptionPane.showMessageDialog(this, "Employee ID not found.");
                return;
            }

            payrollSystem.removeEmployee(id);
            displayEmployees();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.");
        }
    }

    private void displayEmployees() {
        displayArea.setText("");
        for (Employee emp : payrollSystem.getEmployeeList()) {
            displayArea.append(emp.toString() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeePayrollGUI::new);
    }
}
