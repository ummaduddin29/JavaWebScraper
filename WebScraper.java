import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
        tagsTextField = new JTextField(20);
        resultTextArea = new JTextArea(20, 40);
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        JLabel websiteLabel = new JLabel("Website to scrape (in this format: https://www.google.com):");
        JLabel tagsLabel = new JLabel("HTML tag(s) to scrape (separated by spaces) without any angle brackets:");
        JButton scrapeButton = new JButton("Scrape");
        scrapeButton.addActionListener(new ScrapeButtonListener());

        JPanel inputsPanel = new JPanel();
        inputsPanel.setLayout(new GridLayout(3, 2));
        inputsPanel.add(websiteLabel);
        inputsPanel.add(websiteTextField);
        inputsPanel.add(tagsLabel);
        inputsPanel.add(tagsTextField);
        inputsPanel.add(new JLabel());
        inputsPanel.add(scrapeButton);

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

        this.setContentPane(content);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class ScrapeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            resultTextArea.setText("");
            String website = websiteTextField.getText();
            String tags = tagsTextField.getText();

            try {
                Document document = Jsoup.connect(website).get();
                Elements elements = document.select(tags);

                for (int i = 0; i < elements.size(); i++) {
                    resultTextArea.append(elements.get(i) + "\n");
                }
            } catch (IOException e) {
                resultTextArea.setText("Error connecting to website: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        WebScraper window = new WebScraper();
        window.setVisible(true);
    }
}
