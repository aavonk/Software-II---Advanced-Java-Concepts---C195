package services;

import models.Country;
import models.Customer;
import models.Division;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * CustomerService is responsible for database operations specific to the Customer entity.
 */
public class CustomerService extends Service {
    private final AppointmentService _appointmentService;

    public CustomerService(Connection dbConnection, AppointmentService appointmentService) {
        super(dbConnection);
        _appointmentService = appointmentService;
    }

    /**
     * Gets a list of all the customers in the DB
     *
     * @return a list of customers. If there is an error, an empty list will be returned.
     */
    public List<Customer> getAllCustomers() {
        try {

            PreparedStatement stmt = _conn.prepareStatement("SELECT * FROM customers AS cust\n" +
                    "JOIN first_level_divisions AS f on cust.Division_ID = f.Division_ID\n" +
                    "JOIN countries as c ON f.Country_ID = c.Country_ID;");
            ResultSet rs = stmt.executeQuery();
            List<Customer> customers = new ArrayList<Customer>();

            while (rs.next()) {
                Country country = this.initCountry(rs);
                Division division = this.initDivision(rs);
                Customer customer = this.initCustomer(rs);

                division.setCountry(country);
                customer.setDivision(division);

                customers.add(customer);
            }

            return customers;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new ArrayList<Customer>();
        }

    }

    /**
     * Saves a customer to the database
     *
     * @param customer the object to save
     * @throws SQLException - throws an exception if unable to save to DB
     */
    public void createCustomer(Customer customer) throws SQLException {
        PreparedStatement statement = _conn.prepareStatement("INSERT INTO customers " +
                "(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)" +
                " VALUES (?,?,?,?,?,?,?,?,?)");
        statement.setString(1, customer.getCustomerName());
        statement.setString(2, customer.getAddress());
        statement.setString(3, customer.getPostalCode());
        statement.setString(4, customer.getPhoneNumber());
        statement.setTimestamp(5, new Timestamp(customer.getCreateDate().getTime()));
        statement.setString(6, customer.getCreatedBy());
        statement.setTimestamp(7, new Timestamp(customer.getLastUpdate().getTime()));
        statement.setString(8, customer.getLastUpdatedBy());
        statement.setInt(9, customer.getDivisionId());

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected != 1) {
            throw new SQLException("unsuccessful creation of customer");
        }
    }

    /**
     * Deletes a customer from the database. Before deletion, the customers
     * appointments are deleted.
     *
     * @param customer the customer to delete
     */
    public void deleteCustomer(Customer customer) throws SQLException {
        _appointmentService.deleteAppointmentsForCustomer(customer.getId());

        PreparedStatement statement = _conn.prepareStatement("DELETE FROM customers WHERE Customer_ID = ?");
        statement.setInt(1, customer.getId());

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected != 1) {
            throw new SQLException(String.format("Query effected %d rows", rowsAffected));
        }
    }

    /**
     * Updates a customer record in the database
     *
     * @param customer the customer to update
     * @throws SQLException -
     */
    public void updateCustomer(Customer customer) throws SQLException {
        PreparedStatement statement = _conn.prepareStatement("UPDATE customers\n" +
                "SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By =  ?, Last_Update = NOW(), Last_Updated_By = 'application', Division_Id=?\n" +
                "WHERE Customer_Id = ?;");

        statement.setString(1, customer.getCustomerName());
        statement.setString(2, customer.getAddress());
        statement.setString(3, customer.getPostalCode());
        statement.setString(4, customer.getPhoneNumber());
        statement.setDate(5, new Date(customer.getCreateDate().getTime()));
        statement.setString(6, customer.getCreatedBy());
        statement.setInt(7, customer.getDivisionId());
        statement.setInt(8, customer.getId());

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected != 1) {
            throw new SQLException(String.format("updateCustomer query affected %d rows", rowsAffected));
        }

    }

}
