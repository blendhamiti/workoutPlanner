import javax.swing.*;

public class Client {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGui();
            }
        });
    }

    public static void createAndShowGui() {
        JFrame frame = new JFrame("Workout Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Hello!");
        frame.getContentPane().add(label);

        frame.pack();
        frame.setVisible(true);
    }
}
