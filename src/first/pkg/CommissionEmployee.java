package first.pkg;

import java.util.Objects;

/**
 * Represents an employee who earns a commission based on their gross sales.
 * This class extends the Employee abstract class and includes properties specific to commission-based earnings.
 */
public class CommissionEmployee extends Employee {

    private int commission;
    private float grossSales;

    /**
     * Default constructor that initializes the commission employee with default values for commission and gross sales.
     */
    public CommissionEmployee() {
        commission = 0;
        grossSales = 0.0f;
    }

    /**
     * Parameterized constructor that initializes the commission employee with provided values for name, ID, commission percentage, and gross sales.
     *
     * @param firstName   The first name of the employee.
     * @param lastName    The last name of the employee.
     * @param id          The identification number of the employee.
     * @param commission  The commission percentage of the employee's sales.
     * @param grossSales  The gross sales amount for the employee.
     */
    public CommissionEmployee(final String firstName, final String lastName, final int id, final int commission, final float grossSales) {
        super(firstName, lastName, id);
        setCommission(commission);
        setGrossSales(grossSales);
    }

    /**
     * Gets the commission percentage of the employee's sales.
     *
     * @return The commission percentage.
     */
    public int getCommission() {
        return commission;
    }

    /**
     * Gets the gross sales amount for the employee.
     *
     * @return The gross sales amount.
     */
    public float getGrossSales() {
        return grossSales;
    }

    /**
     * Sets the gross sales amount for the employee after validating it is not negative.
     *
     * @param grossSales The gross sales amount to set.
     * @throws IllegalArgumentException If the gross sales amount is negative.
     */
    private void setGrossSales(float grossSales) {
        if (grossSales < 0) {
            throw new IllegalArgumentException("Invalid GrossSales.");
        }
        this.grossSales = grossSales;
    }

    /**
     * Sets the commission percentage for the employee after validating it is within the valid range.
     *
     * @param commission The commission percentage to set.
     * @throws IllegalArgumentException If the commission percentage is not within the valid range.
     */
    private void setCommission(int commission) {
        if (commission > 75 || commission < 0) {
            throw new IllegalArgumentException("Invalid Commission.");
        }
        this.commission = commission;
    }

    /**
     * Calculates the earnings for the commission employee based on the commission percentage and gross sales.
     *
     * @return The total earnings for the commission employee.
     */
    @Override
    public float earnings() {
        return commission / 100f * grossSales;
    }

    /**
     * Compares this commission employee to the specified object for equality.
     *
     * @param obj The object to compare with.
     * @return true if the given object represents a CommissionEmployee equivalent to this employee, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof CommissionEmployee other
                && super.equals(other) && grossSales == other.grossSales && commission == other.commission;
    }

    /**
     * Returns a string representation of the commission employee's information, including commission percentage and gross sales.
     *
     * @return A string representation of the commission employee.
     */
    @Override
    public String toString() {
        return super.toString() + "\nCommission Percentage: " + commission + '%' + "\nGross Sales: " + grossSales;
    }

    /**
     * Computes the hash code for this commission employee.
     *
     * @return A hash code value for this commission employee.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commission, grossSales);
    }
}
