package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<clientHandler> connections;
    private ServerSocket serversocket;
    private ExecutorService threadPool;

    private boolean closed;
    public int port = 9999;

    private clientHandler player1;
    private clientHandler player2;

    public Server() {
        connections = new ArrayList<>();
        closed = false;
    }

    @Override
    public void run() {
        System.out.println("Server started");
        try {
            serversocket = new ServerSocket(port);
            threadPool = Executors.newCachedThreadPool();
            while(!closed) {
                Socket clientSocket = serversocket.accept();
                clientHandler handler = new clientHandler(clientSocket);
                connections.add(handler);

                /*if (player1 == null) {
                    player1 = handler;
                } else if (player2 == null) {
                    player2 = handler;
                }*/

                threadPool.execute(handler);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    public void shutdown() {
        System.out.println("Server shutting down");
        try {
            closed = true;
            threadPool.shutdown();
            if (!serversocket.isClosed()) {
                serversocket.close();
            }
            for (clientHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class clientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public clientHandler(Socket client) {
            this.clientSocket = client;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true); // send to client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // recieve from client

                out.println("Enter username: ");
                username = in.readLine();
                serverLog(username + " connected");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/username")) { // change username
                        String[] messageSplit = message.split(" ", 1); // no spaces in username
                        if (messageSplit.length == 1) {
                            serverLog(username + " changed username to " + message);
                            username = message;
                        } else {
                            out.println("Invalid username");
                        }
                    }
                    else if (message.startsWith("/quit")) { // disconnect
                        serverLog(username + " disconnected");
                    }
                    else {
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        // sends data to all users
        public void broadcast(String message) {
            for (clientHandler client : connections) {
                if (!client.username.equals(this.username)) { // to avoid repeat send
                    client.out.println(message);
                }
            }
        }

        public void serverLog(String message) {
            for (clientHandler client : connections) {
                client.out.println("SERVER: " + message);
            }
        }

        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException _) {}
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
