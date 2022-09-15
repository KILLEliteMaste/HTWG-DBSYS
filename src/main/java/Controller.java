import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

public class Controller {
    @FXML
    public DatePicker anreisedatum;
    @FXML
    public DatePicker abreisedatum;
    @FXML
    public TextField maxpreis;
    @FXML
    public ComboBox<String> land;
    @FXML
    public ComboBox<String> ausstattung;
    @FXML
    public TableView<TableModel> table;
    @FXML
    public TableColumn<TableModel, Integer> avgSterne;
    @FXML
    public TableColumn<TableModel, String> fwName;

    String name = "dbsys18";
    String passwd = "do161fel98123";
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    Connection conn = null;
    Statement stmt = null;
    ResultSet rset = null;

    @FXML
    public void initialize() {
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());                // Treiber laden
            String url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"; // String für DB-Connection
            conn = DriverManager.getConnection(url, name, passwd);                        // Verbindung erstellen
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);            // Transaction Isolations-Level setzen
            conn.setAutoCommit(false);                                                    // Kein automatisches Commit
            stmt = conn.createStatement();                                                // Statement-Objekt erzeugen

            String mySelectQuery = "SELECT * FROM LAND";
            rset = stmt.executeQuery(mySelectQuery);                                    // Query ausführen
            while (rset.next()) {
                land.getItems().add(rset.getString("name"));
            }

            mySelectQuery = "SELECT * FROM AUSSTATTUNG";
            rset = stmt.executeQuery(mySelectQuery);                                    // Query ausführen
            while (rset.next()) {
                ausstattung.getItems().add(rset.getString("name"));
            }


            stmt.close();                                                                // Verbindung trennen
            conn.commit();
            conn.close();
        } catch (SQLException se) {                                                        // SQL-Fehler abfangen
            System.out.println("ERRORRRRRRRR");
            try {
                conn.rollback();                                                        // Rollback durchführen
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.exit(-1);
        }
    }

    public void clickedSuchen(MouseEvent mouseEvent) {
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());                // Treiber laden
            String url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"; // String für DB-Connection
            conn = DriverManager.getConnection(url, name, passwd);                        // Verbindung erstellen
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);            // Transaction Isolations-Level setzen
            conn.setAutoCommit(false);                                                    // Kein automatisches Commit
            stmt = conn.createStatement();                                                // Statement-Objekt erzeugen

            String land = this.land.getValue();
            LocalDate anreisedatumlocal = this.anreisedatum.getValue();
            String anreisedatum = anreisedatumlocal.getDayOfMonth() + "/" + anreisedatumlocal.getMonthValue() + "/" + anreisedatumlocal.getYear();
            LocalDate abreisedatumlocal = this.abreisedatum.getValue();
            String abreisedatum = abreisedatumlocal.getDayOfMonth() + "/" + abreisedatumlocal.getMonthValue() + "/" + abreisedatumlocal.getYear();
            String ausstattungen = this.ausstattung.getValue();

            String query = "SELECT f.NAME, AVG(b.BEWERTUNG_STERNE) AS \"Durchschnittliche Bewertung\"\n"
                    + "FROM dbsys18.Buchung b\n"
                    + "         right OUTER JOIN dbsys18.FERIENWOHNUNG f ON b.FERIENWOHNUNG_ID = f.ID\n"
                    + "         INNER JOIN dbsys18.Adresse a ON f.ADRESSE = a.ID\n"
                    + "WHERE a.LAND_NAME = '" + land + "'\n"
                    + "  AND f.ID NOT IN\n"
                    + "      (SELECT f.ID\n"
                    + "       FROM dbsys18.Buchung b\n"
                    + "       WHERE b.ANREISE_DATUM BETWEEN '" + anreisedatum + "' AND '" + abreisedatum + "'\n"
                    + "          OR b.ABREISE_DATUM BETWEEN '" + anreisedatum + "' AND '" + abreisedatum + "'\n"
                    + "          OR b.ANREISE_DATUM < '" + anreisedatum + "' AND b.ABREISE_DATUM > '" + abreisedatum + "'\n"
                    + "      )\n"
                    + "  AND f.ID IN (SELECT fw.FERIENWOHNUNG_ID FROM dbsys18.FW_AUSSTATTUNG fw WHERE fw.AUSSTATTUNG_NAME = '" + ausstattungen + "')\n"
                    + "GROUP BY f.NAME\n"
                    + "ORDER BY NVL(AVG(b.BEWERTUNG_STERNE),0) DESC";
            //System.out.println(query);
            rset = stmt.executeQuery(query);                                    // Query ausführen
            //make sure the property value factory should be exactly same as the e.g getStudentId from your model class
            fwName.setCellValueFactory(new PropertyValueFactory<>("Name"));
            avgSterne.setCellValueFactory(new PropertyValueFactory<>("Sterne"));
            System.out.println("0");
            // add your data here from any source
            ObservableList<TableModel> models = FXCollections.observableArrayList();
            System.out.println("1");
            while (rset.next()) {
                models.add(new TableModel(rset.getString("NAME"), rset.getInt("Durchschnittliche Bewertung")));
                System.out.println(rset.getString("NAME"));
                //System.out.println(rset.getInt("Durchschnittliche Bewertung"));
            }
            System.out.println("2");
            //add your data to the table here.
            table.setItems(models);
            table.refresh();

            stmt.close();                                                                // Verbindung trennen
            conn.commit();
            conn.close();
        } catch (SQLException se) {                                                        // SQL-Fehler abfangen
            se.printStackTrace();
            System.out.println("ERRORRRRRRRR");
            try {
                conn.rollback();                                                        // Rollback durchführen
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.exit(-1);
        }
    }

    public void clickedBuchen(MouseEvent mouseEvent) {

        TableModel tmodel = table.getSelectionModel().getSelectedItem();
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());                // Treiber laden
            String url = "jdbc:oracle:thin:@oracle19c.in.htwg-konstanz.de:1521:ora19c"; // String für DB-Connection
            conn = DriverManager.getConnection(url, name, passwd);                        // Verbindung erstellen
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);            // Transaction Isolations-Level setzen
            conn.setAutoCommit(false);                                                    // Kein automatisches Commit
            stmt = conn.createStatement();                                                // Statement-Objekt erzeugen

            LocalDate anreisedatumlocal = this.anreisedatum.getValue();
            String anreisedatum = anreisedatumlocal.getDayOfMonth() + "/" + anreisedatumlocal.getMonthValue() + "/" + anreisedatumlocal.getYear();
            LocalDate abreisedatumlocal = this.abreisedatum.getValue();
            String abreisedatum = abreisedatumlocal.getDayOfMonth() + "/" + abreisedatumlocal.getMonthValue() + "/" + abreisedatumlocal.getYear();


            String query = "SELECT ID ,PREIS FROM FERIENWOHNUNG WHERE NAME = '" + tmodel.getName() + "'";
            rset = stmt.executeQuery(query);                                    // Query ausführen
            double preis = 0;
            Integer fwId = 0;
            while (rset.next()) {
                fwId = rset.getInt("ID");
                preis = rset.getDouble("preis");
            }
            System.out.println("++++++++++++++++++++++");
            System.out.println(LocalDate.now());
            query = "INSERT INTO DBSYS18.BUCHUNG (KUNDE_MAIL, FERIENWOHNUNG_ID, ANREISE_DATUM, ABREISE_DATUM, DATUM, BEWERTUNG_DATUM, BEWERTUNG_STERNE, BETARG, RECHNUNG_DATUM, RECHNUNGSNUMMER) "
                    + "VALUES ('d.fellbaum@hotmail.de', " + fwId + ", TO_DATE('" + anreisedatum + "', 'DD/MM/YYYY'), TO_DATE('" + abreisedatum + "', 'DD/MM/YYYY'), TO_DATE('" + LocalDate.now() + "', 'YYYY-MM-DD'), null, null, " + preis + ", null, null)";
            rset = stmt.executeQuery(query);
            // Query ausführen
            stmt.close();                                                                // Verbindung trennen
            conn.commit();
            conn.close();
        } catch (SQLException se) {                                                        // SQL-Fehler abfangen
            se.printStackTrace();
            System.out.println("ERRORRRRRRRR");
            try {
                conn.rollback();                                                        // Rollback durchführen
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.exit(-1);
        }
    }
}
