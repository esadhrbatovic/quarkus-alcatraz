/*
 * Copyright (c) 2000 - 2023 by Raiffeisen Software GmbH.
 * All rights reserved.
 *
 */
package at.ac.fhcampuswien.alcatraz.server.spread;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import jakarta.inject.Singleton;

import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistrationService;
import at.ac.fhcampuswien.alcatraz.shared.rmi.RegistryProvider;

/**
 * @author LRCUHRE
 * @since 1.0
 */
@Singleton
public class RmiServer {
    RegistrationService registrationService;

    public void registerRMIEndpoint(){
        try {
            Registry registry = RegistryProvider.getOrCreateRegistry(1099);
            registry.rebind("RegistrationService", registrationService);
            System.out.println("RMI Service bound");

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public RegistrationService getRegistrationService() {
        return registrationService;
    }

    public void setRegistrationService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

}
