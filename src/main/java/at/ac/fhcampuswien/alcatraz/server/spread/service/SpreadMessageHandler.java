package at.ac.fhcampuswien.alcatraz.server.spread.service;

import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import spread.SpreadConnection;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public interface SpreadMessageHandler {

    void handleMembershipMessage(SpreadConnection connection, SpreadGroup group, SpreadMessage spreadMessage) throws RemoteException, AlreadyBoundException;

    void syncGameSessionWithGroup(SpreadConnection connection, SpreadGroup group, GameSession<NetPlayer> gameSession);

    void handleSyncSession(GameSession<NetPlayer> gameSession);
}
