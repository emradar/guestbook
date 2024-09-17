// Emir Adar

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * This is the method for the application that handles inserts and prints through the database
 * */
public class Application extends JFrame {

    // declaring variables
    private static final JTextArea chatArea = new JTextArea();
    private static JTextField nameField;
    private static JTextField emailField;
    private static JTextField homepageField;
    private static JTextField commentField;
    private JButton sendBtn;

    /**
     * This is the constructor for this class
     * */
    public Application() throws SQLException {

        // setting up the GUI and adding action listener to the send button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFrame frame = new JFrame();
        frame.setSize(700, 500);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        nameField = new JTextField();
        emailField = new JTextField();
        homepageField = new JTextField();
        commentField = new JTextField();
        sendBtn = new JButton("Send");
        sendBtn.addActionListener(new InsertObject());

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("E-mail:"));
        panel.add(emailField);
        panel.add(new JLabel("Homepage:"));
        panel.add(homepageField);
        panel.add(new JLabel("Comment:"));
        panel.add(commentField);
        panel.add(new JLabel("Add:"));
        panel.add(sendBtn);

        frame.add(panel, BorderLayout.NORTH);
        chatArea.setEditable(false);
        chatArea.setAutoscrolls(true);
        frame.add(new JScrollPane(chatArea));
        frame.setVisible(true);
        getAll();
        createTable();
    }

    /**
     * This is the method for creating a table in the database that is in the correct format
     * */
    private void createTable(){

        Statement statement = null;
        ResultSet resultSet = null;
        // trying to create the table
        try{
            statement = Guestbook.dbConnection.createStatement();
            resultSet = statement.executeQuery("SHOW TABLES LIKE 'Comments'");
            // checking if it already exists
            if(!resultSet.next()) {
                String query = "CREATE TABLE Comments (" +
                        "NO INT AUTO_INCREMENT PRIMARY KEY, " +
                        "TIME TIMESTAMP, " +
                        "NAME VARCHAR(255), " +
                        "EMAIL VARCHAR(255), " +
                        "HOMEPAGE VARCHAR(255), " +
                        "COMMENT TEXT" +
                        ")";
                statement.execute(query);
            }

        } catch(SQLException sqlException){
            System.out.println("could not create the table: " + sqlException.getMessage());
        } finally{
            try{
                if(resultSet != null)
                    resultSet.close();
                if(statement != null)
                    statement.close();
            } catch (SQLException sqlException){
                System.out.println(sqlException.getMessage());
            }
        }

    }

    /**
     * This is an action listener that gets called when the 'send' button is pressed
     * */
    class InsertObject implements ActionListener {

        public InsertObject() {

        }

        Pattern p = Pattern.compile("<.*>");
        @Override
        public void actionPerformed(ActionEvent ae) {
            // trying to create and execute an SQL statement to insert values into a row in the table
            try {
                String query = "INSERT INTO Comments (TIME, NAME, EMAIL, HOMEPAGE, COMMENT) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = Guestbook.dbConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                // checking if the input text is in an html format, if not, the input gets inserted as is
                if(p.matcher(nameField.getText()).matches()){
                    preparedStatement.setString(2, "censur");
                } else
                    preparedStatement.setString(2, nameField.getText());

                if(p.matcher(emailField.getText()).matches()){
                    preparedStatement.setString(3, "censur");
                } else
                    preparedStatement.setString(3, emailField.getText());

                if(p.matcher(homepageField.getText()).matches()){
                    preparedStatement.setString(4, "censur");
                } else
                    preparedStatement.setString(4, homepageField.getText());

                if(p.matcher(commentField.getText()).matches()){
                    preparedStatement.setString(2, "censur");
                } else
                    preparedStatement.setString(2, commentField.getText());

                preparedStatement.executeUpdate();

                // saving the generated key(NO) and calling getObject to print out the inserted row
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if(generatedKeys.next()){
                    int generatedIndex = generatedKeys.getInt(1);
                    getObject(generatedIndex);
                }
                preparedStatement.close();
            } catch (SQLException sqlException) {
                System.out.println("Could not insert the object: " + sqlException.getMessage());
            }
        }
    }

    /**
     * This method gets the latest inserted value and prints it in the text are
     * */
    private static void getObject(int generatedIndex) throws SQLException {
        try {
            PreparedStatement preparedStatement = Guestbook.dbConnection.prepareStatement("SELECT * FROM Comments WHERE NO = ?");
            preparedStatement.setInt(1, generatedIndex);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chatArea.append("\n\nNO: ");
                chatArea.append(resultSet.getString("NO"));
                chatArea.append(" TIME: ");
                chatArea.append(resultSet.getString("TIME"));
                chatArea.append("\nNAME: ");
                chatArea.append(resultSet.getString("NAME"));
                chatArea.append(" EMAIL: ");
                chatArea.append(resultSet.getString("EMAIL"));
                chatArea.append(" HOMEPAGE: ");
                chatArea.append(resultSet.getString("HOMEPAGE"));
                chatArea.append("\nCOMMENT: ");
                chatArea.append(resultSet.getString("COMMENT"));
                chatArea.updateUI();
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException sqlException) {
            System.out.println("Could not retrieve data: " + sqlException.getMessage());
        }
    }

    /**
     * This method gets the whole table and prints out all the entries when opening the application
     * */
    private static void getAll() throws SQLException {

        try{
        PreparedStatement preparedStatement = Guestbook.dbConnection.prepareStatement("SELECT * FROM Comments");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            chatArea.append("\n\nNO: ");
            chatArea.append(resultSet.getString("NO"));
            chatArea.append(" TIME: ");
            chatArea.append(resultSet.getString("TIME"));
            chatArea.append("\nNAME: ");
            chatArea.append(resultSet.getString("NAME"));
            chatArea.append(" EMAIL: ");
            chatArea.append(resultSet.getString("EMAIL"));
            chatArea.append(" HOMEPAGE: ");
            chatArea.append(resultSet.getString("HOMEPAGE"));
            chatArea.append("\nCOMMENT: ");
            chatArea.append(resultSet.getString("COMMENT"));
            chatArea.updateUI();
        }
        resultSet.close();
        preparedStatement.close();
        } catch (SQLException sqlException) {
            System.out.println("Could not retrieve data: " + sqlException.getMessage());
        }
    }

}
