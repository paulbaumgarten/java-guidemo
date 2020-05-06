package com.pbaumgarten.guidemo;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class View extends JFrame {
    private JPanel panelLeft;
    private JPanel panelRight;
    private JPanel panelMain;
    private JList listPeople;
    private JTextField textName;
    private JTextField textEmail;
    private JTextField textPhoneNumber;
    private JTextField textDateOfBirth;
    private JButton buttonSaveExisting;
    private JLabel labelAge;
    private JButton buttonSaveNew;
    private JLabel labelPhoto;
    private JPanel panelPhoto;
    private ArrayList<Person> people;
    private DefaultListModel listPeopleModel;

    View(String title) {
        super(title);
        people = new ArrayList<Person>();
        this.setContentPane(this.panelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setSize(600,400);
        listPeopleModel = new DefaultListModel();
        listPeople.setModel(listPeopleModel);
        buttonSaveExisting.setEnabled(false);
        loadPeopleFile();

        buttonSaveExisting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSaveExistingClicked(e);
            }
        });

        buttonSaveNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSaveNewClicked(e);
            }
        });

        listPeople.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                listPeopleSelection(e);
            }
        });
    }

    private void buttonSaveExistingClicked(ActionEvent e) {
        int personNumber = listPeople.getSelectedIndex();
        if (personNumber >= 0) {
            Person p = people.get(personNumber);
            p.setName( textName.getText() );
            p.setEmail( textEmail.getText() );
            p.setPhoneNumber( textPhoneNumber.getText() );
            p.setDateOfBirth( textDateOfBirth.getText() );
            refreshPeopleList();
            savePeopleFile();
            System.out.println(p.toString());
        } else {
            System.out.println("Can only save existing if an item is selected in the list");
        }
    }

    private void buttonSaveNewClicked(ActionEvent e) {
        Person p = new Person( textName.getText(), textEmail.getText(), textPhoneNumber.getText(), textDateOfBirth.getText() );
        people.add(p);
        savePeopleFile();
        refreshPeopleList();
    }

    private void listPeopleSelection(ListSelectionEvent e) {
        int personNumber = listPeople.getSelectedIndex();
        if (personNumber >= 0) {
            Person p = people.get(personNumber);
            textName.setText(p.getName());
            textEmail.setText(p.getEmail());
            textPhoneNumber.setText(p.getPhoneNumber());
            textDateOfBirth.setText(p.getDateOfBirthString());
            labelAge.setText(Integer.toString(p.getAge())+" years");
            buttonSaveExisting.setEnabled(true);
            displayIcon(p.getName()+".jpg", labelPhoto);
        }
    }

    public void refreshPeopleList() {
        System.out.println("[refreshPeopleList] With "+Integer.toString(people.size())+" entries");
        listPeopleModel.removeAllElements();
        for (Person p : people) {
            listPeopleModel.addElement(p.getName());
        }
    }

    public void addPerson(Person p) {
        System.out.println("[addPerson] Adding "+p.getName());
        people.add(p);
        refreshPeopleList();
    }

    private boolean saveFileFromString(String filename, String data) {
        try {
            FileWriter fw = new FileWriter(filename);
            PrintWriter output = new PrintWriter(fw);
            output.println( data );
            output.close();
            fw.close();
            return true;
        } catch(Exception e) {
            System.out.println("[saveFileFromString] error saving "+filename);
            return false;
        }
    }

    private boolean displayIcon(String filename, JLabel target) {
        try {
//            ImageIcon icon = new ImageIcon(filename);
            // Resize the icon to fit the target JLabel
            ImageIcon icon = new ImageIcon(new ImageIcon(filename).getImage().getScaledInstance(target.getWidth(), target.getHeight(), Image.SCALE_DEFAULT));
            target.setIcon(icon);
            return true;
        } catch (Exception e) {
            target.setIcon(null);
            return false;
        }
    }

    private void savePeopleFile() {
        // Convert the ArrayList of Person objects into a JSONArray of JSONObjects
        JSONArray ja = new JSONArray();
        for (Person p : people) {
            JSONObject jo = new JSONObject(p);
            ja.put(jo);
        }
        // Save the JSONArray to a text file
        saveFileFromString("contacts.json", ja.toString());
    }

    private String loadFileToString(String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            // Load the JSON file as String data
            File f = new File(filename);
            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()) {
                sb.append(reader.nextLine());
            }
            reader.close();
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private void loadPeopleFile() {
        String filename = "contacts.json";
        String fileData = loadFileToString(filename);
        if (fileData != null) {
            // Create a JSONArray from the String
            JSONArray ja = new JSONArray(fileData);
            // Iterate over the JSONObjects within the JSONArray
            for (int i=0; i< ja.length(); i++) {
                JSONObject o = ja.getJSONObject(i);
                // Use each JSONObject to create a Person object
                Person p = new Person(o.getString("name"),
                        o.getString("email"),
                        o.getString("phoneNumber"),
                        o.getString("dateOfBirthString"));
                // Add the Person object to the ArrayList of people
                people.add(p);
            }
        }
        // Refresh the list
        refreshPeopleList();
    }

    public static void printLookAndFeels() {
        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo look : looks) {
            System.out.println(look.getClassName());
        }
    }

    public static void main(String[] args) {
        printLookAndFeels();
        View view = new View("Contacts app");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (Exception e) {

        }
        view.setVisible(true);
        /*
        Person sheldon = new Person("Sheldon Lee Cooper", "sheldon@gmail.com", "555 0001", "26/02/1980");
        Person howard = new Person("Howard Joel Wolowitz", "howard@gmail.com", "555 0002", "01/03/1981");
        Person raj = new Person("Rajesh Ramayan Koothrappali", "raj@gmail.com", "555 0003", "06/10/1981");
        Person penny = new Person("Penny Hofstadter", "penny@gmail.com", "555 0004", "02/12/1985");
        Person bernadette = new Person("Bernadette Rostenkowski-Wolowitz", "bernadette@gmail.com", "555 0002", "01/01/1984");
        Person amy = new Person("Amy Farrah Fowler", "amy@gmail.com", "555 0005", "17/12/1979");
        Person leonard = new Person("Leonard Hofstadter", "leonard@gmail.com", "555 0006", "17/05/1980");
        view.addPerson(sheldon);
        view.addPerson(howard);
        view.addPerson(raj);
        view.addPerson(penny);
        view.addPerson(amy);
        view.addPerson(bernadette);
        view.addPerson(leonard);

javax.swing.plaf.metal.MetalLookAndFeel
javax.swing.plaf.nimbus.NimbusLookAndFeel
com.sun.java.swing.plaf.motif.MotifLookAndFeel
com.sun.java.swing.plaf.windows.WindowsLookAndFeel
com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel

        */
    }

}
