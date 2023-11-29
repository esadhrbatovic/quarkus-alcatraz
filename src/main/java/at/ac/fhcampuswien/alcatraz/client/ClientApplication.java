package at.ac.fhcampuswien.alcatraz.client;

import at.ac.fhcampuswien.alcatraz.client.service.ClientController;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

import javax.swing.*;

@QuarkusMain(name = "client")
public class ClientApplication implements QuarkusApplication {

    @Inject
    ClientController clientController;

    @Override
    public int run(String... args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ClientGui gui = new ClientGui();
                gui.initializeUI(clientController);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Keep the application running. Depending on how your GUI is set up,
        // you might need a different mechanism to wait for the GUI to close.
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return 0;
    }
}
