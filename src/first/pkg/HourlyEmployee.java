package first.pkg;

import java.util.Objects;

/**
 * Represents an employee paid by the hour.
 * This class extends the Employee abstract class and includes additional properties specific to hourly employees.
 */
public class HourlyEmployee extends Employee {

    private int hours;
    private float wage;

    /**
     * Default constructor that initializes the hourly employee with default values for hours and wage.
     */
    public HourlyEmployee() {
        hours = 0;
        wage = 0.0f;
    }

    /**
     * Parameterized constructor that initializes the hourly employee with provided values for name, ID, hours, and wage.
     *
     * @param firstName The first name of the employee.
     * @param lastName  The last name of the employee.
     * @param id        The identification number of the employee.
     * @param hours     The number of hours worked by the employee.
     * @param wage      The wage per hour for the employee.
     */
    public HourlyEmployee(final String firstName, final String lastName, final int id, final int hours, final float wage) {
        super(firstName, lastName, id);
        setHours(hours);
        setWage(wage);
    }

    /**
     * Gets the number of hours worked by the employee.
     *
     * @return The number of hours worked.
     */
    public int getHours() {
        return hours;
    }

    /**
     * Gets the wage per hour for the employee.
     *
     * @return The wage per hour.
     */
    public float getWage() {
        return wage;
    }

    /**
     * Sets the wage per hour for the employee after validating its range.
     *
     * @param wage The wage per hour to set.
     * @throws IllegalArgumentException If the wage is not within the valid range.
     */
    private void setWage(float wage) {
        if (wage > 999 || wage < 0)
            throw new IllegalArgumentException("Invalid wage.");
        this.wage = wage;
    }

    /**
     * Sets the number of hours worked by the employee after validating its range.
     *
     * @param hours The number of hours worked to set.
     * @throws IllegalArgumentException If the number of hours is not within the valid range.
     */
    private void setHours(int hours) {
        if (hours > 75 || hours < 0)
            throw new IllegalArgumentException("Invalid Hours worked.");
        this.hours = hours;
    }

    /**
     * Calculates the earnings for the hourly employee based on the hours worked and the wage.
     *
     * @return The total earnings for the hourly employee.
     */
    @Override
    public float earnings() {
        return hours * wage;
    }

    /**
     * Compares this hourly employee to the specified object for equality.
     *
     * @param obj The object to compare with.
     * @return true if the given object represents an HourlyEmployee equivalent to this employee, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof HourlyEmployee other
                && super.equals(other) && hours == other.hours && wage == other.wage;
    }

    /**
     * Returns a string representation of the hourly employee's information, including hours worked and wage.
     *
     * @return A string representation of the hourly employee.
     */
    @Override
    public String toString() {
        return super.toString() + "\nWork Hours: " + hours + "\nWage Per Hour: " + wage;
    }

    /**
     * Computes the hash code for this hourly employee.
     *
     * @return A hash code value for this hourly employee.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hours, wage);
    }
}
