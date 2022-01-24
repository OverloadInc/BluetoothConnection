package over.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.swing.JFileChooser;

/**
 * The <code>ServiceDiscovery</code> class allows users sending files to a specific
 * Bluetooth device selected from a list.
 * This class implements the <code>DiscoveryListener</code> class methods for device
 * and service discovery and provides its own methods for selecting and sending
 * files to a detected Bluetooth device.
 * 
 * @author Overload Inc.
 * @version 1.0, 07 Apr 2017
 */
public class ServiceDiscovery implements DiscoveryListener {
    
    /**
     * The custom list model for storing and display remote devices' information.
     */
    private static CustomListModel customListModel;
    
    /**
     * Selected device's current position.
     */
    private static int selectedDevice;

    /**
     * Synchronized object.
     */
    private static Object lock = new Object();
    
    /**
     * List of discovered Bluetooth devices.
     */
    private static Vector devicesDiscovered = new Vector();
    
    /**
     * List of discovered Bluetooth services.
     */
    private static Vector servicesDiscovered = new Vector();
    
    /**
     * The <code>URL</code> connection.
     */
    private static String connectionURL = null;    
    
    /**
     * Constructor for initializing the custom list model.
     * @param listModel the custom list model.
     */
    public ServiceDiscovery(CustomListModel listModel) {
        customListModel = listModel;
    }
    
    /**
     * Sets the current index for a selected device from the list model.
     * @param index current device's position.
     */
    public static void setSelectedDevice(int index) {
        selectedDevice = index;
    }
    
    /**
     * Opens a file chooser to specify which file to send to another device.
     * @return the selected file.
     */
    public static File openFile() {
        JFileChooser fileChooser = new JFileChooser();
        
        fileChooser.showOpenDialog(null);
                       
        return fileChooser.getSelectedFile();
    }
            
    /**
     * 
     * @throws IOException 
     */
    public static void initInquiry() throws IOException {                
        try {
            synchronized (lock) {
                lock.wait();
            }
        }
        catch (InterruptedException e) {            
        }
    }
    
    /**
     * Prints all Bluetooth devices found.
     */
    public static void printDevices() {        
        int deviceCounter = devicesDiscovered.size();
        
        if(deviceCounter <= 0)
            Printer.setStatus("Devices not found");        
        else {
            for(int i = 0; i < deviceCounter; i++) {
                RemoteDevice remoteDevice = (RemoteDevice) devicesDiscovered.get(i);
                
                customListModel.addDevice(remoteDevice);
            }
            
            Printer.setStatus("Devices: " + devicesDiscovered.size());
        }
    }
    
    /**
     * Checks for available services and opens a connection to establish 
     * communication with the target Bluetooth device.
     * @param discoveryAgent the discovery agent.
     * @param discoveryListener the discovery listener.
     * @throws BluetoothStateException
     * @throws IOException 
     */
    public static void checkServices(DiscoveryAgent discoveryAgent, DiscoveryListener discoveryListener) throws BluetoothStateException, IOException {                        
        RemoteDevice remoteDevice = (RemoteDevice) devicesDiscovered.get(selectedDevice);

        UUID[] uuidSet = {new UUID("1105", true)};

        int[] attrSet = {0x0100, 0x0003, 0x0004};

        Printer.setStatus("Searching for services");

        int currentService = discoveryAgent.searchServices(attrSet, uuidSet, remoteDevice, discoveryListener);

        Printer.setStatus("Current service: " + currentService);

        try {
            synchronized (lock) {
                lock.wait();
            }
        } 
        catch (InterruptedException e) {
        }            

        Printer.setStatus("Opening connection to the server");

        sendMessage();
    }
    
    /**
     * Sends the selected file to the target Bluetooth device.
     */
    public static void sendMessage() {            
        try {
            Connection connection = Connector.open(connectionURL);
            
            Printer.setStatus("Connection obtained");

            ClientSession clientSession = (ClientSession) connection;
            
            HeaderSet headerSet = clientSession.createHeaderSet();

            clientSession.connect(headerSet);
            
            Printer.setStatus("OBEX session created");

            File file = openFile();
            
            InputStream inputStream = new FileInputStream(file);
            
            byte filebytes[] = new byte[inputStream.available()];
            
            inputStream.read(filebytes);
            inputStream.close();

            headerSet = clientSession.createHeaderSet();
            headerSet.setHeader(HeaderSet.NAME, file.getName());
            headerSet.setHeader(HeaderSet.TYPE, "image/jpeg");
            headerSet.setHeader(HeaderSet.LENGTH, new Long(filebytes.length));

            Operation operation = clientSession.put(headerSet);
            
            Printer.setStatus("Pushing file: " + file.getName());
            Printer.setStatus("File size: " + filebytes.length + " bytes");

            OutputStream outputStream = operation.openOutputStream();
            
            outputStream.write(filebytes);
            
            Printer.setStatus("File push completed");

            outputStream.close();
            
            operation.close();

            clientSession.disconnect(null);

            connection.close();
        } 
        catch (IOException e) {
            Printer.setStatus("Connection: " + e.getMessage());
        }
    }

    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        if(!devicesDiscovered.contains(remoteDevice))
            devicesDiscovered.addElement(remoteDevice);       
    }

    @Override
    public void servicesDiscovered(int index, ServiceRecord[] serviceRecord) {
        for(int i = 0; i < serviceRecord.length; i++) {
            DataElement dataElement = serviceRecord[i].getAttributeValue(0x0100);
            
            String dataName = (String)dataElement.getValue();
            String serviceName = dataName.trim();            

            if (serviceName.equals("OBEX Object Push")) {

                Printer.setStatus("Matching service found");
               
                connectionURL = serviceRecord[i].getConnectionURL(0, false);

                Printer.setStatus(connectionURL + "\n");                
            }
        }
    }

    @Override
    public void serviceSearchCompleted(int i, int responseCode) {
        String searchStatus = null;

        switch (responseCode) {
            case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
                searchStatus = "SERVICE_SEARCH_DEVICE_NOT_REACHABLE\n";
                break;
            case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
                searchStatus = "SERVICE_SEARCH_NO_RECORDS\n";
                break;
            case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
                searchStatus = "SERVICE_SEARCH_COMPLETED\n";
                break;        
            case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
                searchStatus = "SERVICE_SEARCH_TERMINATED\n";
                break;
            case DiscoveryListener.SERVICE_SEARCH_ERROR:
                searchStatus = "SERVICE_SEARCH_ERROR\n";
                break;
            default:
                break;
        }

        Printer.setStatus("Status: " + searchStatus);

        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void inquiryCompleted(int discoveryType) {
        switch (discoveryType) {
            case DiscoveryListener.INQUIRY_COMPLETED:
                Printer.setStatus("INQUIRY_COMPLETED");
                break;
            case DiscoveryListener.INQUIRY_TERMINATED:
                Printer.setStatus("INQUIRY_TERMINATED");
                break;
            case DiscoveryListener.INQUIRY_ERROR:
                Printer.setStatus("INQUIRY_ERROR");
                break;
            default:
                Printer.setStatus("Unknown response code");
                break;
        }
        synchronized (lock) {
            lock.notify();
        }
    }
}