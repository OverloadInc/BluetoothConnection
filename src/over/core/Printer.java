package over.core;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;

/**
 * The <code>Printer</code> class provides a set of methods to set the information
 * related to the Bluetooth service discovery process.
 * This information is displayed in the application's front-end.
 * 
 * @author Overload Inc.
 * @version 1.0, 07 Apr 2017
 */
public class Printer {
    
    /**
     * <code>JLabel</code> used for containing information of devices selected
     * by the user.
     */
    private static JLabel messageLabel;
    
    /**
     * <code>JTextArea</code> used for displaying information of the Bluetooth
     * discovery service process.
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
     * Initializes the <code>JLabel</code> in which the information will be displayed.
     * @param message the <code>JLable</code>.
     */
    public static void initMessageLabel(JLabel message) {
        Printer.messageLabel = message;
    }
    
    /**
     * Initializes the <code>JTextArea</code> that plays the role of application console.
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