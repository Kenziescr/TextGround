import java.awt.*;
import javax.swing.*;
import javax.swing.undo.UndoManager;
import javax.imageio.ImageIO;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import  java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Objects;


public class App {

    static boolean hasCopied = false;
    static boolean linewrapped = false;
    static File SelectedFile = null;
    static int startFrom;
    static String copied;
    static Image icon;
    public static void main(String[] args) throws Exception {
            try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "LookAndFeel can't be loaded", "Error", JOptionPane.ERROR_MESSAGE);
            }


        JFrame fr = new JFrame();
        fr.setTitle("Text Ground");
        InputStream stream = App.class.getResourceAsStream("/images/icon.png");
        if(stream==null){
            try{
                stream = new FileInputStream("resources/images/icon.png");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Resources can't be loaded", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        if(stream==null){
            JOptionPane.showMessageDialog(null, "Resources can't be loaded", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        try{
            icon = ImageIO.read(stream);
            if(icon==null){
                JOptionPane.showMessageDialog(null, "Icon can't be loaded", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            fr.setIconImage(icon);
        }catch(IOException e){
            JOptionPane.showMessageDialog(null, "Icon failed to be loaded", "Error", JOptionPane.ERROR_MESSAGE);
        }
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setSize(775, 545);

        UndoManager TextHistory = new UndoManager();

        JMenuItem[] fileItems = {new JMenuItem("New"), new JMenuItem("Open"), new JMenuItem("Save"), new JMenuItem("Save As"), new JMenuItem("Exit")};
        JMenuItem[] edItems = {new JMenuItem("Line Wrap: " + Boolean.toString(linewrapped)), new JMenuItem("Font"), new JMenu("Actions")};
        JMenuItem[] acItems = {new JMenuItem("Find and Replace"), new JMenuItem("Copy"), new JMenuItem("Paste"), new JMenuItem("Undo"), new JMenuItem("Redo")};

        JMenuBar menu  = new JMenuBar();
        JMenu fileMn = new JMenu("File");
        JMenu editMn = new JMenu("Edit");
        JMenu helpMn = new JMenu("Help");

        JTextArea textpad = new JTextArea("Hello World!");
        textpad.getDocument().addUndoableEditListener(e -> {
            TextHistory.addEdit(e.getEdit());
        });
        textpad.setLineWrap(linewrapped);
        textpad.setFont(new Font("Arial", Font.PLAIN, 14));

        for(int i=0;i<fileItems.length;i++){
            fileMn.add(fileItems[i]);
            if(i==0){
                // NEW button
                fileItems[0].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        textpad.setText(null);
                        SelectedFile = null;
                    }
                });
                fileMn.addSeparator();
            } else if(i==4){
                // EXIT button
                fileItems[4].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        System.exit(0);
                    }
                });
            } else if(i==1){
                // OPEN button
                fileItems[1].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        // FILE CHOOSER BLOCK
                        FileDialog fileChooser = new FileDialog(fr, "Open File", FileDialog.LOAD);
                        fileChooser.setFile("*.txt");
                        fileChooser.setDirectory(System.getProperty("user.home"));
                        fileChooser.setVisible(true);

                        String dir = fileChooser.getDirectory();
                        String name = fileChooser.getFile();

                        // VALIDATION (SAFETY FIRST CHECKS CUZ WHY NOT)
                        if(!(dir==null)&&!(name==null)){
                            File file = new File(dir, name);

                            if(file.exists() && file.isFile()){
                                try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                                    String line;
                                    String full = "";
                                    while((line=reader.readLine()) != null){
                                        full += line + "\n";
                                    }
                                    textpad.setText(full);
                                    SelectedFile = file;
                                    fr.setTitle("Text Ground - " + file.getName());
                                }catch(Exception exc){
                                    JOptionPane.showMessageDialog(fr, "A Unexpected Error Happened", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }else{
                                JOptionPane.showMessageDialog(fr, "Invalid File", "Error", JOptionPane.ERROR_MESSAGE);
                            }

                        };
                    };
                });
            }else if(i==2){
                // SAVE button
                fileItems[2].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        if (Objects.isNull(SelectedFile)){
                            // FILE CHOOSER BLOCK
                            FileDialog fileChooser = new FileDialog(fr, "Save File", FileDialog.SAVE);
                            fileChooser.setFile("*.txt");
                            fileChooser.setDirectory(System.getProperty("user.home"));
                            fileChooser.setVisible(true);

                            String dir = fileChooser.getDirectory();
                            String name = fileChooser.getFile();

                            // VALIDATION
                            if(!(dir==null)&&!(name==null)){
                                File file = new File(dir, name);

                                if(file.exists()&&file.isFile()){
                                    try (FileWriter writer=new FileWriter(file)) {
                                        writer.write(textpad.getText());
                                        SelectedFile = file;
                                    } catch (Exception exc) {
                                        JOptionPane.showMessageDialog(fr, "An Unexpected Error Happened", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                            } else {
                                try (FileWriter writer=new FileWriter(file)) {
                                    writer.write(textpad.getText());
                                    SelectedFile = file;
                                    fr.setTitle("Text Ground - " + file.getName());
                                } catch (Exception exc) {
                                    JOptionPane.showMessageDialog(fr, "An Unexpected Error Happened", "Error", JOptionPane.ERROR_MESSAGE);                            
                                }
                            }
                            }
                        }else{
                            try (FileWriter writer=new FileWriter(SelectedFile)) {
                                writer.write(textpad.getText());
                            } catch (Exception exc) {
                                JOptionPane.showMessageDialog(fr, "An Unexpected Error Happened", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
            }else if(i==3){
                // SAVE AS button
                fileMn.addSeparator();
                fileItems[3].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                         // FILE CHOOSER BLOCK
                            FileDialog fileChooser = new FileDialog(fr, "Save File", FileDialog.SAVE);
                            fileChooser.setFile("*.txt");
                            fileChooser.setDirectory(System.getProperty("user.home"));
                            fileChooser.setVisible(true);

                            String dir = fileChooser.getDirectory();
                            String name = fileChooser.getFile();

                            // VALIDATION
                            if(!(dir==null)&&!(name==null)){
                                File file = new File(dir, name);


                                try (FileWriter writer=new FileWriter(file)) {
                                    writer.write(textpad.getText());
                                    SelectedFile = file;
                                    fr.setTitle("Text Ground - " + file.getName());
                                } catch (Exception exc) {
                                    JOptionPane.showMessageDialog(fr, "An Unexpected Error Happened", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                    }}
                });
            }
        }
        JMenu aclist = (JMenu) edItems[2];
        for(int i=0;i<acItems.length;i++){
            aclist.add(acItems[i]);
            if(i==0){
                acItems[0].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        JDialog findDlg = new JDialog(fr, "Find");
                        findDlg.setSize(555, 150);
                        findDlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        JPanel formPnl = new JPanel(new GridLayout(2, 2));
                        JPanel btnPnl = new JPanel(new FlowLayout());
                        JTextField find = new JTextField("A");
                        JTextField replc = new JTextField("B");
                        formPnl.add(new JLabel("Find:"));
                        formPnl.add(find);
                        formPnl.add(new JLabel("Replace:"));
                        formPnl.add(replc);
                        JButton findNextBtn = new JButton("Find Next");
                        JButton replcBtn = new JButton( "Replace");
                        JButton replcAllBtn = new JButton("Replace All");
                        JButton closeBtn = new JButton("Close");
                        JButton[] Btns = {findNextBtn, replcBtn, replcAllBtn, closeBtn};
                        for(int i=0;i<Btns.length;i++){
                            btnPnl.add(Btns[ i]);
                        }

                        findNextBtn.addActionListener(new ActionListener() {
                            @Override 
                            public void actionPerformed(ActionEvent e){
                                startFrom = textpad.getSelectionEnd();
                                String wanted = find.getText();
                                String text = textpad.getText();
                                int foundAt = text.indexOf(wanted, startFrom);
                                if(foundAt==-1){
                                    foundAt = text.indexOf(wanted, 0);
                                    if(foundAt==-1){
                                        JOptionPane.showMessageDialog(fr, wanted + " Not Found", "Info", JOptionPane.INFORMATION_MESSAGE);
                                    }else{
                                        textpad.requestFocus();
                                        textpad.setSelectionStart(foundAt);
                                        textpad.setSelectionEnd(foundAt + wanted.length());
                                    }
                                    
                                }else{
                                    textpad.requestFocus();
                                    textpad.setSelectionStart(foundAt);
                                    textpad.setSelectionEnd(foundAt + wanted.length());
                                }
                            }
                        });
                        replcBtn.addActionListener(new ActionListener() {
                            @Override 
                            public void actionPerformed(ActionEvent e){
                                String wanted = find.getText();
                                String replcment = replc.getText();
                                String selected = textpad.getSelectedText();
                                if(selected!=null&&selected.equals(wanted)){
                                    textpad.replaceSelection(replcment);;
                                }
                            }
                        });
                        replcAllBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e){
                                String wanted = find.getText();
                                String replcment = replc.getText();
                                String text = textpad.getText();
                                String newText = text.replace(wanted, replcment);
                                textpad.setText(newText);

                            }
                        });
                        closeBtn.addActionListener(new ActionListener() {
                            @Override 
                            public void actionPerformed(ActionEvent e){
                                findDlg.dispose();
                            }
                        });
                        findDlg.add(formPnl, BorderLayout.NORTH);
                        findDlg.add(btnPnl, BorderLayout.SOUTH);
                        findDlg.setVisible(true);
                    }
                });
            }
            else if(i==1){
                acItems[1].addActionListener(new ActionListener() {
                    @Override 
                    public  void actionPerformed(ActionEvent e){
                        copied = textpad.getSelectedText();
                        hasCopied = true;
                        acItems[2].setEnabled(true);
                    }
                });
            }
            else if(i==2){
                aclist.addSeparator();
                acItems[2].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        if(hasCopied){
                            textpad.replaceSelection(copied);
                        }
                    }
                });
            }
            else if(i==2 && !hasCopied){
                acItems[2].setEnabled(false);
            }
            else if(i==3){
                acItems[3].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                    if(TextHistory.canUndo()){
                        TextHistory.undo();
                    }
                    }
                });

            }
            else if(i==4){
                acItems[4].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                    if(TextHistory.canRedo()){
                        TextHistory.redo();
                    }
                }
                });

            }
            
        }
        
        for(int i=0;i<edItems.length;i++){
            editMn.add(edItems[ i]);
            if(i==0){
                // line-wrapped option
                edItems[i].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        linewrapped = !linewrapped;
                        textpad.setLineWrap(linewrapped);
                        textpad.setWrapStyleWord(linewrapped);
                        edItems[0].setText("Line Wrap: " + Boolean.toString(linewrapped));

                    }
                });
                editMn.addSeparator();
            }else if(i==1){
                // FONT buttom
                edItems[1].addActionListener(new ActionListener() {
                    @Override 
                    public void actionPerformed(ActionEvent e){
                        JDialog fontDlg = new JDialog(fr, "Edit Font", true);

                        fontDlg.setSize(400, 250);
                        fontDlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        fontDlg.setLayout(new GridLayout(4, 2, 10, 10));
                        fontDlg.setLocationRelativeTo(fr);

                        // Fonts installation
                        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
                        // Fonts dropdown
                        JComboBox<String> fontBox = new JComboBox<>(fonts); 
                        fontBox.setSelectedItem(textpad.getFont().getFamily());

                        // Sizes
                        String[] sizes = {"8", "10", "12", "14", "16", "18", "20", "24", "28", "32", "36", "38", "42"};
                        JComboBox<String> sizeBox = new JComboBox<>(sizes);
                        sizeBox.setSelectedItem(String.valueOf(textpad.getFont().getSize()));

                        // Weight (checkboxes)
                        JCheckBox boldCB = new JCheckBox("Bold");
                        boldCB.setSelected(textpad.getFont().isBold());
                        JCheckBox italicCB = new JCheckBox("Italic");
                        italicCB.setSelected(textpad.getFont().isItalic());

                        // Some buttons
                        JButton applyBtn = new JButton("Apply");
                        JButton cancelBtn = new JButton("Cancel");

                        applyBtn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e){
                                String family = (String) fontBox.getSelectedItem();
                                int size = Integer.parseInt((String) sizeBox.getSelectedItem());
                                int weight = Font.PLAIN;
                                if(boldCB.isSelected()){
                                    weight += Font.BOLD;
                                }
                                if(italicCB.isSelected()){
                                    weight += Font.ITALIC;
                                }

                                // Finally sets everything
                                textpad.setFont(new Font(family, weight, size));
                                fontDlg.dispose();
                            }
                        });

                        cancelBtn.addActionListener(new ActionListener() {
                            @Override 
                            public void actionPerformed(ActionEvent e){
                                fontDlg.dispose();
                            }
                        });
                        fontDlg.add(new JLabel("Font: "));
                        fontDlg.add(fontBox);
                        fontDlg.add(new JLabel("Size: "));
                        fontDlg.add(sizeBox);
                        fontDlg.add(boldCB);
                        fontDlg.add(italicCB);
                        fontDlg.add(applyBtn);
                        fontDlg.add(cancelBtn);

                        fontDlg.setVisible(true);
                    }
                });
            }
            
        }
        JMenuItem abt = new JMenuItem("About");
        abt.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e){
                JDialog abtPage = new JDialog(fr, "About Text Ground");
                abtPage.setSize(700, 450);
                abtPage.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                JPanel content = new JPanel();
                content.setLayout(new BorderLayout(10, 10));
                JLabel title = new JLabel("Text Ground", JLabel.LEFT);
                title.setVerticalAlignment(JLabel.TOP);
                title.setFont(new Font("Bookman Old Style", Font.BOLD, 35));
                JTextArea mainIdea = new JTextArea("""
Text Ground is a little project made by Kenzie (GitHub: Kenziescr) for some funsies.

Text Ground has many features including:
    • File Manipulation
    • Button-ized Copy and Pastes
    • Undo and Redo
    • Find and Replace
    • Font Manipulation
    • This About Page

I've been working on this for a little while, and I'm happy with how this look!

Version: 1.0
Available on GitHub
                """
                );
                mainIdea.setFont(new Font("Bookman Old Style", Font.PLAIN, 14));
                mainIdea.setEditable(false);
                mainIdea.setLineWrap(true);
                mainIdea.setWrapStyleWord(true);
                content.add(title, BorderLayout.NORTH);
                content.add(mainIdea, BorderLayout.CENTER);
                abtPage.add(content);
                abtPage.setVisible(true);
            }
        });
        helpMn.add(abt);
        
        menu.add(fileMn);
        menu.add(editMn);
        menu.add(helpMn);
        
        fr.setJMenuBar(menu);


        JScrollPane sp = new JScrollPane(textpad);
        
        fr.add(sp);

        fr.setLocationRelativeTo(null);
        fr.revalidate();
        fr.repaint();
        fr.setVisible(true);
    }
}


