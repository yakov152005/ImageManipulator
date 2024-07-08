package image;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static utilz.Constants.SizeImage.*;
import static utilz.Constants.Manipulation.*;
import static utilz.Constants.ColorRGB.*;

public class ImageManipulationApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage displayedImage;
    private JLabel imageLabel;
    private String selectedEffect;
    private int splitX;

    public ImageManipulationApp() {
        initUI();
    }

    private void initUI() {
        this.splitX  = -1;
        this.selectedEffect = "";

        setTitle(TEXT_1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH_DEFAULT, HEIGHT_DEFAULT);
        setLocationRelativeTo(null);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                splitX = e.getX();
                applyEffect();
            }
        });

        JPanel buttonPanel = new JPanel();

        JButton openButton = new JButton(TEXT_2);
        openButton.addActionListener(e -> openImage());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(openButton, BorderLayout.WEST);

        JComboBox<String> effectComboBox = new JComboBox<>(MANIPULATIONS);
        effectComboBox.addActionListener(e -> {
            selectedEffect = (String) effectComboBox.getSelectedItem();
            applyEffect();
        });
        topPanel.add(effectComboBox, BorderLayout.CENTER);


        JButton saveButton = new JButton(TEXT_3);
        saveButton.addActionListener(e -> saveImage());
        topPanel.add(saveButton, BorderLayout.AFTER_LINE_ENDS);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(file);
                displayedImage = originalImage;
                imageLabel.setIcon(new ImageIcon(displayedImage));
            } catch (Exception e) {System.out.println(TEXT_9);}
        }
    }



    private void applyEffect() {
        if (originalImage == null || selectedEffect.isEmpty()) {
            return;
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = originalImage.getRGB(x, y);
                if (splitX != -1 && x < splitX) {
                    rgb = applyEffectToPixel(rgb, selectedEffect);
                }
                resultImage.setRGB(x, y, rgb);
            }
        }

        displayedImage = resultImage;
        imageLabel.setIcon(new ImageIcon(displayedImage));
    }


    private int applyEffectToPixel(int rgb, String selectedEffect) {
        int r = (rgb >> DEF_16) & HEXADECIMAL;
        int g = (rgb >> DEF_8) & HEXADECIMAL;
        int b = rgb & HEXADECIMAL;

        switch (selectedEffect) {
            case C1:
                int gray = (r + g + b) / 3;
                return gray > 127 ? HEX_TRY_COLOR : HEX_NULL;
            case C2:
                gray = (r + g + b) / 3;
                return (gray << DEF_16) | (gray << DEF_8) | gray;
            case C3:
                int numColors = 4;
                int colorStep = (MAX +1 ) / numColors;
                r = (r / colorStep) * colorStep;
                g = (g / colorStep) * colorStep;
                b = (b / colorStep) * colorStep;
                return (r << DEF_16) | (g << DEF_8) | b;
            case C4:
                Color tint = new Color(MAX, DEF_100, DEF_100, DEF_50);
                r = (r + tint.getRed()) / 2;
                g = (g + tint.getGreen()) / 2;
                b = (b + tint.getBlue()) / 2;
                return (r << DEF_16) | (g << DEF_8) | b;
            case C5:
                return (g << DEF_16) | (b << DEF_8) | r;
            case C6:
                return (b << DEF_16) | (r << DEF_8) | g;
            case C7:
                return (0 << DEF_16) | (g << DEF_8) | b;
            case C8:
                return (r << DEF_16) | (0 << DEF_8) | b;
            case C9:
                return (r << DEF_16) | (g << DEF_8) | 0;
            case C10:
                return ((MAX - r) << DEF_16) | ((MAX - g) << DEF_8) | (MAX - b);
            case C11:
                float contrast = DEF_1_2;
                r = Math.min(MAX, Math.max(0, (int)(r * contrast)));
                g = Math.min(MAX, Math.max(0, (int)(g * contrast)));
                b = Math.min(MAX, Math.max(0, (int)(b * contrast)));
                return (r << DEF_16) | (g << DEF_8) | b;
            case C12:
                int tr = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                int tg = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                int tb = (int)(0.272 * r + 0.534 * g + 0.131 * b);
                r = Math.min(MAX, tr);
                g = Math.min(MAX, tg);
                b = Math.min(MAX, tb);
                return (r << DEF_16) | (g << DEF_8) | b;
            case C13:
                float factor = DEF_1_2;
                r = Math.min(MAX, (int)(r * factor));
                g = Math.min(MAX, (int)(g * factor));
                b = Math.min(MAX, (int)(b * factor));
                return (r << DEF_16) | (g << DEF_8) | b;
            case C14:
                factor = DEF_0_8;
                r = (int)(r * factor);
                g = (int)(g * factor);
                b = (int)(b * factor);
                return (r << DEF_16) | (g << DEF_8) | b;
            case C15:
                int noise = (int)(Math.random() * DEF_64) - DEF_32;
                r = Math.min(MAX, Math.max(0, r + noise));
                g = Math.min(MAX, Math.max(0, g + noise));
                b = Math.min(MAX, Math.max(0, b + noise));
                return (r << DEF_16) | (g << DEF_8) | b;
            case C16:
                int threshold = DEF_128;
                if (r > threshold) r = MAX - r;
                if (g > threshold) g = MAX - g;
                if (b > threshold) b = MAX - b;
                return (r << DEF_16) | (g << DEF_8) | b;
            case C17:
                return applyEffectToPixel(applyEffectToPixel(rgb, C15), C12);
        }
        return rgb;
    }



    private void saveImage() {
        if (displayedImage == null) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(TEXT_4, TEXT_5));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String filePath = file.getPath();

                if (!filePath.toLowerCase().endsWith(TEXT_6)) {
                    file = new File(filePath + TEXT_6);
                }

                ImageIO.write(displayedImage, TEXT_5, file);
                JOptionPane.showMessageDialog(this, TEXT_7);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, TEXT_8 + e.getMessage(), TEXT_9, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
