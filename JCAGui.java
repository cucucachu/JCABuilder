import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;

public class JCAGui extends JFrame implements ActionListener {
   private static final String newline = System.lineSeparator();
   private static final String DefaultDirectory = "/Home";
   private static final String welcomeText = "Welcome" + newline; 

   private JPanel mainPanel;
   
   private JPanel recapSelectionPanel;
   private JTextArea recapSelectText;
   private JButton recapSelectButton;
   
   private JPanel jcaSelectionPanel;
   private JTextArea jcaSelectText;
   private JButton jcaSelectButton;
   
   private JTextArea dateTextArea;
   
   private JTextArea log;
   private JScrollPane logScrollPane;
   private JButton runButton;
   
   private JFrame recapChooserPopUp;
   private JFileChooser recapChooser;
   
   private JFrame jcaChooserPopUp;
   private JFileChooser jcaChooser;
   
   private JProgressBar progressBar;

   public JCAGui() {
      super("JCA Compiler");
      guiSetUp();
   }
   
   private void guiSetUp() {
      this.setDefaultCloseOperation(EXIT_ON_CLOSE);
      this.setSize(700, 500);
      this.setResizable(false);
      
      mainPanel = new JPanel();
      
      recapChooser = new JFileChooser();
      recapChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      
      jcaChooser = new JFileChooser();
      jcaChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      
      log = new JTextArea(15, 60);
      log.setMargin(new Insets(5,5,5,5));
      log.setEditable(false);
      log.setLineWrap(true);
      log.setWrapStyleWord(true);
      output(welcomeText);
      
      recapSelectionPanel = new JPanel();
      recapSelectionPanel.setSize(100, 100);
      recapSelectText = new JTextArea("Select the recap file...", 1, 30);
      recapSelectText.setEditable(false);
      recapSelectText.setMargin(new Insets(5,5,5,5));
      recapSelectButton = new JButton("Choose Recap");
      recapSelectButton.addActionListener(this);
      
      jcaSelectionPanel = new JPanel();
      jcaSelectionPanel.setSize(100, 100);
      jcaSelectText = new JTextArea("Select the current JCA Folder...", 1, 30);
      jcaSelectText.setEditable(false);
      jcaSelectText.setMargin(new Insets(5,5,5,5));
      jcaSelectButton = new JButton("Choose JCA Folder");
      jcaSelectButton.addActionListener(this);
      
      dateTextArea = new JTextArea("Week ending on...", 1, 20);
      dateTextArea.setMargin(new Insets(5,5,5,5));
      dateTextArea.setEditable(true);
      
      logScrollPane = new JScrollPane(log);
      logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      runButton = new JButton("Start");
      runButton.addActionListener(this);
      
      recapSelectionPanel.add(recapSelectText);
      recapSelectionPanel.add(recapSelectButton);
      
      jcaSelectionPanel.add(jcaSelectText);
      jcaSelectionPanel.add(jcaSelectButton);
      
      mainPanel.add(recapSelectionPanel);
      mainPanel.add(jcaSelectionPanel);
      mainPanel.add(dateTextArea);
      mainPanel.add(logScrollPane);
      mainPanel.add(runButton);
      
      this.add(mainPanel);
      
      this.setVisible(true);
   }
   
   public void actionPerformed(ActionEvent e) {
      String source = e.getActionCommand();
      if (source.compareTo("Start") == 0) {
         try {
            JCABuilder.buildJCA(jcaChooser.getSelectedFile().getAbsolutePath(),
                recapChooser.getSelectedFile().getAbsolutePath(),
                dateTextArea.getText());
         }
         catch (Exception ex) {
            output("Please select the JCA folder and Recap file first.");
         }
      }
      else if (source.compareTo("Choose Recap") == 0) {
         openRecapChooser();
      }
      else if (source.compareTo("Choose JCA Folder") == 0) {
         openJcaChooser();
      }
   }
   
   private void openRecapChooser() {
      JFrame fileChooserFrame = new JFrame();
      fileChooserFrame.setSize(500, 300);
      fileChooserFrame.setResizable(false);
      fileChooserFrame.add(recapChooser);
      fileChooserFrame.setVisible(true);
      
      recapChooser.showOpenDialog(fileChooserFrame);
      String filePath = recapChooser.getSelectedFile().getAbsolutePath();
      recapSelectText.setText(filePath);
      fileChooserFrame.setVisible(false);
   }
   
   private void openJcaChooser() {
      JFrame fileChooserFrame = new JFrame();
      fileChooserFrame.setSize(500, 300);
      fileChooserFrame.setResizable(false);
      fileChooserFrame.add(jcaChooser);
      fileChooserFrame.setVisible(true);
      
      jcaChooser.showOpenDialog(fileChooserFrame);
      String filePath = jcaChooser.getSelectedFile().getAbsolutePath();
      jcaSelectText.setText(filePath);
      fileChooserFrame.setVisible(false);
   }
   
   public void output(String text) {
      log.append(text + newline);
   }
}
