module com.example.atm_machine {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.apache.logging.log4j;
    requires org.slf4j;
    requires org.apache.logging.log4j.core;


    opens com.example.atm_machine to javafx.fxml;
    exports com.example.atm_machine;
    exports com.example.atm_machine.Controllers;
    opens com.example.atm_machine.Controllers to javafx.fxml;
    exports com.example.atm_machine.Models;
    opens com.example.atm_machine.Models to javafx.fxml;
}