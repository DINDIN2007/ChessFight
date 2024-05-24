package org.cockyadolescents.truechessthegame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MainServer {

    public static Vector<ServerClient> clients = new Vector<ServerClient>();
    public static ServerClient mainServer;
    public static int port = 56969;

    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static void startServer() throws IOException {
        ServerSocket mainServer = new ServerSocket(port);
        while (true) {
            Socket clientSocket = mainServer.accept();
            clients.add(new ServerClient(clientSocket));
        }
    }
}
