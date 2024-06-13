package first.pkg;

import java.util.Objects;

/**
 * Represents an abstract concept of an employee with basic personal information.
 * This class serves as a base for all specific types of employees.
 */
public abstract class Employee {
    private String firstName;
    private String lastName;
    private int id;

    /**
     * Default constructor that initializes the employee with default values.
     */
    public Employee() {
        firstName = "plony";
        lastName = "almony";
        id = 0;
    }

    /**
     * Parameterized constructor that initializes the employee with provided values.
     *
     * @param firstName The first name of the employee.
     * @param lastName  The last name of the employee.
     * @param id        The identification number of the employee.
     */
    public Employee(final String firstName, final String lastName, final int id) {
        setFirstName(firstName);
        setLastName(lastName);
        setId(id);
    }

    /**
     * Sets the first name of the employee after validating its length.
     *
     * @param firstName The first name to set.
     * @throws IllegalArgumentException If the first name does not meet length requirements.
     */
    private void setFirstName(final String firstName) {
        if (firstName.length() < 3)
            throw new IllegalArgumentException("First Name must be at least 3 characters long.");
        if (firstName.length() > 20)
            throw new IllegalArgumentException("First Name must be at most 20 characters.");
        this.firstName = firstName;
    }

    /**
     * Sets the last name of the employee after validating its length.
     *
     * @param newLastName The last name to set.
     * @throws IllegalArgumentException If the last name does not meet length requirements.
     */
    private void setLastName(final String newLastName) {
        if (newLastName.length() < 3)
            throw new IllegalArgumentException("Last Name must be at least 3 characters long");
        if (newLastName.length() > 20)
            throw new IllegalArgumentException("Last Name must be at most 20 characters.");
        lastName = newLastName;
    }

    /**
     * Sets the identification number of the employee after validating its range.
     *
     * @param newID The identification number to set.
     * @throws IllegalArgumentException If the identification number is not valid.
     */
    final protected void setId(final int newID) {
        if (newID <= 100000000 || newID > 999999999) {
            throw new IllegalArgumentException("Invalid ID.");
        }
        id = newID;
    }

    /**
     * Gets the first name of the employee.
     *
     * @return The first name of the employee.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the employee.
     *
     * @return The last name of the employee.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the identification number of the employee.
     *
     * @return The identification number of the employee.
     */
    public int getId() {
        return id;
    }

    /**
     * Abstract method to calculate the earnings of the employee.
     *
     * @return The earnings of the employee.
     */
    public abstract float earnings();

    /**
     * Returns a string representation of the employee's basic information.
     *
     * @return A string representation of the employee.
     */
    @Override
    public String toString() {
        return "ID: " + id + "\nFirst Name:  " + firstName + "\nLast Name:  " + lastName;
    }

    /**
     * Compares this employee to the specified object for equality.
     *
     * @param obj The object to compare with.
     * @return true if the given object represents an Employee equivalent to this employee, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Employee other
                && id == other.id && firstName.equals(other.firstName) && lastName.equals(other.lastName);
    }

    /**
     * Computes the hash code for this employee.
     *
     * @return A hash code value for this employee.
     */
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, id);
    }
}
