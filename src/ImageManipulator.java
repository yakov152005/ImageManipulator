import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilz.Constants.SizeImage.*;
import static utilz.Constants.Manipulation.*;
import static utilz.Constants.ColorRGB.*;

public class ImageManipulator extends JFrame {

    private BufferedImage image;
    private BufferedImage originalImage;
    private JButton pointSelectionButton;
    private JButton divideImageButton;
    private JLabel imageLabel;
    private List<Point> points = new ArrayList<>();

    public ImageManipulator() {
        super(TEXT_1);

        JButton openButton = new JButton(TEXT_2);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });

        // כפתור לבחירת סימון נקודות
        pointSelectionButton = new JButton(TEXT_7);
        pointSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                enablePointSelection();
            }
        });

        // כפתור לחלוקת תמונה
        divideImageButton = new JButton(TEXT_8);
        divideImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                enableDivideImage();
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
                        case C6:
                            posterize();
                            break;
                        case C7:
                            tint();
                            break;
                        case C8:
                            colorShiftRight();
                            break;
                        case C9:
                            colorShiftLeft();
                            break;
                        case C10:
                            pixelate();
                            break;
                        case C11:
                            showBorders();
                            break;
                        case C12:
                            eliminateRed();
                            break;
                        case C13:
                            eliminateGreen();
                            break;
                        case C14:
                            eliminateBlue();
                            break;
                        case C15:
                            contrast();
                            break;
                        case C16:
                            lighten();
                            break;
                        case C17:
                            darken();
                            break;
                        case C18:
                            vignette();
                            break;
                        case C19:
                            addNoise();
                            break;
                        case C20:
                            solarize();
                            break;
                        case C21:
                            vintage();
                            break;
                        default:
                            restoreOriginalImage();
                            break;
                    }
                }
            }
        });

        imageLabel = new JLabel();

        JPanel controlPanel = new JPanel();
        controlPanel.add(openButton);
        controlPanel.add(new JLabel(TEXT_4));
        controlPanel.add(manipulationBox);
        controlPanel.add(pointSelectionButton);
        controlPanel.add(divideImageButton);

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
                originalImage = ImageIO.read(selectedFile);
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

    // פונקציות פילטרים נוספות
    private void posterize() {
        manipulateImage(color -> {
            int r = (color.getRed() / DEF_3) * DEF_3;
            int g = (color.getGreen() / DEF_3) * DEF_3;
            int b = (color.getBlue() / DEF_3) * DEF_3;
            return new Color(r, g, b);
        });
    }

    private void tint() {
        manipulateImage(color -> {
            int r = (color.getRed() + (DEF_1*2)) > MAX ? MAX : color.getRed() + (DEF_1*2);
            int g = (color.getGreen() + DEF_1) > MAX ? MAX : color.getGreen() + DEF_1;
            int b = color.getBlue();
            return new Color(r, g, b);
        });
    }

    private void colorShiftRight() {
        manipulateImage(color -> new Color(color.getGreen(), color.getBlue(), color.getRed()));
    }

    private void colorShiftLeft() {
        manipulateImage(color -> new Color(color.getBlue(), color.getRed(), color.getGreen()));
    }

    private void pixelate() {
        int pixelSize = 10;
        for (int y = 0; y < image.getHeight(); y += pixelSize) {
            for (int x = 0; x < image.getWidth(); x += pixelSize) {
                int pixelColor = image.getRGB(x, y);
                for (int dy = 0; dy < pixelSize && y + dy < image.getHeight(); dy++) {
                    for (int dx = 0; dx < pixelSize && x + dx < image.getWidth(); dx++) {
                        image.setRGB(x + dx, y + dy, pixelColor);
                    }
                }
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    private void showBorders() {
        manipulateImage(color -> {
            int threshold = 30;
            int r = Math.abs(color.getRed() - DEF_2) > threshold ? 0 : MAX;
            int g = Math.abs(color.getGreen() - DEF_2) > threshold ? 0 : MAX;
            int b = Math.abs(color.getBlue() - DEF_2) > threshold ? 0 : MAX;
            return new Color(r, g, b);
        });
    }

    private void eliminateRed() {
        manipulateImage(color -> new Color(0, color.getGreen(), color.getBlue()));
    }

    private void eliminateGreen() {
        manipulateImage(color -> new Color(color.getRed(), 0, color.getBlue()));
    }

    private void eliminateBlue() {
        manipulateImage(color -> new Color(color.getRed(), color.getGreen(), 0));
    }

    private void contrast() {
        manipulateImage(color -> {
            int r = color.getRed() > DEF_2 ? MAX : 0;
            int g = color.getGreen() > DEF_2 ? MAX : 0;
            int b = color.getBlue() > DEF_2 ? MAX : 0;
            return new Color(r, g, b);
        });
    }

    private void lighten() {
        manipulateImage(color -> {
            int r = Math.min(MAX, color.getRed() + DEF_1);
            int g = Math.min(MAX, color.getGreen() + DEF_1);
            int b = Math.min(MAX, color.getBlue() + DEF_1);
            return new Color(r, g, b);
        });
    }

    private void darken() {
        manipulateImage(color -> {
            int r = Math.max(0, color.getRed() - DEF_1);
            int g = Math.max(0, color.getGreen() - DEF_1);
            int b = Math.max(0, color.getBlue() - DEF_1);
            return new Color(r, g, b);
        });
    }

    private void vignette() {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxDistance = (int) Math.sqrt(width * width + height * height) / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int distance = (int) Math.sqrt(Math.pow(x - width / 2, 2) + Math.pow(y - height / 2, 2));
                double ratio = (double) distance / maxDistance;

                Color color = new Color(image.getRGB(x, y));
                int r = (int) (color.getRed() * (1 - ratio));
                int g = (int) (color.getGreen() * (1 - ratio));
                int b = (int) (color.getBlue() * (1 - ratio));
                image.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    private void addNoise() {
        manipulateImage(color -> {
            int noise = (int) (Math.random() * 100) - DEF_1;
            int r = Math.min(MAX, Math.max(0, color.getRed() + noise));
            int g = Math.min(MAX, Math.max(0, color.getGreen() + noise));
            int b = Math.min(MAX, Math.max(0, color.getBlue() + noise));
            return new Color(r, g, b);
        });
    }

    private void solarize() {
        manipulateImage(color -> {
            int threshold = DEF_2;
            int r = color.getRed() > threshold ? MAX - color.getRed() : color.getRed();
            int g = color.getGreen() > threshold ? MAX - color.getGreen() : color.getGreen();
            int b = color.getBlue() > threshold ? MAX - color.getBlue() : color.getBlue();
            return new Color(r, g, b);
        });
    }

    private void vintage() {
        convertSepia();
        addNoise();
    }
}
