Application: WGU.Scheduling
Author: Aaron von Kreisler
Contact Information: avonkr3@wgu.edu
Version: 2


Purpose:

This is a desktop application written in Java to allow employees to schedule appointments for customers.
It provides functionality to create, read, update, and delete appointments as well as creating, reading, updating and
deleting customers. Other that that functionality, it provides 3 different reports that gives an analytical overview as to what
type of appointments are being scheduled, by who, and where those people live.

Development Environment:
- IDE: IntelliJ IDEA 2022.1.3 (Community Edition)
- SDK: Java SDK Version 18
- JavaFX Version - JavaFX-SDK-18.0.1
- MySQL Connector version 8.0.29 (mysql-connector-java-8.0.29)

Directions:
To run this application, you need to create a run configuration in IntelliJ. To do that, look for the configuration menu next to the green
arrow in the top right corner of the IDE. Select "Edit Configurations", and then choose to create a new one.
- Give the configuration a name (e.g Main)
- Select  Java SDK Version 18. If you do not see this, you will need to download it.
- Add this to the VM options: --module-path ${PATH_TO_FX} --add-modules javafx.fxml,javafx.controls,javafx.graphics

Lastly, you need to make sure the PATH_TO_FX environment variable is set. You can do this in settings, under
settings -> appearance and behavior -> Path Variables. The value for the variable needs to be set to the file location of
JavaFX on your machine.


Additional Report:
The additional report shows how many customers exist in each division. This is useful for helping us understand where
our customers live and how we can better serve them based on their location.



