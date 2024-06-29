import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static utilz.Constants.SizeImage.*;
import static utilz.Constants.Manipulation.*;
import static utilz.Constants.ColorRGB.*;

public class ImageManipulator extends JFrame {

    private BufferedImage image;
    private BufferedImage originalImage;
    private JLabel imageLabel;

    public ImageManipulator() {
        super(TEXT_1);

        JButton openButton = new JButton(TEXT_2);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });

        JComboBox<String> manipulationBox = new JComboBox<>(MANIPULATIONS);
        manipulationBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> combo = (JComboBox<String>) e.getSource();
                String selectedManipulation = (String) combo.getSelectedItem();
                System.out.println(TEXT_3 + selectedManipulation);
                if (image != null) {
                    restoreOriginalImage();
                    switch (selectedManipulation) {
                        case C1:
                            restoreOriginalImage();
                            break;
                        case C2:
                            convertToGrayscal();
                            break;
                        case C3:
                            convertNegative();
                            break;
                        case C4:
                            convertSepia();
                            break;
                        case C5:
                            convertToMirror();
                            break;
                        // כאן תוסיפו CASE של מנפולציה אחרת כמובן צריך לעשות לה מתודה נפרדת כמו האלה למעלה
                        case null:
                            break;
                        default:
                            restoreOriginalImage();
                            break;
                    }
                }
            }
        });

        imageLabel = new JLabel();

        JPanel controlPanel = new JPanel();// זה הפאנל לכפתורים של התפריט הנפתח
        controlPanel.add(openButton);
        controlPanel.add(new JLabel(TEXT_4));
        controlPanel.add(manipulationBox);


        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);


        setSize(WIDTH_DEFAULT, HEIGHT_DEFAULT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                image = ImageIO.read(selectedFile);
                originalImage = ImageIO.read(selectedFile); // שמרתי פה את העתק של התמונה המקורית כדי שיהיה אפשר לעשות עליה מחדש עריכות
                imageLabel.setIcon(new ImageIcon(image));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, TEXT_5, TEXT_6, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreOriginalImage() {
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                image.setRGB(x, y, originalImage.getRGB(x, y));
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    private void manipulateImage(PixelManipulator manipulator) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                Color newColor = manipulator.manipulate(color);
                image.setRGB(x, y, newColor.getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    private void convertToGrayscal() {
        manipulateImage(color -> {
            int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
            return new Color(gray, gray, gray);
        });
    }

    private void convertNegative() {
        manipulateImage(color -> {
            int r = MAX - color.getRed();
            int g = MAX - color.getGreen();
            int b = MAX - color.getBlue();
            return new Color(r, g, b);
        });
    }

    private void convertSepia() {
        manipulateImage(color -> {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            if (tr > MAX) {r = MAX;} else {r = tr;}
            if (tg > MAX) {g = MAX;} else {g = tg;}
            if (tb > MAX) {b = MAX;} else {b = tb;}

            return new Color(r, g, b);
        });
    }

    private void convertToMirror() {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width / 2; x++) {
                int mirrorX = width - x - 1;


                int originalRGB = image.getRGB(x, y);
                int mirrorRGB = image.getRGB(mirrorX, y);

                image.setRGB(x, y, mirrorRGB);
                image.setRGB(mirrorX, y, originalRGB);
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    // כביכןל ככה צריך לעשות אם רוצים לשנות ממש את ערכי האיקס והוואי
    // למעלה השתמשתי בממשק שמשותף לכולם אבל יש דברים ספציפים כמו mirror וכו שחייב לרוץ מחדש על הלולאות^
//    private void convertToGrayscal() {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = new Color(image.getRGB(x, y));
//
//                int r = color.getRed();
//                int g = color.getGreen();
//                int b = color.getBlue();
//
//                int gray = (r + g + b) / 3;
//                Color grayscalColor = new Color(gray, gray, gray);
//
//                image.setRGB(x, y, grayscalColor.getRGB());
//            }
//        }
//        imageLabel.setIcon(new ImageIcon(image));
//    }


}