/*
 * Copyright (c) 2000 - 2023 by Raiffeisen Software GmbH.
 * All rights reserved.
 *
 */
package at.ac.fhcampuswien.alcatraz.shared.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryProvider {

    public static Registry getOrCreateRegistry(int port) {
        Registry registry;
        //try {
        //    registry = LocateRegistry.getRegistry(port);
        //} catch (RemoteException e) {
            try {
                registry = LocateRegistry.createRegistry(port);
            } catch (RemoteException ex) {
                try {
                    registry = LocateRegistry.getRegistry(port);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        //}
        return registry;
    }
}
