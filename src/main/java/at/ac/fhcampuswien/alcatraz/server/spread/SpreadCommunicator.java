package at.ac.fhcampuswien.alcatraz.server.spread;

import at.ac.fhcampuswien.alcatraz.server.spread.service.SpreadMessageHandler;
import at.ac.fhcampuswien.alcatraz.shared.model.NetPlayer;
import at.ac.fhcampuswien.alcatraz.shared.model.GameSession;
import at.ac.fhcampuswien.alcatraz.server.spread.enums.SpreadMessageType;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.jboss.logging.Logger;
import spread.*;

@Singleton
public class SpreadCommunicator implements AdvancedMessageListener, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Inject
    SpreadMessageHandler spreadMessageHandler;
    @Inject
    ServerState serverState;
    @ConfigProperty(name = "spread-server")
    String spreadServer;

    SpreadGroupBean spreadGroup;
    SpreadConnectionBean spreadConnection;

    private static final Logger log = Logger.getLogger(SpreadCommunicator.class);

    @PostConstruct
    public void initSpread(){
        this.spreadConnection = spreadConnection();
        this.spreadGroup = spreadGroup();
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        log.info("Received Message");
        try {
            System.out.println("Spreader's object  " + getObject(spreadMessage.getData()));
        } catch (SpreadException e) {
            e.printStackTrace();
        }
        try {
            if (Objects.equals(SpreadMessageType.valueOfLabel(spreadMessage.getType()), SpreadMessageType.SYNC)) {
                if(getObject(spreadMessage.getData()) instanceof GameSession){
                    log.info("Got Lobby Object to sync!");
                    log.info((GameSession) getObject(spreadMessage.getData()));
                    spreadMessageHandler.handleSyncSession((GameSession) getObject(spreadMessage.getData()));
                }

            }
        } catch (SpreadException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        try {
            spreadMessageHandler.handleMembershipMessage(spreadConnection, spreadGroup, spreadMessage);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToSpread(GameSession<NetPlayer> session) {
        spreadMessageHandler.syncSession(spreadConnection, spreadGroup, session);
    }

    public SpreadConnectionBean spreadConnection() {
        SpreadConnectionBean connection = new SpreadConnectionBean();

        try {
            Random random = new Random();
            int privateName = random.nextInt(99999);
            InetAddress hostName = InetAddress.getByName(spreadServer);
            int port = 0;
            // Priority Connection or not -> In our case true
            boolean priority = true;
            //  Set group_membership flag to true so membership messages can be sent after connection
            boolean groupMembership = true;

            connection.connect(hostName, port, Integer.toString(privateName), priority, groupMembership);
            log.info("creating spreadConnection " + Integer.toString(privateName) + " " + priority + " " + groupMembership);
            this.serverState.setServerId(privateName);
        } catch (UnknownHostException | SpreadException e) {
            log.error("Could not establish connection with Spread. Please start spread first and make sure it can be resolved, then restart the game server.");
            shutdownServer();
        }
        connection.add(this);
        return connection;
    }

    public void shutdownServer() {
        System.exit(0);
    }

    public SpreadGroupBean spreadGroup() {
        SpreadGroupBean spreadGroup = new SpreadGroupBean();

        try {
            spreadGroup.join(spreadConnection(), "group");
        } catch (SpreadException e) {
            log.error("Could not join the Spread Group. Please make sure the spread server is running and the group is created.");
            shutdownServer();
        }
        return spreadGroup;
    }

    public Object getObject(byte[] data) throws SpreadException {
        ByteArrayInputStream var1 = new ByteArrayInputStream(data);

        ObjectInputStream var2;
        try {
            var2 = new ObjectInputStream(var1);
        } catch (IOException var8) {
            throw new SpreadException("ObjectInputStream(): " + var8);
        }

        Object var3;
        try {
            var3 = var2.readObject();
        } catch (ClassNotFoundException var6) {
            throw new SpreadException("readObject(): " + var6);
        } catch (IOException var7) {
            throw new SpreadException("readObject(): " + var7);
        }

        try {
            var2.close();
            var1.close();
            return var3;
        } catch (IOException var5) {
            throw new SpreadException("close/close(): " + var5);
        }
    }

}