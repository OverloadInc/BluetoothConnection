package core;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.RemoteDevice;
import javax.swing.AbstractListModel;

/**
 * The CustomListModel class provides a means for Bluetooth device management.
 * This class contains methods for adding, deleting and obtaining information 
 * related to Bluetooth devices which are detected by a service discovery 
 * mechanism. In addition, the CustomListModel is responsible for building a 
 * list model to display all the information obtained by the service discovery 
 * mechanism such that the user selects a specific device for sending files
 * easily.
 * 
 * @author Juan-Alberto Hernández-Martínez
 */
public class CustomListModel extends AbstractListModel{
    
    /**
     * Vector for storing remote devices information.
     */
    private static Vector<RemoteDevice> devices = new Vector<>();
    
    /**
     * Adds a new remote device to the devices list and updates the selection 
     * interval of the list model to know the correct index of a device
     * selected by the user.
     * .
     * @param device the remote device.
     */
    public void addDevice(RemoteDevice device) {
        devices.add(device);
        this.fireIntervalAdded(this, getSize(), getSize() + 1);
    }
    
    /**
     * Removes a remote device given a specific index and updates the selection
     * interval of the list model to know the correct index of a device selected
     * by the user.
     * @param index the index of the device to remove.
     */
    public void deleteDevice(int index) {
        devices.remove(index);
        this.fireIntervalRemoved(index, getSize(), getSize() + 1);
    }
    
    /**
     * Removes all devices in the list.
     */
    public void deleteDevices() {
        devices.removeAllElements();
    }
    
    /**
     * Gets a remote device given a specific index.
     * @param index the remote device index.
     * @return a remote device.
     */
    public RemoteDevice getDevice(int index) {
        return devices.get(index);
    }
    
    /**
     * Gets the remote devices list.
     * @return the remote devices list.
     */
    public Vector<RemoteDevice> getDevices() {
        return devices;
    }
 
    @Override
    public int getSize() {
        return devices.size();
    }
 
    @Override
    public Object getElementAt(int index) {
        String name = null;
        
        try {            
            name = devices.get(index).getFriendlyName(false);                        
        }
        catch (IOException ex) {
            Logger.getLogger(CustomListModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return name;
    }
}