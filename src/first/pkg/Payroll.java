package first.pkg;

import java.util.ArrayList;

/**
 * The Payroll class is responsible for calculating and displaying the earnings of various types of employees.
 * It demonstrates the use of polymorphism, exception handling, and the ArrayList collection.
 */
public class Payroll {
    /**
     * default constructor
     */
    public Payroll(){}

    /**
     * The main method is the entry point of the program. It creates a list of employees, calculates their earnings,
     * and prints the information to the console. It also handles any runtime exceptions that may occur during execution.
     *
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        try {
            ArrayList<Employee> allEmployees = new ArrayList<>();
            // Adding different types of employees to the list
            allEmployees.add(new HourlyEmployee("Benny", "Avrahami", 254985241, 55, 78.5f));
            allEmployees.add(new CommissionEmployee("Dan", "Zilberstein", 974985524, 25, 250031.89f));
            allEmployees.add(new BasePlusCommissionEmployee("Benny", "Avrahami", 254985241, 15, 90857.59f, 5021));
            // Adding default employees
            allEmployees.add(new HourlyEmployee());
            allEmployees.add(new CommissionEmployee());
            allEmployees.add(new BasePlusCommissionEmployee());

            String line = "\n------------------------------------\n";

            // Iterating over the list of employees and printing their details
            for (Employee emp : allEmployees) {
                System.out.println(line + emp + "\nTotal Salary: ");
                // Applying a bonus to BasePlusCommissionEmployee
                if (emp instanceof BasePlusCommissionEmployee)
                    System.out.println(emp.earnings() * 1.1);
                else
                    System.out.println(emp.earnings());
                System.out.println(line);
            }

        } catch (RuntimeException e) {
            // Handling runtime exceptions
            System.out.println(e + "\t(Runtime Exception)");
        } catch (Exception e) {
            // Handling general exceptions
            System.out.println(e + "\t(Exception)");
        } finally {
            // Final block to execute regardless of exception occurrence
            System.out.println("Good By");
        }
    }
}
