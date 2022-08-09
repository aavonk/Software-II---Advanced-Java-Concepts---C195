package services;

import models.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contact service handles all database operations for the contacts table.
 */
public class ContactService extends Service{
    public ContactService(Connection dbConnection) {
        super(dbConnection);
    }

    /**
     * Gets all contacts found in the database
     * @return a List of contacts.
     */
    public List<Contact> getAllContacts(){
        List<Contact> contacts = new ArrayList<>();

        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM contacts;");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Contact c = this.initContact(rs);
                contacts.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return contacts;
    }
}
