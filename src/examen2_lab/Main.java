package examen2_lab;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.*;

public class Main {
    private static PSNUsers psn;
    private static JTextArea salidaArea;

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
        
        JPanel panelUsuario = new JPanel(new BorderLayout());
        JPanel users = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField userField = new JTextField();
        JButton addUserBtn = new JButton("Agregar Usuario");
        JButton deactivateBtn = new JButton("Desactivar Usuario");
        
        users.add(new JLabel("Nombre de usuario:"));
        users.add(userField);
        users.add(addUserBtn);
        users.add(deactivateBtn);
        
        JPanel trophyPanel = new JPanel(new BorderLayout());
        JPanel trophyForm = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField trophyUserField = new JTextField();
        JTextField gameField = new JTextField();
        JTextField trophyNameField = new JTextField();
        JComboBox<Trophy> trophyComboBox = new JComboBox<>(Trophy.values());
        JButton addTrophyBtn = new JButton("Agregar Trofeo");
        
        trophyForm.add(new JLabel("Usuario:"));
        trophyForm.add(trophyUserField);
        trophyForm.add(new JLabel("Juego:"));
        trophyForm.add(gameField);
        trophyForm.add(new JLabel("Nombre del trofeo:"));
        trophyForm.add(trophyNameField);
        trophyForm.add(new JLabel("Tipo de trofeo:"));
        trophyForm.add(trophyComboBox);
        trophyForm.add(new JLabel(""));
        trophyForm.add(addTrophyBtn);
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        JPanel infoForm = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField infoUserField = new JTextField();
        JButton showInfoBtn = new JButton("Mostrar Información");
        
        infoForm.add(new JLabel("Usuario a consultar:"));
        infoForm.add(infoUserField);
        infoForm.add(new JLabel(""));
        infoForm.add(showInfoBtn);
        
        salidaArea = new JTextArea();
        salidaArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(salidaArea);
        
        addUserBtn.addActionListener(e -> {
            try {
                if (psn.addUser(userField.getText())) {
                    salidaArea.setText("Usuario agregado: " + userField.getText());
                } else {
                    salidaArea.setText("Error: El usuario ya existe");
                }
            } catch (IOException ex) {
                salidaArea.setText("Error: " + ex.getMessage());
            }
        });

        deactivateBtn.addActionListener(e -> {
            try {
                psn.deactivateUser(userField.getText());
                salidaArea.setText("Usuario desactivado: " + userField.getText());
            } catch (IOException ex) {
                salidaArea.setText("Error: " + ex.getMessage());
            }
        });

        addTrophyBtn.addActionListener(e -> {
            salidaArea.setText("");
            if(!trophyUserField.getText().isEmpty() && !gameField.getText().isEmpty() && !trophyNameField.getText().isEmpty()) {
                try {
                    psn.addTrophieTo(
                        trophyUserField.getText(),
                        gameField.getText(),
                        trophyNameField.getText(),
                        (Trophy)trophyComboBox.getSelectedItem()
                    );
                    salidaArea.setText("Trofeo agregado a " + trophyUserField.getText());
                    trophyUserField.setText("");
                    gameField.setText("");
                    trophyNameField.setText("");
                } catch (IOException ex) {
                    salidaArea.setText("Error: " + ex.getMessage());
                }
            } else {
                StringBuilder error = new StringBuilder("Error:");
                if(trophyUserField.getText().isEmpty()) error.append("\n- Nombre de usuario");
                if(gameField.getText().isEmpty()) error.append("\n- Nombre del juego");
                if(trophyNameField.getText().isEmpty()) error.append("\n- Nombre del trofeo");
                salidaArea.setText(error.toString());
            }
        });

        showInfoBtn.addActionListener(e -> {
            try {
                String info = psn.playerInfo(infoUserField.getText());
                salidaArea.setText(info);
            } catch (IOException ex) {
                salidaArea.setText("Error: " + ex.getMessage());
            }
        });

        panelUsuario.add(users, BorderLayout.NORTH);
        trophyPanel.add(trophyForm, BorderLayout.NORTH);
        infoPanel.add(infoForm, BorderLayout.NORTH);

        tabbedPane.addChangeListener(e -> {
            userField.setText("");
            trophyUserField.setText("");
            gameField.setText("");
            trophyNameField.setText("");
            infoUserField.setText("");
            trophyComboBox.setSelectedIndex(0);
        });
        
        tabbedPane.addTab("Usuarios", panelUsuario);
        tabbedPane.addTab("Trofeos", trophyPanel);
        tabbedPane.addTab("Información", infoPanel);
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(tabbedPane, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}