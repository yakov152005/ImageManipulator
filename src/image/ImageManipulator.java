package image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;

import static utilz.Constants.SizeImage.*;
import static utilz.Constants.Manipulation.*;
import static utilz.Constants.ColorRGB.*;



public class ImageManipulator extends JFrame {

    private BufferedImage image;
    private BufferedImage originalImage;
    private JButton divideImageButton;
    private JLabel imageLabel;
    private boolean isDividingImage = false;
    private int divisionX = -30;

    public ImageManipulator() {
        super(TEXT_1);

        JButton openButton = new JButton(TEXT_2);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });


        divideImageButton = new JButton(TEXT_7);
        divideImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDivideImage();
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

        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image != null) {
                    g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
                    if (isDividingImage && divisionX != -1) {
                        g.setColor(Color.RED);
                        g.drawLine(divisionX, 0, divisionX, this.getHeight());
                    }
                }
            }
        };

        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isDividingImage) {
                    divisionX = e.getX();
                }
                repaint();
            }

        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(openButton);
        controlPanel.add(new JLabel(TEXT_4));
        controlPanel.add(manipulationBox);
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

    private void toggleDivideImage() {
        isDividingImage = !isDividingImage;
        divideImageButton.setText((TEXT_7)+ (isDividingImage ? TEXT_8 : TEXT_9));
        if (!isDividingImage) {
            restoreOriginalImage();
        }
    }

    private void manipulateImage(PixelManipulator manipulator, int startX, int endX) {
        if (image == null) return;

        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = startX; x < endX && x < width; x++) {
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
        }, 0, divisionX);
    }

    private void convertNegative() {
        manipulateImage(color -> {
            int r = MAX - color.getRed();
            int g = MAX - color.getGreen();
            int b = MAX - color.getBlue();
            return new Color(r, g, b);
        }, 0, divisionX);
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
        }, 0, divisionX);
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

    private void posterize() {
        manipulateImage(color -> {
            int r = (color.getRed() / DEF_3) * DEF_3;
            int g = (color.getGreen() / DEF_3) * DEF_3;
            int b = (color.getBlue() / DEF_3) * DEF_3;
            return new Color(r, g, b);
        }, 0, divisionX);
    }

    private void tint() {
        manipulateImage(color -> {
            int r = (color.getRed() + DEF_50) > MAX ? MAX : (color.getRed() + DEF_50);
            int g = color.getGreen();
            int b = color.getBlue();
            return new Color(r, g, b);
        }, 0, divisionX);
    }

    private void colorShiftRight() {
        manipulateImage(color -> new Color(color.getBlue(), color.getRed(), color.getGreen()), 0, divisionX);
    }

    private void colorShiftLeft() {
        manipulateImage(color -> new Color(color.getGreen(), color.getBlue(), color.getRed()), 0, divisionX);
    }

    private void pixelate() {
        int pixelSize = DEF_10;
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y += pixelSize) {
            for (int x = 0; x < divisionX; x += pixelSize) {
                int pixelX = Math.min(x + pixelSize, divisionX);
                int pixelY = Math.min(y + pixelSize, height);

                Color averageColor = getAverageColor(x, y, pixelX, pixelY);
                fillRect(x, y, pixelX, pixelY, averageColor);
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    private Color getAverageColor(int startX, int startY, int endX, int endY) {
        int sumR = 0, sumG = 0, sumB = 0, count = 0;

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                Color color = new Color(image.getRGB(x, y));
                sumR += color.getRed();
                sumG += color.getGreen();
                sumB += color.getBlue();
                count++;
            }
        }

        return new Color(sumR / count, sumG / count, sumB / count);
    }

    private void fillRect(int startX, int startY, int endX, int endY, Color color) {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                image.setRGB(x, y, color.getRGB());
            }
        }
    }

    private void showBorders() {
        int width = image.getWidth();
        int height = image.getHeight();
        int threshold =  DEF_5;

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < divisionX - 1; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb);

                int gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                Color leftColor = new Color(image.getRGB(x - 1, y));
                Color rightColor = new Color(image.getRGB(x + 1, y));
                Color topColor = new Color(image.getRGB(x, y - 1));
                Color bottomColor = new Color(image.getRGB(x, y + 1));

                int leftGray = (leftColor.getRed() + leftColor.getGreen() + leftColor.getBlue()) / 3;
                int rightGray = (rightColor.getRed() + rightColor.getGreen() + rightColor.getBlue()) / 3;
                int topGray = (topColor.getRed() + topColor.getGreen() + topColor.getBlue()) / 3;
                int bottomGray = (bottomColor.getRed() + bottomColor.getGreen() + bottomColor.getBlue()) / 3;

                if (Math.abs(gray - leftGray) > threshold || Math.abs(gray - rightGray) > threshold ||
                        Math.abs(gray - topGray) > threshold || Math.abs(gray - bottomGray) > threshold) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }



    private void eliminateRed() {
        manipulateImage(color -> new Color(0, color.getGreen(), color.getBlue()), 0, divisionX);
    }

    private void eliminateGreen() {
        manipulateImage(color -> new Color(color.getRed(), 0, color.getBlue()), 0, divisionX);
    }

    private void eliminateBlue() {
        manipulateImage(color -> new Color(color.getRed(), color.getGreen(), 0), 0, divisionX);
    }

    private void contrast() {
        manipulateImage(color -> {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            r = (int) ((r - DEF_128) * DEF_1_2 + DEF_128);
            g = (int) ((g - DEF_128) * DEF_1_2 + DEF_128);
            b = (int) ((b - DEF_128) * DEF_1_2 + DEF_128);

            r = Math.min(Math.max(r, 0), MAX);
            g = Math.min(Math.max(g, 0), MAX);
            b = Math.min(Math.max(b, 0), MAX);

            return new Color(r, g, b);
        }, 0, divisionX);
    }

    private void lighten() {
        manipulateImage(color -> {
            int r = Math.min(color.getRed() + DEF_30, MAX);
            int g = Math.min(color.getGreen() + DEF_30, MAX);
            int b = Math.min(color.getBlue() + DEF_30, MAX);
            return new Color(r, g, b);
        }, 0, divisionX);
    }

    private void darken() {
        manipulateImage(color -> {
            int r = Math.max(color.getRed() - DEF_30, 0);
            int g = Math.max(color.getGreen() - DEF_30, 0);
            int b = Math.max(color.getBlue() - DEF_30, 0);
            return new Color(r, g, b);
        }, 0, divisionX);
    }

    private void vignette() {
        int width = image.getWidth();
        int height = image.getHeight();
        int centerX = width / DEF_2;
        int centerY = height / DEF_2;
        int maxDistance = Math.max(centerX, centerY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < divisionX; x++) {
                int distance = (int) Math.sqrt(Math.pow(x - centerX, DEF_2) + Math.pow(y - centerY, DEF_2));
                float ratio = (float) distance / maxDistance;

                Color color = new Color(image.getRGB(x, y));

                int r = (int) (color.getRed() * (1 - ratio));
                int g = (int) (color.getGreen() * (1 - ratio));
                int b = (int) (color.getBlue() * (1 - ratio));

                Color newColor = new Color(Math.max(0, r), Math.max(0, g), Math.max(0, b));
                image.setRGB(x, y, newColor.getRGB());
            }
        }
        imageLabel.setIcon(new ImageIcon(image));
    }

    private void addNoise() {
        manipulateImage(color -> {
            int noise = (int) (Math.random() * DEF_50) - DEF_25;
            int r = Math.min(Math.max(color.getRed() + noise, 0), MAX);
            int g = Math.min(Math.max(color.getGreen() + noise, 0), MAX);
            int b = Math.min(Math.max(color.getBlue() + noise, 0), MAX);
            return new Color(r, g, b);
        }, 0, divisionX);
    }

    private void solarize() {
        manipulateImage(color -> {
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            if (r > DEF_128) {r = MAX - r;}
            if (g > DEF_128) {g = MAX - g;}
            if (b > DEF_128) {b = MAX - b;}

            return new Color(r, g, b);
        }, 0, divisionX);
    }

    private void vintage() {
        convertSepia();
        addNoise();
    }

}

