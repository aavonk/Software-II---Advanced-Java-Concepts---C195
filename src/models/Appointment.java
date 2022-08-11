package models;

import java.time.*;
import java.util.Date;

public class Appointment extends BaseEntity {
    private int id;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerId;
    private int userId;
    private int contactId;

    private Contact contact;

    public Appointment() {

    }

    public Appointment(String title, String description, String location, String type, LocalTime start, LocalTime end, LocalDate appointmentDate, Contact contact, int userId, int customerId) {
        Date date = Date.from(appointmentDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        Instant startInstant = start.atDate(appointmentDate).atZone(ZoneId.systemDefault()).toInstant();
        this.start = LocalDateTime.ofInstant(startInstant,ZoneId.systemDefault());

        Instant endInstant = end.atDate(appointmentDate).atZone(ZoneId.systemDefault()).toInstant();
        this.end = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault());
        this.customerId = customerId;
        this.userId = userId;
        this.contact = contact;
        this.contactId = contact.getId();
        Date createdAt = new Date();
        this.createDate = createdAt;
        this.createdBy = "application";
        this.lastUpdate = createdAt;
        this.lastUpdatedBy = "application";
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
}
