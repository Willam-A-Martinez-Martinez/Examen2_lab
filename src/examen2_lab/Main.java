package examen2_lab;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main {
    private static PSNUsers psn;
    private static JTextArea outputArea;

    public static void main(String[] args) {
        try {
            psn = new PSNUsers();
            createAndShowGUI();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al inicializar PSN: " + e.getMessage(), 
                                       "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Administrador PSN");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel userForm = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField userField = new JTextField();
        JButton addUserBtn = new JButton("Agregar Usuario");
        JButton deactivateBtn = new JButton("Desactivar Usuario");
        JPanel trophyPanel = new JPanel(new BorderLayout());
        JPanel trophyForm = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField trophyUserField = new JTextField();
        JTextField gameField = new JTextField();
        JTextField trophyNameField = new JTextField();
        JComboBox<Trophy> trophyTypeCombo = new JComboBox<>(Trophy.values());
        JButton addTrophyBtn = new JButton("Agregar Trofeo");
        JPanel infoPanel = new JPanel(new BorderLayout());
        JPanel infoForm = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField infoUserField = new JTextField();
        JButton showInfoBtn = new JButton("Mostrar Información");

        userForm.add(new JLabel("Nombre de usuario:"));
        userForm.add(userField);
        userForm.add(addUserBtn);
        userForm.add(deactivateBtn);

        trophyForm.add(new JLabel("Usuario:"));
        trophyForm.add(trophyUserField);
        trophyForm.add(new JLabel("Juego:"));
        trophyForm.add(gameField);
        trophyForm.add(new JLabel("Nombre del trofeo:"));
        trophyForm.add(trophyNameField);
        trophyForm.add(new JLabel("Tipo de trofeo:"));
        trophyForm.add(trophyTypeCombo);
        trophyForm.add(addTrophyBtn);

        infoForm.add(new JLabel("Usuario a consultar:"));
        infoForm.add(infoUserField);
        infoForm.add(showInfoBtn);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        addUserBtn.addActionListener(e -> {
            try {
                if (psn.addUser(userField.getText())) {
                    outputArea.setText("Usuario agregado: " + userField.getText());
                } else {
                    outputArea.setText("Error: El usuario ya existe");
                }
            } catch (IOException ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });

        deactivateBtn.addActionListener(e -> {
            try {
                if(psn.users.search(userField.getText())!=-1){
                    psn.deactivateUser(userField.getText());
                    outputArea.setText("Usuario desactivado: " + userField.getText());
                }else{
                    outputArea.setText("Error: usuario no existe!");
                }
            } catch (IOException ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
            
        });

        addTrophyBtn.addActionListener(e -> {
            outputArea.setText("");
            if(!trophyUserField.getText().equals("") && !gameField.getText().equals("") && !trophyNameField.getText().equals("")) {
                try {
                    psn.addTrophieTo(
                        trophyUserField.getText(),
                        gameField      .getText(),
                        trophyNameField.getText(),
                        (Trophy)trophyTypeCombo.getSelectedItem()
                    );
                    outputArea.setText("Trofeo agregado a " + trophyUserField.getText());
                } catch (IOException ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
            if(trophyUserField.getText().equals("")) {
                outputArea.append("Error: Debe ingresar un nombre de usuario\n");
                trophyUserField.setText("");
            }
            if(gameField.getText().equals("")) {
                outputArea.append("Error: Debe ingresar un nombre de juego\n");
                gameField.setText("");
            }
            if(trophyNameField.getText().equals("")) {
                outputArea.append("Error: Debe ingresar un nombre para el trofeo\n");
                trophyNameField.setText("");
            }
        });

        showInfoBtn.addActionListener(e -> {
            try {
                String info = psn.playerInfo(infoUserField.getText());
                outputArea.setText(info);
            } catch (IOException ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });

        userPanel.add(userForm, BorderLayout.NORTH);
        trophyPanel.add(trophyForm, BorderLayout.NORTH);
        infoPanel.add(infoForm, BorderLayout.NORTH);

        tabbedPane.addChangeListener(e -> {
            userField.setText("");
            trophyUserField.setText("");
            gameField.setText("");
            trophyNameField.setText("");
            infoUserField.setText("");
            
            trophyTypeCombo.setSelectedIndex(0);
        });
        
        tabbedPane.addTab("Usuarios", userPanel);
        tabbedPane.addTab("Trofeos", trophyPanel);
        tabbedPane.addTab("Información", infoPanel);
        
        

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(tabbedPane, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}