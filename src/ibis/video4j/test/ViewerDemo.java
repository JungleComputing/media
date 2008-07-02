package ibis.video4j.test;

import ibis.video4j.VideoDeviceDescription;
import ibis.video4j.VideoDeviceFactory;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewerDemo extends JPanel implements ActionListener {

    // Generated
    private static final long serialVersionUID = 7697445736367043254L;

    private VideoDeviceDescription [] devices; 
    
    private JComboBox deviceList;
    
    private VideoStream videoStream;
    
  //  private JLabel picture;
    
    public ViewerDemo() { 
        
        super(new BorderLayout());
        
        // Find all video devices
        try { 
            devices = VideoDeviceFactory.availableDevices(); 
        } catch (Exception e) {
            devices = new VideoDeviceDescription[0];
        }
        
        // Create an array with all options for the combo box. 
        Object [] tmp = { "No devices found!" };
        
        if (devices.length > 0) { 
            
            tmp = new Object[devices.length+1];
            
            tmp[0] = "None";
            
            for (int i=0;i<devices.length;i++) { 
                tmp[i+1] = devices[i];
            }
        }
        
        // Create the combo box, select the item at index 0 (Item "none").
        deviceList = new JComboBox(tmp);
        deviceList.setSelectedIndex(0);
        deviceList.addActionListener(this);
        
        // Create the video panel
        videoStream = new VideoStream(352, 288);
        
        // Lay out the demo.
        add(deviceList, BorderLayout.PAGE_START);
        add(videoStream, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
    
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();

        Object tmp = cb.getSelectedItem();
        
        if (tmp instanceof VideoDeviceDescription) {
            VideoDeviceDescription d = (VideoDeviceDescription) tmp;
            System.out.println("Selected device: " + d.getSimpleDescription());
            
            try { 
                videoStream.selectDevice(d.deviceNumber);
            } catch (Exception ex) {
                videoStream.setMessage("Failed to select device " + d.deviceNumber);
            
                ex.printStackTrace();
            }
        } else { 
            String s = (String) tmp;
            System.out.println("Selected special option: " + s);
            
            try {
                videoStream.selectDevice(-1);
            } catch (Exception e1) {
                // ignored
            }
        }   
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Video4JDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ViewerDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
      