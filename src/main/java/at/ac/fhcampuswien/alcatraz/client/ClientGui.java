package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.client.service.ClientController;
import at.ac.fhcampuswien.alcatraz.client.util.AlcatrazClientLogger;
import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;
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
    public JTextField usernameField;
    public JTextArea outputArea;
    public JButton registerButton, logOffButton, startGameButton;
    public JCheckBox readyCheckBox;
    public JLabel sessionSizeLabel;

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

        this.clientController.setGui(this);
        setVisible(true);
    }

    private void startGame() {
        if (this.username == null) {
            AlcatrazClientLogger.logInfo(outputArea, Messages.REGISTER_FIRST);
            return;
        }
        checkPrimaryIsAvailable();
        try {
            clientController.startGame();
        } catch (NotEnoughPlayersException | GameAlreadyRunningException e) {
            AlcatrazClientLogger.logError(this, e.getMessage());
        }
    }

    public void register() {
        checkPrimaryIsAvailable();

        try {
            String username = usernameField.getText();
            if (StringUtil.isNullOrEmpty(username)) {
                AlcatrazClientLogger.logError(this, Messages.NO_USERNAME_PROVIDED);
                return;
            }
            clientController.register(username);
            this.username = username;
            this.readyCheckBox.setEnabled(true);
            this.logOffButton.setEnabled(true);
            this.registerButton.setEnabled(false);
            AlcatrazClientLogger.logInfo(outputArea, "registered user: " + this.username);
        } catch (AlreadyRegisteredException | FullSessionException | GameAlreadyRunningException e) {
            AlcatrazClientLogger.logError(this, e.getMessage());
        } catch (NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }

    }


    private void logOff() {
        checkPrimaryIsAvailable();

        try {
            clientController.logOff(this.username);
            this.username = null;
            AlcatrazClientLogger.logInfo(outputArea, Messages.LOGOFF_SUCCESS);
            this.readyCheckBox.setEnabled(false);
            this.readyCheckBox.setSelected(false);
            this.logOffButton.setEnabled(false);
            this.registerButton.setEnabled(true);
            this.sessionSizeLabel.setText("");
        } catch (RemoteException | UserNotFoundException | GameAlreadyRunningException e) {
            AlcatrazClientLogger.logError(this, e.getMessage());
        }

    }

    public void readyToPlay() {
        checkPrimaryIsAvailable();

        try {
            clientController.readyToPlay(this.username);
            AlcatrazClientLogger.logInfo(outputArea, Messages.READY_TO_PLAY_SUCCESS);
        } catch (RemoteException | UserNotFoundException | GameAlreadyRunningException e) {
            AlcatrazClientLogger.logError(this, e.getMessage());
        }

    }

    private void notReadyToPlay() {
        checkPrimaryIsAvailable();
        try {
            clientController.notReadyToPlay(this.username);
            AlcatrazClientLogger.logInfo(outputArea, Messages.NOT_READY_TO_PLAY_SUCCES);
        } catch (RemoteException | UserNotFoundException | GameAlreadyRunningException e) {
            AlcatrazClientLogger.logError(this, e.getMessage());
        }

    }

    private void checkPrimaryIsAvailable() {
        try {
            this.clientController.findCurrentPrimary();
        } catch (RemoteException ex) {
            //cached primary in clientController
            AlcatrazClientLogger.logError(this, ex.getMessage());
            System.exit(0);
        }
    }

}
