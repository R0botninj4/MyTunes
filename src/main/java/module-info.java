module MyTunes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires java.sql;
    requires java.naming;
    requires com.microsoft.sqlserver.jdbc;
    requires java.desktop;
    requires javafx.swing;

    opens GUI to javafx.fxml;
    opens BE to javafx.base;
    opens BLL to javafx.fxml;
    opens DAL to javafx.fxml;

    exports GUI;
    exports BE;
    exports BLL;
    exports DAL;
    exports DAL.DB;
    opens DAL.DB to javafx.fxml;
}
