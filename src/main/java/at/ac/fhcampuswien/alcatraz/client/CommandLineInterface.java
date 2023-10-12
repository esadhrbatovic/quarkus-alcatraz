package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.client.service.ClientController;
import at.ac.fhcampuswien.alcatraz.shared.cli.Menu;
import at.ac.fhcampuswien.alcatraz.shared.exception.PlayerAlreadyExistsException;
import at.ac.fhcampuswien.alcatraz.shared.exception.FullSessionException;
import at.ac.fhcampuswien.alcatraz.shared.exception.GameRunningException;
import at.ac.fhcampuswien.alcatraz.shared.exception.PlayerNotFoundException;
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
        menu.insert("a", "Register", this::register);
        menu.insert("b", "Ready to play", this::ready);
        menu.insert("c", "Unregister", this::unregister);
        menu.insert("d", "Not ready to play", this::undoReady);
        menu.insert("q", "Quit", null);
        Runnable choice;

        while ((choice = menu.exec()) != null) {
            choice.run();
        }
        System.out.println("Program finished");
    }


    public void register() {
        ensureServerIsAvailableAndPrimary();
        if (this.username != null) {
            System.out.println("You are already registered, please unregister before proceeding.");
        } else {
            try {
                System.out.print("Enter your name:");
                Scanner scn = new Scanner(System.in);
                String input = scn.nextLine();

                //String input = RandomStringGenerator.generateRandomString(8);

                clientController.register(input);
                this.username = input;
                System.out.println("Your player has been registered.");
            } catch (PlayerAlreadyExistsException | GameRunningException | FullSessionException e) {
                System.out.println(e.getMessage());
            } catch (NotBoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void unregister() {
        ensureServerIsAvailableAndPrimary();
        if (this.username == null) {
            System.out.println("There is no player registered");
        } else {
            try {
                clientController.unregister(this.username);
                this.username = null;
                System.out.println("Your player has been unregistered.");
            } catch (RemoteException | PlayerNotFoundException | GameRunningException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void ready() {
        ensureServerIsAvailableAndPrimary();
        if (this.username == null) {
            System.out.println("There is no player registered");
        } else {
            try {
                clientController.joinSession(this.username);
                System.out.println("Your player now has the status 'ready'.");
            } catch (RemoteException | PlayerNotFoundException | GameRunningException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void undoReady() {
        ensureServerIsAvailableAndPrimary();
        if (this.username == null) {
            System.out.println("There is no player registered");
        } else {
            try {
                clientController.renameSession(this.username);
                System.out.println("Your player now has the status 'not ready'.");
            } catch (RemoteException | PlayerNotFoundException | GameRunningException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    private void findNewPrimary() throws RemoteException {
        this.clientController.findNewPrimary();
    }

    // todo: wonky exception handling here
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
