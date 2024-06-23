import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageManipulator extends JFrame {
    private BufferedImage image;
    private BufferedImage originalImage;
    private JLabel imageLabel;

    public ImageManipulator() {
        super("Image Manipulator");

        JButton openButton = new JButton("פתח תמונה");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });

        String[] manipulations = {"בחר מניפולציה", "Black-White", "Negative"}; // כל פעם שמוסיפים מנפולציה לא לשכוח להוסיף אותה למערך
        JComboBox<String> manipulationBox = new JComboBox<>(manipulations);
        manipulationBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> combo = (JComboBox<String>) e.getSource();
                String selectedManipulation = (String) combo.getSelectedItem();
                System.out.println("Selected Manipulation: " + selectedManipulation); // Log the selected manipulation
                if (image != null) {
                    restoreOriginalImage(); // כאן שיחזרתי את התמונה המקורית בכל פעם שאני רוצה להכנס לIF אחר כדי שיעשה עליו את המניפולציה
                    if (selectedManipulation.equals("Black-White")) {
                        convertToBlackAndWhite();
                    }
                    if (selectedManipulation.equals("Negative")) {
                        convertNegative();
                    }
                    // כאן תוסיפו if של מנפולציה אחרת כמובן צריך לעשות לה מתודה נפרדת כמו האלה למעלה
                }
            }
        });

        imageLabel = new JLabel();

        JPanel controlPanel = new JPanel();// זה הפאנל לכפתורים של התפריט הנפתח
        controlPanel.add(openButton);
        controlPanel.add(new JLabel("Survived Status:"));
        controlPanel.add(manipulationBox);


        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);


        setSize(1000, 800); // גודל החלון של התמונה בסוף הפרויקט אני אשנה הכל לפינאלים ואסדר
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void openImage() {
        // יצירת בחירת קובץ
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(selectedFile);
                originalImage = ImageIO.read(selectedFile); // שמרתי פה את העתק של התמונה המקורית כדי שיהיה אפשר לעשות עליה מחדש עריכות
                imageLabel.setIcon(new ImageIcon(image));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "שגיאה בפתיחת התמונה", "שגיאה", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreOriginalImage() {
        // לשחזר את התמונה המקורית
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                image.setRGB(x, y, originalImage.getRGB(x, y));
            }
        }
    }

    private void convertToBlackAndWhite() {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));

                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                int gray = (r + g + b) / 3;
                Color grayColor = new Color(gray, gray, gray);

                image.setRGB(x, y, grayColor.getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    public void convertNegative() {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));

                int r = 255 - color.getRed();
                int g = 255 - color.getGreen();
                int b = 255 - color.getBlue();
                Color negativeColor = new Color(r, g, b);

                image.setRGB(x, y, negativeColor.getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }
}