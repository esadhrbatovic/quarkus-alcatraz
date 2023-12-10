package at.ac.fhcampuswien.alcatraz.client.rmi;

import at.ac.fhcampuswien.alcatraz.shared.exception.messages.Messages;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@ApplicationScoped
public class RmiClient {
    private static final Logger log = Logger.getLogger(RmiClient.class);

    @ConfigProperty(name = "serverIps")
    List<String> serverIps;

    public RegistrationService getRegistrationService() throws RemoteException {
        ExecutorService executor = Executors.newFixedThreadPool(serverIps.size());
        List<Future<RegistrationService>> futures = new ArrayList<>();

        for (String serverIp : serverIps) {
            Callable<RegistrationService> task = () -> {
                try {
                    log.info("Attempt to connect with " + serverIp);
                    RegistrationService registrationService = (RegistrationService) Naming.lookup("rmi://" + serverIp + ":1099/RegistrationService");
                    if (registrationService != null && registrationService.isPrimary()) {
                        log.info("Connected to primary " + registrationService);
                        return registrationService;
                    }else{
                        log.info(serverIp + " is a backup server");
                    }
                } catch (Exception e) {
                    log.error("Error connecting to " + serverIp + ": " + e);
                }
                return null;
            };
            futures.add(executor.submit(task));
        }

        RegistrationService primaryService = null;
        for (Future<RegistrationService> future : futures) {
            RegistrationService service = null; // this blocks until the task completes
            try {
                service = future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(Messages.NO_SERVER_AVAILABLE, e);
            }

            if (service != null) {
                primaryService = service;
                break; // Found the primary service, no need to wait for other tasks
            }
        }

        executor.shutdown(); // Shut down the executor service

        if (primaryService == null) {
            log.error(Messages.NO_SERVER_AVAILABLE);
            System.exit(0);
        }

        return primaryService;
    }


}
