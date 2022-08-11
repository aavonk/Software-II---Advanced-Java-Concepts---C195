package services;

import models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Base class for Services that interact with the database.
 * This class provides methods for mapping a result set returned from a database call
 * into an internal application type.
 */
public abstract class Service {
    protected final Connection _conn;

    public Service(Connection dbConnection) {
        this._conn = dbConnection;
    }

    /**
     * Maps a result set into a Country object
     * @param rs the result set to map
     * @return Country
     */
    protected Country initCountry(ResultSet rs) {
        Country country = new Country();
        try {
            country.setCountryId(rs.getInt("Country_ID"));
            country.setName(rs.getString("Country"));
            country.setCreateDate(rs.getDate("Create_Date"));
            this.initBaseEntityProperties(country, rs);
        } catch (SQLException e) {
            System.out.println("Failed binding query results to Country object");
            System.out.println(e.getMessage());
        }

        return country;
    }

    /**
     * Maps a result set into a Division object
     * @param rs the result set to map
     * @return Division
     */
    protected Division initDivision(ResultSet rs) {
        Division division = new Division();
        try {
            division.setId(rs.getInt("Division_Id"));
            division.setDivision(rs.getString("Division"));
            division.setCountryId(rs.getInt("Country_ID"));
            this.initBaseEntityProperties(division, rs);
        } catch (SQLException e) {
            System.out.println("Failed binding query results to Division object");
            System.out.println(e.getMessage());
        }
        return division;
    }

    /**
     * Maps a result set into a Customer object
     * @param rs the result set to map
     * @return Customer
     */
    protected Customer initCustomer(ResultSet rs) {
        Customer customer = new Customer();
        try {
            customer.setId(rs.getInt("Customer_ID"));
            customer.setCustomerName(rs.getString("Customer_Name"));
            customer.setAddress(rs.getString("Address"));
            customer.setPostalCode(rs.getString("Postal_Code"));
            customer.setPhoneNumber(rs.getString("Phone"));
            customer.setDivisionId(rs.getInt("Division_Id"));
            this.initBaseEntityProperties(customer, rs);
        } catch (SQLException e) {
            System.out.println("Failed binding query results to Customer object");
            System.out.println(e.getMessage());
        }
        return customer;
    }

    /**
     * Maps a result set into a Appointment object
     * @param rs the result set to map
     * @return an Appointment
     */
    protected Appointment initAppointment(ResultSet rs) {
        Appointment appointment = new Appointment();
        try {

            appointment.setId(rs.getInt("Appointment_ID"));
            appointment.setTitle(rs.getString("Title"));
            appointment.setDescription(rs.getString("Description"));
            appointment.setLocation(rs.getString("Location"));
            appointment.setType(rs.getString("Type"));
            appointment.setStart(this.fromUtcTime(rs.getTimestamp("Start").toLocalDateTime()));
            appointment.setEnd(this.fromUtcTime(rs.getTimestamp("End").toLocalDateTime()));
            appointment.setCustomerId(rs.getInt("Customer_ID"));
            appointment.setUserId(rs.getInt("User_ID"));
            appointment.setContactId(rs.getInt("Contact_ID"));
            this.initBaseEntityProperties(appointment, rs);
        } catch (SQLException e) {
            System.out.println("Failed binding query results to Appointment object");
            System.out.println(e.getMessage());
        }
        return appointment;
    }

    /**
     * Maps a result set into a Contact object
     * @param rs the result set to map
     * @return -
     */
    protected Contact initContact(ResultSet rs) {
        Contact contact = new Contact();
        try {
            contact.setId(rs.getInt("Contact_ID"));
            contact.setName(rs.getString("Contact_Name"));
            contact.setEmail(rs.getString("Email"));
        } catch (SQLException e) {
            System.out.println("Failed binding query results to Contact object");
            System.out.println(e.getMessage());
        }
        return contact;
    }

    /**
     * Maps a result set into a AppointmentTypeItem object
     * @param rs the result set to map
     * @return AppointmentTypeItem
     */
    protected AppointmentTypeItem initAppointmentTypeItem(ResultSet rs) {
        AppointmentTypeItem item = new AppointmentTypeItem();
        try {
            item.setType(rs.getString("Type"));
            item.setAmount(rs.getInt("Amount"));
            item.setMonth(rs.getString("Month"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return item;
    }

    /**
     * Maps a result set into a DivisionReportItem object
     * @param rs the result set to map
     * @return DivisionReportItem
     */
    protected DivisionReportItem initDivisionReportItem(ResultSet rs) {
        DivisionReportItem item = new DivisionReportItem();
        try {
            item.setDivision(rs.getString("Division"));
            item.setAmount(rs.getInt("Amount"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return item;
    }

    private void initBaseEntityProperties(BaseEntity entity, ResultSet rs) {
        try {
            entity.setCreateDate(rs.getDate("Create_Date"));
            entity.setCreatedBy(rs.getString("Created_By"));
            entity.setLastUpdate(rs.getTimestamp("Last_Update"));
            entity.setLastUpdatedBy(rs.getString("Last_Updated_By"));
        } catch (SQLException e) {
            System.out.println("Failed to bind query results to base entity object");
            System.out.println(e.getMessage());
        }
    }


    private LocalDateTime fromUtcTime(LocalDateTime date) {
        ZonedDateTime zdtOut = date.atZone(ZoneId.of("UTC"));
        ZonedDateTime zdtOutToLocalTZ = zdtOut.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        return zdtOutToLocalTZ.toLocalDateTime();
    }
}
