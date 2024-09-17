//Emir Adar
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Guestbook {

    public static Connection dbConnection;

    public static void main(String[] args) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

        String computer = "";
        String db_name = "";
        String url = "jdbc:mysql://" + computer + "/" + db_name;


        String username = "";
        String password = "";

        Class.forName("com.mysql.jdbc.Driver");
        dbConnection = DriverManager.getConnection(url, username, password);

        new Application();
    }

}

