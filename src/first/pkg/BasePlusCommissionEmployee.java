package first.pkg;

import java.util.Objects;

/**
 * Represents an employee who earns a base salary in addition to commission based on their gross sales.
 * This class extends the CommissionEmployee class and includes an additional property for the base salary.
 */
public class BasePlusCommissionEmployee extends CommissionEmployee {

    private int baseSalary;

    /**
     * Default constructor that initializes the base plus commission employee with default values for base salary.
     */
    public BasePlusCommissionEmployee() {
        super();
        baseSalary = 0;
    }

    /**
     * Parameterized constructor that initializes the base plus commission employee with provided values for name, ID, commission percentage, gross sales, and base salary.
     *
     * @param firstName   The first name of the employee.
     * @param lastName    The last name of the employee.
     * @param id          The identification number of the employee.
     * @param commission  The commission percentage of the employee's sales.
     * @param grossSales  The gross sales amount for the employee.
     * @param baseSalary  The base salary for the employee.
     */
    public BasePlusCommissionEmployee(final String firstName, final String lastName, final int id, final int commission, final float grossSales, final int baseSalary) {
        super(firstName, lastName, id, commission, grossSales);
        setBaseSalary(baseSalary);
    }

    /**
     * Gets the base salary for the employee.
     *
     * @return The base salary.
     */
    public int baseSalary() {
        return baseSalary;
    }

    /**
     * Sets the base salary for the employee after validating it is not negative.
     *
     * @param baseSalary The base salary to set.
     * @throws IllegalArgumentException If the base salary is negative.
     */
    private void setBaseSalary(final int baseSalary) {
        if (baseSalary < 0) {
            throw new IllegalArgumentException("Invalid Base Salary.");
        }
        this.baseSalary = baseSalary;
    }

    /**
     * Calculates the earnings for the base plus commission employee based on the base salary, commission percentage, and gross sales.
     *
     * @return The total earnings for the base plus commission employee.
     */
    @Override
    public float earnings() {
        return super.earnings() + baseSalary;
    }

    /**
     * Compares this base plus commission employee to the specified object for equality.
     *
     * @param obj The object to compare with.
     * @return true if the given object represents a BasePlusCommissionEmployee equivalent to this employee, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof BasePlusCommissionEmployee other
                && super.equals(other) && baseSalary == other.baseSalary;
    }

    /**
     * Returns a string representation of the base plus commission employee's information, including base salary.
     *
     * @return A string representation of the base plus commission employee.
     */
    @Override
    public String toString() {
        return super.toString() + "\nBase Salary: " + baseSalary;
    }

    /**
     * Computes the hash code for this base plus commission employee.
     *
     * @return A hash code value for this base plus commission employee.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), baseSalary);
    }
}
