package at.ac.fhcampuswien.alcatraz.server.spread.service;

import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.Session;
import spread.SpreadConnection;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public interface SpreadMessageHandler {

    void handleMembershipMessage(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) throws RemoteException, AlreadyBoundException;
    void syncSession(SpreadConnection connection, SpreadGroup group, Session<NetPlayer> session);
    void handleSyncSession(Session<NetPlayer> session);
}
