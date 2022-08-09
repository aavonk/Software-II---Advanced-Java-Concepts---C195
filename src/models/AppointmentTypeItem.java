package models;

/**
 * A class that holds data for the Appointment Types Per Month Report
 */
public class AppointmentTypeItem {
    public String month;
    public String type;
    public int amount;

    public AppointmentTypeItem() {
    }

    public AppointmentTypeItem(String month, String type, int amount) {
        this.amount = amount;
        this.month = month;
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
