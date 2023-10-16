package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.client.service.ClientController;
import at.ac.fhcampuswien.alcatraz.shared.cli.Menu;
import at.ac.fhcampuswien.alcatraz.shared.exception.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;


@ApplicationScoped
public class CommandLineInterface {
    private static final Logger log = Logger.getLogger(CommandLineInterface.class);

    @Inject
    ClientController clientController;

    String username;

    public void start() {

        Menu<Runnable> menu = new Menu<>("User Interface");
        menu.setTitle("Choose an option:");
        menu.insert("1", "Register on Server", this::register);
        menu.insert("2", "Join Game Session", this::joinGameSession);
        menu.insert("3", "Log Off from Server", this::logOff);
        menu.insert("4", "Leave Game Session", this::leaveGameSession);
        menu.insert("0", "Quit", null);
        Runnable choice;

        while ((choice = menu.exec()) != null) {
            choice.run();
        }
        System.out.println("Program finished");
    }


    public void register() {
        ensureServerIsAvailableAndPrimary();
        if (this.username != null) {
            System.out.println("This user is already registered");
        } else {
            try {
                System.out.print("Enter username:");
                Scanner scn = new Scanner(System.in);
                String input = scn.nextLine();
                clientController.register(input);
                this.username = input;
                System.out.println("registered user: " + this.username);
            } catch (AlcatrazException e) {
                System.out.println(e.getMessage());
            } catch (NotBoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void logOff() {
        ensureServerIsAvailableAndPrimary();
        if (this.username == null) {
            System.out.println("No user registered");
        } else {
            try {
                clientController.logOff(this.username);
                this.username = null;
                System.out.println("You have successfully logged off.");
            } catch (RemoteException | AlcatrazException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void joinGameSession() {
        ensureServerIsAvailableAndPrimary();
        if (this.username == null) {
            System.out.println("No user registered");
        } else {
            try {
                clientController.joinSession(this.username);
                System.out.println("You have joined the Game Session. Please wait until other players join");
            } catch (RemoteException | AlcatrazException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void leaveGameSession() {
        ensureServerIsAvailableAndPrimary();
        if (this.username == null) {
            System.out.println("No user registered");
        } else {
            try {
                clientController.renameSession(this.username);
                System.out.println("Your player now has the status 'not ready'.");
            } catch (RemoteException | AlcatrazException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    private void findNewPrimary() throws RemoteException {
        this.clientController.findNewPrimary();
    }

    private void ensureServerIsAvailableAndPrimary() {
        try {
            if (this.clientController.getRegistrationService() == null || !this.clientController.getRegistrationService()
                    .isPrimary()) {
                findNewPrimary();
            }
        } catch (RemoteException e) {
            try {
                findNewPrimary();
            } catch (RemoteException ex) {
                System.out.println("No registration servers available, exiting the game.");
                log.error(ex.getMessage());
                System.exit(0);
            }
        }
    }
}
