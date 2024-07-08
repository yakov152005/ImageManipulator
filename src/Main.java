import image.ImageManipulationApp;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageManipulationApp app = new ImageManipulationApp();
            app.setVisible(true);
        });
    }
}