package at.ac.fhcampuswien.alcatraz.server;

import at.ac.fhcampuswien.alcatraz.server.rmi.RmiServer;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

@QuarkusMain(name = "server")
public class ServerApplication implements QuarkusApplication {
    @Inject
    CommandLineInterface cli;

    @Inject
    RegistrationService registrationService;

    @Inject
    RmiServer rmiServer;

    @Override
    public int run(String... args) {
        rmiServer.setRegistrationService(registrationService);
        cli.start();
        return 0;
    }
}