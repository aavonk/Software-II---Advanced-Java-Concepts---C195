package models;


import java.util.Date;

public class Customer extends BaseEntity {

    private int id;
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private int divisionId;
    private Division division;

    public Customer() {

    }


    public Customer(String name, String address, String postalCode, String phoneNumber) {
        this.customerName = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.createDate = new Date();
        this.lastUpdate = new Date();
        this.lastUpdatedBy = "application";
        this.createdBy = "application";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }


    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.setDivisionId(division.getId());
        this.division = division;
    }
}
