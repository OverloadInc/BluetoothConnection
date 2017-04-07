package core;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;

/**
 * The Printer class provides a set of methods to set the information related to 
 * the Bluetooth service discovery process.
 * This information is displayed in the application's front-end.
 * 
 * @author Juan-Alberto Hernández-Martínez
 */
public class Printer {
    
    /**
     * JLabel used for containing information of devices selected by the user.
     */
    private static JLabel messageLabel;
    
    /**
     * JTextArea used for displaying information of the Bluetooth discovery 
     * service process.
     */
    private static JTextArea statusConsole;
    
    /**
     * Variable to concatenate the information to display in the 
     * application's console.
     */
    private static String message = "";
    
    /**
     * Deletes both list model and device list information.
     * @param listModel the list model.
     * @param deviceList the device list.
     */
    public static void initModel(CustomListModel listModel, JList deviceList) {
        listModel.deleteDevices();
        deviceList.setModel(listModel);
    }
    
    /**
     * Initializes the JLabel in which the information will be displayed.
     * @param message the JLable.
     */
    public static void initMessageLabel(JLabel message) {
        Printer.messageLabel = message;
    }
    
    /**
     * Initializes the JTextArea that plays the role of application console.
     * @param status the JTextArea.
     */
    public static void initStatusConsole(JTextArea status) {
        Printer.statusConsole = status;
    }
    
    /**
     * Deletes the message content.
     */
    public static void initMessage() {
        message = "";
    }
    
    /**
     * Sets the message label with a specific content.
     * @param message the message to display.
     */
    public static void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    /**
     * Sets the status console with a specific content.
     * @param status the status console.
     */
    public static void setStatus(String status) {
        statusConsole.setText(message += status + "\n");
    }      
}