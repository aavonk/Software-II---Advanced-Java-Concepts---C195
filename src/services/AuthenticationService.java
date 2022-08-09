package services;

import infrastructure.DbContext;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/*
    AuthenticationService is responsible for determining whether a user is
    considered authenticated. It communicates with the database to see if the credentials
    stored are equal to the ones provided.
 */
public class AuthenticationService extends Service {


    public AuthenticationService(Connection dbConnection) {
        super(dbConnection);
    }

    /**
     * Gets a user from the database that has the matching userId and password.
     * @param userId the UserId of the user
     * @param password a password of the account
     * @return a User object if successful, null otherwise.
     * @throws SQLException  -
     */
    public User getUser(int userId, String password) throws SQLException {
        PreparedStatement stmt = _conn.prepareStatement("SELECT * FROM client_schedule.users WHERE User_ID = ? AND Password = ?");
        stmt.setInt(1, userId);
        stmt.setString(2, password);
        stmt.setMaxRows(1);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            User user =  new User();
            user.setId(rs.getInt("User_ID"));
            user.setUserName(rs.getString("User_Name"));
            user.setPassword(rs.getString("Password"));
            user.setCreateDate(rs.getDate("Create_Date"));
            user.setCreatedBy(rs.getString("Created_By"));
            user.setLastUpdate(rs.getTimestamp("Last_Update"));
            user.setLastUpdatedBy(rs.getString("Last_Updated_By"));
            return user;
        }

        return null;
    }

}
