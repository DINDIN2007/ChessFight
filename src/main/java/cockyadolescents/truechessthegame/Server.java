package cockyadolescents.truechessthegame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket serversocket;
    private boolean closed;
    private ExecutorService pool;
    public static int port = 9999;

    public Server() {
        connections = new ArrayList<>();
        closed = false;
    }

    @Override
    public void run() {
        serverLog("Server started");
        try {
            serversocket = new ServerSocket(port);
            pool = Executors.newCachedThreadPool();
            while(!closed) {
                Socket client = serversocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    public void shutdown() {
        serverLog("Server shutting down");
        try {
            closed = true;
            pool.shutdown();
            if (!serversocket.isClosed()) {
                serversocket.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public void serverLog(String message) {
        System.out.println("SERVER: " + message);
    }

    class ConnectionHandler implements Runnable {

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Enter username: ");
                username = in.readLine();
                serverLog(username + " connected");
                broadcast(username + " joined the chat");
                String message;

                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/username")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(username + " renamed themselves to " + messageSplit[1]);
                            serverLog(username + " renamed themselves to " + messageSplit[1]);
                            username = messageSplit[1];
                            out.println("changed to username to " + username);
                        } else {
                            out.println("no username provided");
                        }
                    } else if (message.startsWith("/quit")) {
                        broadcast(username + " left the chat");
                        // quit
                    } else {
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void broadcast(String message) {
            for (ConnectionHandler ch : connections) {
                ch.sendMessage(message);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e){
                // ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
