import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebScraper extends JFrame {
    private JTextField websiteTextField;
    private JTextField tagsTextField;
    private JTextArea resultTextArea;

    public WebScraper() {
        super("Web Scraper");

        websiteTextField = new JTextField(20);
        websiteTextField.setPreferredSize(new Dimension(20, 60));
        websiteTextField.setFont(new Font("Arial", Font.BOLD, 18));
        websiteTextField.setHorizontalAlignment(JTextField.CENTER);
        websiteTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String website = websiteTextField.getText();
                if (!website.startsWith("https://")) {
                    websiteTextField.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                    JOptionPane.showMessageDialog(WebScraper.this, "Enter valid website with https://", "Error",
                            JOptionPane.ERROR_MESSAGE);

                } else {
                    websiteTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    websiteTextField.setToolTipText(null);
                }
            }
        });

        tagsTextField = new JTextField(20);
        tagsTextField.setPreferredSize(new Dimension(20, 60));
        tagsTextField.setFont(new Font("Arial", Font.BOLD, 18));
        tagsTextField.setHorizontalAlignment(JTextField.CENTER);
        resultTextArea = new JTextArea(20, 40);
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        JLabel websiteLabel = new JLabel("Website to scrape (in this format: https://www.google.com):");
        JLabel tagsLabel = new JLabel("HTML tag(s) to scrape (separated by spaces) without any angle brackets:");
        JButton scrapeButton = new JButton("Scrape");
        scrapeButton.addActionListener(new ScrapeButtonListener());

        JPanel inputsPanel = new JPanel();
        inputsPanel.setLayout(new BoxLayout(inputsPanel, BoxLayout.Y_AXIS));
        inputsPanel.add(websiteLabel);
        inputsPanel.add(websiteTextField);
        inputsPanel.add(tagsLabel);
        inputsPanel.add(tagsTextField);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(scrapeButton);
        inputsPanel.add(buttonsPanel);

        JButton saveButton = new JButton("Save Data");
        saveButton.addActionListener(new SaveButtonListener());
        inputsPanel.add(new JLabel());
        buttonsPanel.add(saveButton);
        saveButton.setBackground(Color.GREEN);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(inputsPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.setBackground(Color.GRAY);

        websiteLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tagsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scrapeButton.setBackground(Color.GREEN);
        scrapeButton.setForeground(Color.WHITE);
        scrapeButton.setFont(new Font("Arial", Font.BOLD, 16));

        this.setTitle("Web Wrangler");
        this.setContentPane(content);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Scraped Data");
            int userSelection = fileChooser.showSaveDialog(WebScraper.this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(fileToSave)) {
                    writer.print(resultTextArea.getText());
                } catch (FileNotFoundException e) {
                    JOptionPane.showMessageDialog(WebScraper.this, "Error saving file: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    class ScrapeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String website = websiteTextField.getText();
            String[] tagArray = tagsTextField.getText().split(" ");

            if (!website.startsWith("https://")) {
                websiteTextField.setBorder(BorderFactory.createLineBorder(Color.RED));
                JOptionPane.showMessageDialog(WebScraper.this, "Enter valid website with https://", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                websiteTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                Font font = new Font("Verdana", Font.BOLD, 20);
                resultTextArea.setFont(font);
                resultTextArea.setText("Loading...");

                Timer timer = new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Font regular = new Font("Arial", Font.PLAIN, 12);
                        resultTextArea.setFont(regular);
                        resultTextArea.setText("");
                        try {
                            Document document = Jsoup.connect(website).get();

                            for (String tag : tagArray) {
                                Elements elements = document.select(tag);
                                for (int i = 0; i < elements.size(); i++) {
                                    resultTextArea.append(elements.get(i) + "\n");
                                }
                            }
                        } catch (IOException e1) {
                            resultTextArea.setText("Error connecting to website: " + e1.getMessage());
                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }

    }

    public static void main(String[] args) {
        WebScraper window = new WebScraper();
        window.setVisible(true);
    }
}
