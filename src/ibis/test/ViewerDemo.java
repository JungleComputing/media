package ibis.test;

import ibis.imaging4j.Format;
import ibis.video4j.VideoDeviceDescription;
import ibis.video4j.VideoDeviceFactory;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewerDemo extends JPanel implements ActionListener {

    // Generated
    private static final long serialVersionUID = 7697445736367043254L;

    private VideoDeviceDescription [] devices;
    
    private final JButton button = new JButton("GO!");
    private final JComboBox deviceList;
    private final JComboBox formatList;
    
    private final VideoStream videoStream;
    
    private static int width = 352;
    private static int height = 288;
    
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
    
        tmp = new Object [] { "No format available!" };
    
        formatList = new JComboBox(tmp);
        formatList.setSelectedIndex(0);
        formatList.addActionListener(this);
                
        // Create the video panel
        videoStream = new VideoStream(width, height);
        
        // Lay out the demo.
        
        button.addActionListener(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(deviceList, BorderLayout.WEST);
        panel.add(formatList, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        
        add(panel, BorderLayout.PAGE_START);
        
        //add(deviceList, BorderLayout.PAGE_START);
        //add(formatList, BorderLayout.PAGE_START);
        
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel2.add(videoStream);
        
        add(panel2, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
    
    public void actionPerformed(ActionEvent e) {
        Object cb = e.getSource();
        
        if (cb == deviceList) { 

            Object tmp = ((JComboBox)cb).getSelectedItem();

            if (tmp instanceof VideoDeviceDescription) {
            
                VideoDeviceDescription d = (VideoDeviceDescription) tmp;
                System.out.println("Selected device: " + d.getSimpleDescription());

/*                
                
                try { 
                    videoStream.selectDevice(d.deviceNumber);
                } catch (Exception ex) {
                    videoStream.setMessage("Failed to select device " + d.deviceNumber);

                    ex.printStackTrace();
                }
*/
                Format [] palette = d.getFormats();
                
                formatList.removeAllItems();
                
                if (palette.length > 0) {
                    
                    for (int i=0;i<palette.length;i++) { 
                        formatList.addItem(palette[i]);
                    }
                    
                    formatList.addItem("None");
                    
                } else { 
                    formatList.addItem("No format available!");
                }
                
            } else { 
                String s = (String) tmp;
                System.out.println("Selected special option: " + s);

                formatList.removeAllItems();
                formatList.addItem("No format available!");

                /*
                try {
                    videoStream.selectDevice(-1);
                } catch (Exception e1) {
                    // ignored
                }
                */                
            }
            
        } else if (cb == formatList) { 
            
            Object tmp = ((JComboBox)cb).getSelectedItem();

            System.out.println("Selected format: " + tmp);
            
        } else if (cb == button) { 
            
            Object device = deviceList.getSelectedItem();
            Object format = formatList.getSelectedItem();
            
            if (device instanceof VideoDeviceDescription) {
                VideoDeviceDescription d = (VideoDeviceDescription) device;
                System.out.println("Selected device: " + d.getSimpleDescription());

                if (format instanceof Format) { 
                    try { 
                        videoStream.selectDevice(d.deviceNumber, (Format) format);
                        return;
                    } catch (Exception ex) {
                        videoStream.setMessage("Failed to select device " + d.deviceNumber);
                        ex.printStackTrace();
                    }
                } else { 
                    videoStream.setMessage("No palette selected!");
                }                
            } else { 
                videoStream.setMessage("No device selected!");
            }
            
            try {
                videoStream.selectDevice(-1, null);
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
        
        if (args.length == 2) { 
            width = Integer.parseInt(args[0]);
            height = Integer.parseInt(args[1]);
        }
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
      