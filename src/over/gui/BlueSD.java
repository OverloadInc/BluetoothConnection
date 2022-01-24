package over.gui;

import over.core.CustomListModel;
import over.core.Printer;
import over.core.ServiceDiscovery;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>BlueSD</code> class encapsulates a basic Bluecove-based client for bluetooth
 * service discovery.
 * 
 * @author Overload Inc.
 * @version 1.0, 07 Apr 2017
 */
public class BlueSD extends JFrame {
    private JButton btnClear;
    private JButton btnSearch;
    private JPanel deletePanel;
    private JList deviceList;
    private JPanel devicePanel;
    private JScrollPane deviceScroll;
    private static JLabel lblMessage;
    private JPanel optionPanel;
    private JTextArea statusConsole;
    private JScrollPane statusScroll;
    
    /**
     * The list model for storing and display remote devices' information.
     */
    private CustomListModel listModel = new CustomListModel();
    
    /**
     * The bluetooth service discovery.
     */
    private ServiceDiscovery bluetoothServiceDiscovery;
    
    /**
     * The discovery agent.
     */
    private DiscoveryAgent agent;
    
    /**
     * Constructor for initializing the application elements.
     */
    public BlueSD() {        
        initComponents();
        Printer.initMessageLabel(lblMessage);
        Printer.initStatusConsole(statusConsole);
        Printer.initModel(listModel, deviceList);        
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        devicePanel = new JPanel();
        lblMessage = new JLabel();
        deviceScroll = new JScrollPane();
        deviceList = new JList();
        statusScroll = new JScrollPane();
        statusConsole = new JTextArea();
        optionPanel = new JPanel();
        btnSearch = new JButton();
        deletePanel = new JPanel();
        btnClear = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("BlueSD v1.0");
        setMaximumSize(new Dimension(270, 350));
        setName("frmBSD"); 

        devicePanel.setMaximumSize(new Dimension(270, 270));
        devicePanel.setMinimumSize(new Dimension(270, 270));
        devicePanel.setName("devicePanel"); 
        devicePanel.setPreferredSize(new Dimension(270, 270));
        devicePanel.setLayout(new BorderLayout(0, 10));

        lblMessage.setName("lblMessage"); 
        devicePanel.add(lblMessage, BorderLayout.NORTH);

        deviceScroll.setName("deviceScroll"); 

        deviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deviceList.setName("deviceList"); 
        deviceList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                deviceListValueChanged(evt);
            }
        });
        deviceScroll.setViewportView(deviceList);

        devicePanel.add(deviceScroll, BorderLayout.CENTER);

        statusScroll.setName("statusScroll"); 

        statusConsole.setEditable(false);
        statusConsole.setColumns(20);
        statusConsole.setRows(5);
        statusConsole.setName("statusConsole"); 
        statusScroll.setViewportView(statusConsole);

        devicePanel.add(statusScroll, BorderLayout.SOUTH);

        getContentPane().add(devicePanel, BorderLayout.CENTER);

        optionPanel.setMaximumSize(new Dimension(270, 40));
        optionPanel.setMinimumSize(new Dimension(270, 40));
        optionPanel.setName("optionPanel"); 
        optionPanel.setPreferredSize(new Dimension(270, 40));
        optionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 5));

        btnSearch.setText("Search");
        btnSearch.setName("btnSearch"); 
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        optionPanel.add(btnSearch);

        getContentPane().add(optionPanel, BorderLayout.NORTH);

        deletePanel.setMaximumSize(new Dimension(60, 40));
        deletePanel.setMinimumSize(new Dimension(60, 40));
        deletePanel.setName("deletePanel"); 
        deletePanel.setPreferredSize(new Dimension(60, 40));

        btnClear.setText("Clear");
        btnClear.setName("btnClear"); 
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        deletePanel.add(btnClear);

        getContentPane().add(deletePanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Method for searching Bluetooth devices.
     * @param evt the ActionEvent
     */
    private void btnSearchActionPerformed(ActionEvent evt) {
        try {
            Printer.initModel(listModel, deviceList);
            Printer.initMessage();
            Printer.setStatus("Searching for devices");            
            
            bluetoothServiceDiscovery = new ServiceDiscovery(listModel);

            LocalDevice localDevice = LocalDevice.getLocalDevice();
            
            Printer.setMessage("Local device: " + localDevice.getFriendlyName());
            
            agent = localDevice.getDiscoveryAgent();
                        
            agent.startInquiry(DiscoveryAgent.GIAC, bluetoothServiceDiscovery);

            ServiceDiscovery.initInquiry();
            ServiceDiscovery.printDevices();
        }
        catch(IOException e){
            e.getMessage();
        }
    }

    /**
     * Method for cleaning the application's GUI.
     * @param evt the Action
     */
    private void btnClearActionPerformed(ActionEvent evt) {
       Printer.initMessage();
       Printer.setStatus("");
    }

    /**
     * Method for selecting a specific Bluetooth device.
     * @param evt the ListSelection
     */
    private void deviceListValueChanged(ListSelectionEvent evt) {
        try {
            int selected = deviceList.getSelectedIndex();
            
            Printer.setStatus("Selected device: " + deviceList.getSelectedValue().toString());
            
            ServiceDiscovery.setSelectedDevice(selected);
            ServiceDiscovery.checkServices(agent, bluetoothServiceDiscovery);
        }
        catch (IOException ex) {            
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(BlueSD.class.getName()).log(Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(() -> new BlueSD().setVisible(true));
    }
}