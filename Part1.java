import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.Border;
import java.sql.*;

public class Part1 extends JFrame implements ActionListener {

    JButton b1, addButton;
    JTextField tfExerciseTime, caloriesGainedField, caloriesLostField, totalCaloriesField, dateField, quantityField, weightField;
    JLabel l2, caloriesGainedLabel, caloriesLostLabel, totalCaloriesLabel, healthStatusLabel, weightLabel;
    JRadioButton maleRadioButton, femaleRadioButton, workoutYesRadioButton, workoutNoRadioButton;
    JComboBox<String> foodComboBox;
    JPanel selectedItemsPanel;
    JScrollPane scrollPane;
    JProgressBar progressBar, weightLossProgressBar;
    DefaultListModel<String> foodListModel;
    DefaultListModel<Integer> quantityListModel;
    HashMap<LocalDate, Integer> dailyCalories;
    Connection connection=null;
    Statement statement=null;
    ResultSet resultSet=null;

    Part1() {
        // Initialize dailyCalories map
        dailyCalories = new HashMap<>();

        // Greet label
        JLabel l1 = new JLabel();
        String msg = greetMsg();
        l1.setText(msg);
        l1.setBounds(10, 10, 400, 30);
        l1.setForeground(Color.BLACK); // Changed to black
        l1.setFont(new Font("Arial", Font.PLAIN, 18));
        l1.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        // Health progress label
        JLabel healthProgressLabel = new JLabel("Let's track your today's health progress");
        healthProgressLabel.setBounds(10, 50, 600, 40);
        healthProgressLabel.setForeground(Color.GREEN);
        healthProgressLabel.setFont(new Font("Arial", Font.BOLD, 24));
        healthProgressLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        // Instruction label
        l2 = new JLabel();
        l2.setText("Select food items you ate today:");
        l2.setBounds(10, 100, 400, 30);
        l2.setForeground(Color.BLACK); // Changed to black
        l2.setFont(new Font("Arial", Font.PLAIN, 18));
        l2.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        // ComboBox for selecting food items
        String[] foodItems = {"Apple", "Banana", "Bread", "Chicken", "Egg", "Fish", "Milk", "Orange", "Rice", "Vegetables"};
        foodComboBox = new JComboBox<>(foodItems);
        foodComboBox.setBounds(400, 100, 150, 30);
        foodComboBox.setFont(new Font("Arial", Font.PLAIN, 18));

        // Quantity label and text field
        JLabel quantityLabel = new JLabel("Quantity (grams):");
        quantityLabel.setBounds(10, 140, 150, 30);
        quantityLabel.setForeground(Color.BLACK); // Changed to black
        quantityLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        quantityLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        quantityField = new JTextField();
        quantityField.setBounds(160, 140, 100, 30);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 18));

        // Button to add selected food item and quantity to the list
        addButton = new JButton("Add");
        addButton.setBounds(560, 140, 80, 30);
        addButton.setFont(new Font("Arial", Font.PLAIN, 18));
        addButton.addActionListener(this);

        // Panel to display selected food items
        selectedItemsPanel = new JPanel();
        selectedItemsPanel.setBackground(Color.LIGHT_GRAY);
        selectedItemsPanel.setLayout(new BoxLayout(selectedItemsPanel, BoxLayout.Y_AXIS));

        // Scroll pane to wrap the selectedItemsPanel
        scrollPane = new JScrollPane(selectedItemsPanel);
        scrollPane.setBounds(10, 180, 760, 100);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // List models to manage selected food items and their quantities
        foodListModel = new DefaultListModel<>();
        quantityListModel = new DefaultListModel<>();

        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(10, 290, 80, 30);
        genderLabel.setForeground(Color.BLACK); // Changed to black
        genderLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        genderLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        maleRadioButton = new JRadioButton("Male");
        maleRadioButton.setBounds(100, 290, 70, 30);
        maleRadioButton.setFont(new Font("Arial", Font.PLAIN, 18));

        femaleRadioButton = new JRadioButton("Female");
        femaleRadioButton.setBounds(180, 290, 100, 30);
        femaleRadioButton.setFont(new Font("Arial", Font.PLAIN, 18));

        ButtonGroup genderButtonGroup = new ButtonGroup();
        genderButtonGroup.add(maleRadioButton);
        genderButtonGroup.add(femaleRadioButton);

        // Date label and text field
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(10, 330, 180, 30);
        dateLabel.setForeground(Color.BLACK); // Changed to black
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        dateLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        dateField = new JTextField();
        dateField.setBounds(200, 330, 150, 30);
        dateField.setFont(new Font("Arial", Font.PLAIN, 18));

        // Workout
        JLabel workoutLabel = new JLabel("Did you have your workout today?");
        workoutLabel.setBounds(10, 370, 400, 30);
        workoutLabel.setForeground(Color.BLACK); // Changed to black
        workoutLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        workoutLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        workoutYesRadioButton = new JRadioButton("Yes");
        workoutYesRadioButton.setBounds(300, 370, 70, 30);
        workoutYesRadioButton.setFont(new Font("Arial", Font.PLAIN, 18));

        workoutNoRadioButton = new JRadioButton("No");
        workoutNoRadioButton.setBounds(380, 370, 70, 30);
        workoutNoRadioButton.setFont(new Font("Arial", Font.PLAIN, 18));

        ButtonGroup workoutButtonGroup = new ButtonGroup();
        workoutButtonGroup.add(workoutYesRadioButton);
        workoutButtonGroup.add(workoutNoRadioButton);

        // Exercise time label and text field
        JLabel exerciseTimeLabel = new JLabel("How much time did you do your exercise (in minutes)?");
        exerciseTimeLabel.setBounds(10, 410, 500, 30);
        exerciseTimeLabel.setForeground(Color.BLACK); // Changed to black
        exerciseTimeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        exerciseTimeLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        tfExerciseTime = new JTextField();
        tfExerciseTime.setBounds(500, 410, 100, 30);
        tfExerciseTime.setFont(new Font("Arial", Font.PLAIN, 18));

        // Weight label and text field
        weightLabel = new JLabel("Your weight (kg):");
        weightLabel.setBounds(10, 450, 200, 30);
        weightLabel.setForeground(Color.BLACK); // Changed to black
        weightLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        weightLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        weightField = new JTextField();
        weightField.setBounds(220, 450, 100, 30);
        weightField.setFont(new Font("Arial", Font.PLAIN, 18));

        // Button to generate fields
        b1 = new JButton("Submit");
        b1.setBounds(10, 490, 100, 30);
        b1.setFont(new Font("Arial", Font.PLAIN, 18));
        b1.addActionListener(this);

        // Calories labels and fields
        caloriesGainedLabel = new JLabel("Calories Gained: ");
        caloriesGainedLabel.setBounds(10, 530, 200, 30);
        caloriesGainedLabel.setForeground(Color.BLACK); // Changed to black
        caloriesGainedLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        caloriesGainedLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        caloriesGainedField = new JTextField();
        caloriesGainedField.setBounds(220, 530, 100, 30);
        caloriesGainedField.setFont(new Font("Arial", Font.PLAIN, 18));
        caloriesGainedField.setEditable(false);

        caloriesLostLabel = new JLabel("Calories Lost: ");
        caloriesLostLabel.setBounds(10, 570, 200, 30);
        caloriesLostLabel.setForeground(Color.BLACK); // Changed to black
        caloriesLostLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        caloriesLostLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        caloriesLostField = new JTextField();
        caloriesLostField.setBounds(220, 570, 100, 30);
        caloriesLostField.setFont(new Font("Arial", Font.PLAIN, 18));
        caloriesLostField.setEditable(false);

        totalCaloriesLabel = new JLabel("Total Calories: ");
        totalCaloriesLabel.setBounds(10, 610, 200, 30);
        totalCaloriesLabel.setForeground(Color.BLACK); // Changed to black
        totalCaloriesLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        totalCaloriesLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        totalCaloriesField = new JTextField();
        totalCaloriesField.setBounds(220, 610, 100, 30);
        totalCaloriesField.setFont(new Font("Arial", Font.PLAIN, 18));
        totalCaloriesField.setEditable(false);

        // Health status label
        healthStatusLabel = new JLabel();
        healthStatusLabel.setBounds(10, 650, 400, 30);
        healthStatusLabel.setForeground(Color.BLACK); // Changed to black
        healthStatusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        healthStatusLabel.setHorizontalAlignment(SwingConstants.LEFT); // Left align

        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setBounds(10, 690, 760, 30);
        progressBar.setStringPainted(true);

        // Weight loss progress bar
        weightLossProgressBar = new JProgressBar();
        weightLossProgressBar.setBounds(10, 730, 760, 30);
        weightLossProgressBar.setStringPainted(true);

        //JDBC CONNECTION ESTABLISHMENT.
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","23MMCI77","MCA108");   
        } catch (ClassNotFoundException ce) {
            System.out.println(ce);
        }catch(SQLException se){
            System.out.println(se);
        }
        //JDBC CONNECTION ESTABLISHED

        // Adding components to the frame
        add(l1);
        add(healthProgressLabel);
        add(l2);
        add(foodComboBox);
        add(quantityLabel);
        add(quantityField);
        add(addButton);
        add(scrollPane);
        add(genderLabel);
        add(maleRadioButton);
        add(femaleRadioButton);
        add(dateLabel);
        add(dateField);
        add(workoutLabel);
        add(workoutYesRadioButton);
        add(workoutNoRadioButton);
        add(exerciseTimeLabel);
        add(tfExerciseTime);
        add(weightLabel);
        add(weightField);
        add(b1);
        add(caloriesGainedLabel);
        add(caloriesGainedField);
        add(caloriesLostLabel);
        add(caloriesLostField);
        add(totalCaloriesLabel);
        add(totalCaloriesField);
        add(healthStatusLabel);
        add(progressBar);
        add(weightLossProgressBar);

        // Frame settings
        setLayout(null);
        setTitle("Health Progress Tracker");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String selectedItem = (String) foodComboBox.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());

            foodListModel.addElement(selectedItem);
            quantityListModel.addElement(quantity);

            JLabel foodItemLabel = new JLabel(selectedItem + " - " + quantity + " grams");
            selectedItemsPanel.add(foodItemLabel);
            selectedItemsPanel.revalidate();
            selectedItemsPanel.repaint();
        } else if (e.getSource() == b1) {
            int caloriesGained = calculateCaloriesGained();
            int caloriesLost = calculateCaloriesLost();
            int totalCalories = caloriesGained - caloriesLost;

            caloriesGainedField.setText(String.valueOf(caloriesGained));
            caloriesLostField.setText(String.valueOf(caloriesLost));
            totalCaloriesField.setText(String.valueOf(totalCalories));

            // Calculate and update progress bar
            int progressValue = Math.max(0, Math.min(100, (int) ((caloriesLost * 1.0 / caloriesGained) * 100)));
            progressBar.setValue(progressValue);

            // Calculate and update weight loss progress bar
            double weight = Double.parseDouble(weightField.getText());
            double weightLoss = calculateWeightLoss(caloriesLost, weight);
            int weightLossProgressValue = (int) ((weightLoss / weight) * 100);
            weightLossProgressBar.setValue(weightLossProgressValue);
            weightLossProgressBar.setString(String.format("Weight Lost: %.2f kg", weightLoss));

            // Update health status label
            if (totalCalories < 0) {
                healthStatusLabel.setText("Great job! You've burned more calories than you consumed.");
            } else {
                healthStatusLabel.setText("Keep going! Try to burn more calories.");
            }

            // Update dailyCalories map
            LocalDate date = LocalDate.parse(dateField.getText());
            dailyCalories.put(date, totalCalories);
        }
    }

    private int calculateCaloriesGained() {
        int totalCalories = 0;
        for (int i = 0; i < foodListModel.size(); i++) {
            String foodItem = foodListModel.get(i);
            int quantity = quantityListModel.get(i);
            totalCalories += getCaloriesForFood(foodItem, quantity);
        }
        return totalCalories;
    }

    private int calculateCaloriesLost() {
        int exerciseTime = Integer.parseInt(tfExerciseTime.getText());
        boolean didWorkout = workoutYesRadioButton.isSelected();
        int caloriesLost=0; //= exerciseTime * 5; // Assuming 5 calories burned per minute of exercise
        if (didWorkout) {
            caloriesLost= exerciseTime * 5; // Additional calories burned for workout
        }
        return caloriesLost;
    }

    private int getCaloriesForFood(String foodItem, int quantity) {
        try{
            String dbQuerry ="select calories from FoodCalories where fname='"+foodItem+"'";
            statement=connection.createStatement();
            resultSet = statement.executeQuery(dbQuerry);    
            while(resultSet.next()){
                quantity=resultSet.getInt("Calories")*(quantity/100);
            }
        }catch(SQLException se){
            System.out.println(se);
        }
        return quantity;
    }

    private double calculateWeightLoss(int caloriesLost, double weight) {
        // Assuming 7700 calories = 1 kg of body weight
        return caloriesLost / 7700.0;
    }

    public String greetMsg() {
        LocalTime time = LocalTime.now();
        if (time.isBefore(LocalTime.NOON)) {
            return "Good Morning!";
        } else if (time.isBefore(LocalTime.of(18, 0))) {
            return "Good Afternoon!";
        } else {
            return "Good Evening!";
        }
    }

    public static void main(String[] args) {
        new Part1();
    }
}