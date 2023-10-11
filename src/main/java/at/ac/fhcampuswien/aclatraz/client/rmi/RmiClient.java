package at.ac.fhcampuswien.aclatraz.client.rmi;

import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;

@ApplicationScoped
public class RmiClient {
    private static final Logger log = Logger.getLogger(RmiClient.class);

    @ConfigProperty(name = "serverIps")
    List<String> serverIps;

    public RegistrationService getRegistrationService() throws RemoteException {

        for (String serverIp : serverIps) {
            RegistrationService registrationService;
            try {
                log.info("Trying to connect to " + serverIp);
                registrationService = (RegistrationService) Naming.lookup("rmi://" + serverIp + ":1099/" + "RegistrationService");
                log.info("conntected  " + registrationService);
            } catch (Exception e) {
                log.error("error connecting " + e);
                continue;
            }
            if (registrationService != null && registrationService.isPrimary()) {
                return registrationService;
            }
        }

        log.error("No available servers could be found. Please start a server and then restart the game client.");

        System.exit(0);
        return null;
    }


}
