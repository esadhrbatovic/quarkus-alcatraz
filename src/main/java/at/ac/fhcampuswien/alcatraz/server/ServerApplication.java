package at.ac.fhcampuswien.alcatraz.server;

import at.ac.fhcampuswien.alcatraz.server.spread.RmiServer;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistryProvider;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import java.rmi.registry.Registry;

@QuarkusMain(name = "server")
public class ServerApplication implements  QuarkusApplication{
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