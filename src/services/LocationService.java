package services;

import infrastructure.DbContext;
import models.Country;
import models.Customer;
import models.Division;
import models.DivisionReportItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * LocationService is a class to communicate with the DB for Divisions and Countries.
 * Rather than creating a separate class for each entity, the location class unifies
 * them since both divisions and countries will typically be used together and not separately.
 */
public class LocationService extends Service {


    public LocationService(Connection dbConnection) {
        super(dbConnection);
    }

    /**
     * Gets all countries in the DB
     *
     * @return a list of all countries. If an error occurred, an empty list is returned.
     */
    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM countries");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                countries.add(this.initCountry(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed getting countries from the database");
            System.out.println(e.getMessage());
        }
        return countries;
    }

    /**
     * Gets a list of first level divisions for a specific country
     *
     * @param countryId the ID of the country to filter for
     * @return a list of Division objects. If there was an error, the list will be empty
     */
    public List<Division> getDivisionsForCountry(int countryId) {
        List<Division> divisions = new ArrayList<>();

        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM first_level_divisions WHERE Country_ID = ?");
            statement.setInt(1, countryId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                divisions.add(this.initDivision(rs));
            }
        } catch (SQLException e) {
            System.out.println("Failed getting divisions from the database");
            System.out.println(e.getMessage());
        }

        return divisions;
    }

    /**
     * Gets a country by ID
     * @param countryId
     * @return the Country, or null if no country found or there was an error
     */
    public Country getCountryById(int countryId) {
        try {
            PreparedStatement  statement = _conn.prepareStatement("SELECT * FROM countries WHERE Country_ID = ?");
            statement.setInt(1, countryId);
            statement.setMaxRows(1);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return this.initCountry(rs);
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public List<DivisionReportItem> getDivisionReportItems(){
        List<DivisionReportItem> items = new ArrayList<>();
        try {
            PreparedStatement statement = _conn.prepareStatement("select Division, count(*) as Amount from customers c " +
                    "join first_level_divisions f " +
                    "on c.Division_ID = f.Division_ID " +
                    "group by Division");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                items.add(this.initDivisionReportItem(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return items;
    }
}
