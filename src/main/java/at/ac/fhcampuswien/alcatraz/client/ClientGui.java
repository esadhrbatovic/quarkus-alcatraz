package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.client.service.ClientController;
import at.ac.fhcampuswien.alcatraz.shared.exception.AlcatrazException;
import at.ac.fhcampuswien.alcatraz.shared.util.AlcatrazClientLogger;
import io.quarkus.runtime.util.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@ApplicationScoped
public class ClientGui extends JFrame {
    ClientController clientController;
    private String username;

    // Components
    private JTextField usernameField;
    private JTextArea outputArea;
    private JButton registerButton, logOffButton, startGameButton;
    private JCheckBox readyCheckBox;
    private JLabel sessionSizeLabel;

    public void initializeUI(ClientController clientController) {
        this.clientController = clientController;
        setTitle("Alcatraz Game Client");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        usernameField = new JTextField();
        readyCheckBox = new JCheckBox("Ready to Play");
        readyCheckBox.setEnabled(false);
        sessionSizeLabel = new JLabel("");
        outputArea = new JTextArea();
        registerButton = new JButton("Register");
        logOffButton = new JButton("Log Off");
        startGameButton = new JButton("Start Game");

        // Layout
        JPanel northPanel = new JPanel(new GridLayout(1, 2));
        northPanel.add(new JLabel("Username: "));
        northPanel.add(usernameField);

        JPanel centerPanel = new JPanel(new GridLayout(5, 1));
        centerPanel.add(readyCheckBox);
        centerPanel.add(sessionSizeLabel);
        centerPanel.add(registerButton);
        centerPanel.add(logOffButton);
        logOffButton.setEnabled(false);
        centerPanel.add(startGameButton);
        startGameButton.setEnabled(false);

        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.SOUTH);

        // Action Listeners
        registerButton.addActionListener(e -> register());
        logOffButton.addActionListener(e -> logOff());
        startGameButton.addActionListener(e -> startGame());

        readyCheckBox.addActionListener(e -> {
            if (readyCheckBox.isSelected()) {
                readyToPlay();
            } else {
                notReadyToPlay();
            }
        });

        this.clientController.setSessionSizeLabel(this.sessionSizeLabel);
        this.clientController.setStartGameButton(this.startGameButton);
        setVisible(true);
    }

    private void startGame() {
        if (this.username == null) {
            AlcatrazClientLogger.logInfo(outputArea,"Please register first");
            return;
        }
        checkPrimaryIsAvailable();
        try{
            clientController.startGame();
        }catch(AlcatrazException e){
            AlcatrazClientLogger.logError(this, e.getMessage());
        }
    }

    public void register() {
        checkPrimaryIsAvailable();
        if (this.username != null) {
            AlcatrazClientLogger.logError(this, "This user is already registered");
        } else {
            try {
                String username = usernameField.getText();
                if(StringUtil.isNullOrEmpty(username)){
                    AlcatrazClientLogger.logInfo(outputArea,"please provide a username");
                    return;
                }
                clientController.register(username);
                this.username = username;
                this.readyCheckBox.setEnabled(true);
                this.logOffButton.setEnabled(true);
                this.registerButton.setEnabled(false);
                AlcatrazClientLogger.logInfo(outputArea,"registered user: " + this.username);
            } catch (AlcatrazException e) {
                AlcatrazClientLogger.logError(this, e.getMessage());
            } catch (NotBoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void logOff() {
        checkPrimaryIsAvailable();
        if (this.username == null) {
            AlcatrazClientLogger.logInfo(outputArea,"No user registered");
        } else {
            try {
                clientController.logOff(this.username);
                this.username = null;
                AlcatrazClientLogger.logInfo(outputArea,"You have successfully logged off.");
                this.readyCheckBox.setEnabled(false);
                this.readyCheckBox.setSelected(false);
                this.logOffButton.setEnabled(false);
                this.registerButton.setEnabled(true);
                this.sessionSizeLabel.setText("");
            } catch (RemoteException | AlcatrazException e) {
                AlcatrazClientLogger.logError(this, e.getMessage());
            }
        }
    }

    public void readyToPlay() {
        checkPrimaryIsAvailable();
        if (this.username == null) {
            AlcatrazClientLogger.logInfo(outputArea,"No user registered");
        } else {
            try {
                clientController.readyToPlay(this.username);
                AlcatrazClientLogger.logInfo(outputArea,"Your player now has the status 'ready'.");
            } catch (RemoteException | AlcatrazException e) {
                AlcatrazClientLogger.logError(this, e.getMessage());
            }
        }
    }

    private void notReadyToPlay() {
        checkPrimaryIsAvailable();
        if (this.username == null) {
            System.out.println("No user registered");
        } else {
            try {
                clientController.notReadyToPlay(this.username);
                AlcatrazClientLogger.logInfo(outputArea,"Your player now has the status 'not ready'.");
            } catch (RemoteException | AlcatrazException e) {
                AlcatrazClientLogger.logError(this, e.getMessage());
            }
        }
    }


    private void findPrimary() throws RemoteException {
        this.clientController.findCurrentPrimary();
    }

    private void checkPrimaryIsAvailable() {
        try {
            if (this.clientController.getRegistrationService() == null || !this.clientController.getRegistrationService()
                    .isPrimary()) {
                findPrimary();
            }
        } catch (RemoteException e) {
            try {
                findPrimary();
            } catch (RemoteException ex) {
                AlcatrazClientLogger.logError(this, ex.getMessage());
                System.exit(0);
            }
        }
    }

}
