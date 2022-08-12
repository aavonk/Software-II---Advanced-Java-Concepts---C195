package services;

import models.Appointment;
import models.AppointmentTypeItem;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AppointmentService is responsible for interacting with the appointments
 * table in the database.
 */
public class AppointmentService extends Service {

    public AppointmentService(Connection dbConnection) {
        super(dbConnection);
    }

    /**
     * Deletes all of the appointments for a specific customer
     * @param customerId the id of the customer to delete appointments for
     * @throws SQLException -
     */
    public void deleteAppointmentsForCustomer(int customerId) throws SQLException {
        PreparedStatement statement = _conn.prepareStatement("DELETE FROM appointments WHERE Customer_ID = ?");
        statement.setInt(1, customerId);
        statement.executeUpdate();

    }

    /**
     * Saves an appointment to the database
     * @param appointment the appointment to save
     * @throws SQLException -
     */
    public void createAppointment(Appointment appointment) throws SQLException {
        PreparedStatement statement = _conn.prepareStatement("INSERT INTO appointments " +
                "(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
        statement.setString(1, appointment.getTitle());
        statement.setString(2, appointment.getDescription());
        statement.setString(3, appointment.getLocation());
        statement.setString(4, appointment.getType());
        statement.setTimestamp(5, Timestamp.valueOf(this.toUtcTime(appointment.getStart())));
        statement.setTimestamp(6, Timestamp.valueOf(this.toUtcTime(appointment.getEnd())));
        statement.setTimestamp(7, new Timestamp(appointment.getCreateDate().getTime()));
        statement.setString(8, appointment.getCreatedBy());
        statement.setTimestamp(9, new Timestamp(appointment.getLastUpdate().getTime()));
        statement.setString(10, appointment.getLastUpdatedBy());
        statement.setInt(11, appointment.getCustomerId());
        statement.setInt(12, appointment.getUserId());
        statement.setInt(13, appointment.getContactId());
        statement.executeUpdate();
    }

    /**
     * Updates the values for an appointment in the database
     * @param appointment an object containing the values to save
     * @throws SQLException -
     */
    public void updateAppointment(Appointment appointment) throws SQLException {
        PreparedStatement statement = _conn.prepareStatement("UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?;");
        statement.setString(1, appointment.getTitle());
        statement.setString(2, appointment.getDescription());
        statement.setString(3, appointment.getLocation());
        statement.setString(4, appointment.getType());
        statement.setTimestamp(5, Timestamp.valueOf(this.toUtcTime(appointment.getStart())));
        statement.setTimestamp(6, Timestamp.valueOf(this.toUtcTime(appointment.getEnd())));
        statement.setTimestamp(7, new Timestamp(appointment.getCreateDate().getTime()));
        statement.setString(8, appointment.getCreatedBy());
        statement.setTimestamp(9, new Timestamp(appointment.getLastUpdate().getTime()));
        statement.setString(10, appointment.getLastUpdatedBy());
        statement.setInt(11, appointment.getCustomerId());
        statement.setInt(12, appointment.getUserId());
        statement.setInt(13, appointment.getContactId());
        statement.setInt(14, appointment.getId());
        statement.executeUpdate();
    }

    /**
     * Deletes a specific appointment from the database
     * @param id - the ID of the appointment
     * @throws SQLException -
     */
    public void deleteAppointment(int id) throws SQLException {
        PreparedStatement statement = _conn.prepareStatement("DELETE FROM appointments WHERE Appointment_ID = ?");
        statement.setInt(1, id);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected != 1) {
            throw new SQLException(String.format("Query effected %d rows", rowsAffected));
        }
    }

    /**
     * Gets all the appointments that are in the database
     * @return a list of appointments
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM appointments AS a" +
                    " JOIN contacts AS c on a.Contact_ID = c.Contact_ID;");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = this.initAppointment(rs);
                appointment.setContact(this.initContact(rs));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    /**
     * Gets all the appointments that are in the database for the current week.
     *
     * @return a list of appointments
     */
    public List<Appointment> getWeeklyAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM appointments AS a" +
                    " JOIN contacts AS c on a.Contact_ID = c.Contact_ID" +
                    " WHERE YEARWEEK(Start, 1) = YEARWEEK(CURDATE(), 1);");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = this.initAppointment(rs);
                appointment.setContact(this.initContact(rs));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    /**
     * Gets all the appointments that are in the database for the current month
     * @return a list of appointments
     */
    public List<Appointment> getMonthlyAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM appointments AS a" +
                    " JOIN contacts AS c on a.Contact_ID = c.Contact_ID" +
                    " WHERE Start BETWEEN (SELECT DATE_ADD(DATE_ADD(LAST_DAY(current_date()), INTERVAL 1 DAY), INTERVAL - 1 MONTH)) and DATE_ADD(LAST_DAY(current_date()), INTERVAL 1 DAY);");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = this.initAppointment(rs);
                appointment.setContact(this.initContact(rs));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    /**
     * Gets an upcoming appointment. An appointment is "upcoming" if it is within 15 minutes of
     * when this method is invoked.
     *
     * @return an Appointment, or null if one does not exist.
     */
    public Appointment getUpcomingAppointment() {
        Appointment appointment = null;

        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM appointments " +
                    "WHERE Start BETWEEN ? and ?;");

            ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneId.systemDefault());
            LocalDateTime now = zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            LocalDateTime future =  now.plusMinutes(15);

            statement.setMaxRows(1);
            statement.setTimestamp(1, Timestamp.valueOf(now));
            statement.setTimestamp(2, Timestamp.valueOf(future));

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                appointment = this.initAppointment(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return appointment;
    }

    /**
     * Checks if there is already an appointment schedules during the start & end time supplied. This
     * is used to prevent overlapping appointments.
     * @param startTime the time the appointment starts
     * @param entTime the time the appointment ends
     * @param id the id of the appointment
     * @return true if there's a conflict, false otherwise
     */
    public boolean hasConflicts(LocalDateTime startTime, LocalDateTime entTime, int id) {
        List<Appointment> appointments = new ArrayList<>();

        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT * FROM appointments AS a" +
                    " JOIN contacts AS c on a.Contact_ID = c.Contact_ID" +
                    " WHERE (Start = ?" +
                    " OR (?) BETWEEN Start AND End" +
                    " OR (?) BETWEEN Start AND End)" +
                    "AND Appointment_ID != ?;");
            statement.setTimestamp(1, Timestamp.valueOf(startTime));
            statement.setTimestamp(2, Timestamp.valueOf(startTime));
            statement.setTimestamp(3, Timestamp.valueOf(entTime));
            statement.setInt(4, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                appointments.add(this.initAppointment(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return appointments.size() > 0;
    }

    /**
     * Gets all  the different appointment types grouped by month
     * @return a List of appointment type items
     */
    public List<AppointmentTypeItem> getAppointmentTypesByMonth() {
        List<AppointmentTypeItem> items = new ArrayList<>();
        try {
            PreparedStatement statement = _conn.prepareStatement("SELECT MONTHNAME(Start) AS Month, Type, COUNT(*) as Amount" +
                    " FROM appointments" +
                    " GROUP BY MONTHNAME(Start), type");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                items.add(this.initAppointmentTypeItem(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return items;
    }

    private LocalDateTime toUtcTime(LocalDateTime date) {
        Timestamp ts = Timestamp.valueOf(date);
        LocalDateTime ldt = ts.toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        ZonedDateTime utczdt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        return utczdt.toLocalDateTime();
    }


}
