
package ravencalculator;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONException;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RavenCalculator 
{
    private static double amountOfUsdForRavencoin = 1;
    private static double convertedAmount;
    private static boolean convertingToRvn = false;
    private static String convertLabel = "USD ";
    private static final String CONVERSION_RATE_FILE_PATH = "save/conversion_rate.txt";
    private static String dateTimeString;
    private static JLabel dateTimeLabel;
    public static void main(String[] args) 
    {
        
        JFrame frame = new JFrame("RVN Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
 
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.BLACK);
        // Add the image label
        ImageIcon imageIcon = new ImageIcon("icons/logo.png"); 
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBounds(150, 50, imageIcon.getIconWidth(), imageIcon.getIconHeight());
        panel.add(imageLabel);

        // Move the "$ for RVN" label down a little
        JLabel label = new JLabel("RVN for USD");
        label.setBounds(150, 150, 100, 30);
        label.setForeground(Color.WHITE);
        panel.add(label);
        
        JToggleButton toggleButton = new JToggleButton("switch");
        toggleButton.setBounds(130, 190, 100, 30);
        panel.add(toggleButton);

        toggleButton.addActionListener(new ActionListener() 
        {
    
            public void actionPerformed(ActionEvent e) 
            {
        if (toggleButton.isSelected()) 
        {
            label.setText("USD for RVN");
            convertingToRvn = true;
        } 
        else 
        {
            label.setText("RVN for USD");
            convertingToRvn = false;
        }
    }
});
        
        JButton button = new JButton("Calculate");
        button.setBounds(100, 300, 200, 50);
        button.setForeground(Color.BLUE);
        button.setBackground(Color.ORANGE);
        panel.add(button);

        JLabel textLabel = new JLabel("Convert Amount:");
        textLabel.setBounds(50, 250, 50, 30);
        panel.add(textLabel);

        JLabel resultLabel = new JLabel();
        resultLabel.setBounds(100, 350, 200, 30);
        resultLabel.setForeground(Color.WHITE);
        panel.add(resultLabel);

        JTextField textField = new JTextField();
        textField.setBounds(100, 250, 200, 30);
        panel.add(textField);

        dateTimeLabel = new JLabel(dateTimeString);
        dateTimeLabel.setBounds(120, 400, 200, 30);
        dateTimeLabel.setForeground(Color.WHITE);
        panel.add(dateTimeLabel);
 
        // Add ActionListener to the button
        button.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                String input = textField.getText(); // Get the text from the input field
                try 
                {
                    int number = Integer.parseInt(input); // Try parsing the input as an integer
                    amountOfUsdForRavencoin = number;
                    //multiply input by the conversion rate//
                    try 
                    {
                        if(convertingToRvn)
                        {
                            convertedAmount = amountOfUsdForRavencoin / getConversionRate();
                            convertLabel = "RVN ";
                        }
                        else
                        {
                            convertedAmount = amountOfUsdForRavencoin * getConversionRate();
                            convertLabel = "USD ";
                        }
                    }                                     
                    catch (JSONException One) 
                    {
                        System.out.println("Error occurred while retrieving the conversion rate: " + One.getMessage());
                    }
                       
                    resultLabel.setBounds(350, 300, 400, 60); // Adjust the position and size as needed
                    resultLabel.setForeground(Color.WHITE);
                    Font font = new Font("Arial", Font.BOLD, 30); // Change the font and size
                    resultLabel.setFont(font);
                    resultLabel.setText(convertLabel + convertedAmount);
                } 
                catch (NumberFormatException ex) 
                {
                    resultLabel.setText("That's not a number");
                }
            
            }
        
        });

        frame.add(panel);
        frame.setVisible(true);
    }
   private static double getConversionRate() throws JSONException
   {
        double conversionRate = 0.0;
        File file = new File(CONVERSION_RATE_FILE_PATH);
        
        try 
        {
            // Make a web call to CoinGecko API to retrieve the conversion rate
            URL url = new URL("https://api.coingecko.com/api/v3/simple/price?ids=ravencoin&vs_currencies=usd");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
                    
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    response.append(line);
                }
                reader.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject ravencoinData = jsonResponse.getJSONObject("ravencoin");
                conversionRate = ravencoinData.getDouble("usd");
                // Get the current date and time
                LocalDateTime currentDateTime = LocalDateTime.now();

                // Format the date and time using a specific pattern
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm");
                dateTimeString  = currentDateTime.format(formatter);
                dateTimeLabel.setText(dateTimeString);
                // Save the conversion rate to the file
                try 
                {
                    FileWriter writer = new FileWriter(file);
                    writer.write(Double.toString(conversionRate));
                    writer.write(System.lineSeparator()); // Add a new line
                    writer.write(dateTimeString);
                    writer.close();
                } 
                catch (IOException we) 
                {
                    System.out.println("Error saving conversion rate to file: " + we.getMessage());
                }
            
            
            } 
            else 
            {
                System.out.println("Failed to retrieve conversion rate. Response code: " + responseCode);
            }
      
            connection.disconnect(); // Close the connection               
        } 
        catch (IOException e) 
        {        
            if (file.exists()) 
            {
                try 
                {
                    Scanner scanner = new Scanner(file);
                    conversionRate = Double.parseDouble(scanner.nextLine());
                    dateTimeString = scanner.nextLine();
                    dateTimeLabel.setText(dateTimeString);
                    scanner.close();
                } 
                catch (FileNotFoundException f) 
                {   
                    dateTimeString = "Rate as of 05/20/2023 - 20:50";
                    conversionRate = 0.0220528;
                    dateTimeLabel.setText(dateTimeString);
                    System.out.println("Error reading conversion rate file: " + e.getMessage());
                }
                catch (NumberFormatException y) 
                {
                    dateTimeString = "05/20/2023 - 20:50";
                    conversionRate = 0.0220528;
                    dateTimeLabel.setText(dateTimeString);
                    System.out.println("Conversion rate file contains invalid data.");
                }
            } 
            else
            {      
                dateTimeString = "05/20/2023 - 20:50";
                dateTimeLabel.setText(dateTimeString);
                conversionRate = 0.0220528;
                System.out.println("Error retrieving conversion rate: " + e.getMessage());
            
            }       
        }                               
                
        return conversionRate;    
    }
}




