import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FridgeManagementApp {
    private Connection connection;
    private JFrame frame;
    private DefaultListModel<String> fridgeListModel;
    private JList<String> fridgeList;
    private JTextField itemNameField;
    private JButton addItemButton;
    private JButton findRecipeButton;
    
    public FridgeManagementApp() {
        frame = new JFrame("Fridge Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Initialize the database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fridgedb", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Create a list to display fridge items
        fridgeListModel = new DefaultListModel<>();
        fridgeList = new JList<>(fridgeListModel);

        // Create a text field for item name
        itemNameField = new JTextField(20);

        // Create buttons to add items and find recipes
        addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItemToDatabase(itemNameField.getText());
                loadFridgeItems();
            }
        });

        findRecipeButton = new JButton("Find Recipe");
        findRecipeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFridgeItem = fridgeList.getSelectedValue();
                if (selectedFridgeItem != null) {
                    String itemName = selectedFridgeItem.split(" \\(Expires:")[0]; // Extract the item name
                    findRecipeForItem(itemName);
                }
            }
        });

        // Set up the layout
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(fridgeList), BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel();
        inputPanel.add(itemNameField);
        inputPanel.add(addItemButton);
        inputPanel.add(findRecipeButton);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        
        loadFridgeItems();
    }
    
    private void addItemToDatabase(String itemName) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO fridge (item_name, expiry_date) VALUES (?, ?)");
            statement.setString(1, itemName);
            // Example: Set the expiry date to 7 days from the current date
            Date currentDate = new Date();
            long expiryTime = currentDate.getTime() + 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds
            Date expiryDate = new Date(expiryTime);
            statement.setTimestamp(2, new Timestamp(expiryDate.getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadFridgeItems() {
        fridgeListModel.clear();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT item_name, expiry_date FROM fridge");
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            while (resultSet.next()) {
                String itemName = resultSet.getString("item_name");
                Date expiryDate = resultSet.getTimestamp("expiry_date");
                String formattedDate = dateFormat.format(expiryDate);
                fridgeListModel.addElement(itemName + " (Expires: " + formattedDate + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void findRecipeForItem(String itemName) {
        // Add your recipe recommendation logic here based on the item name.
        // This could involve querying a recipe database and displaying recipe suggestions.
        // For simplicity, this example just displays a message.
        JOptionPane.showMessageDialog(frame, "Recipe recommendation for " + itemName, "Recipe Recommendation", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FridgeManagementApp();
            }
        });
    }
}
