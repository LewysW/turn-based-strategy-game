import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Locale;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class TroopTransferDialogue {

    public static int display(String title, String msg, int min, int max, boolean cancel, Point2D frameLocation) {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);

        JSlider troopSlider = new JSlider(min, max, min);
        JLabel label = new JLabel(Integer.toString(troopSlider.getValue()), SwingConstants.CENTER);

        troopSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider) e.getSource();
                int numTroops = slider.getValue();
                label.setText(Integer.toString(numTroops));
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel(msg));
        panel.add(label);
        panel.add(troopSlider);

        frame.add(panel);
        frame.requestFocus();
        frame.setLocation((int) frameLocation.getX(), (int) frameLocation.getY());

        String[] options = {"OK"};

        //Get result from slider
        int result = (cancel) ?
                JOptionPane.showConfirmDialog(frame, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)
                : JOptionPane.showOptionDialog(frame, panel, title, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options , options[0]);

        frame.dispose();

        if (result == JOptionPane.OK_OPTION || !cancel) {
            System.out.println("OK");
            return troopSlider.getValue();
        } else {
            System.out.println("Cancelled");
            return 0;
        }
    }
}
