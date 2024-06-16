package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cockyadolescents.truechessthegame.Main.waitingroom;

public class Server implements Runnable {
    private ArrayList<ClientHandler> connections;
    private ArrayList<PlayerHandler> players;
    private ServerSocket chatServerSocket, gameServerSocket;
    private ExecutorService threadPool;

    private boolean closed;
    private int port1 = 9999, port2 = 9998;

    public Server() {
        connections = new ArrayList<>();
        players = new ArrayList<>();
        closed = false;
    }

    @Override
    public void run() {
        System.out.println("Server started");
        try {
            chatServerSocket = new ServerSocket(port1);
            gameServerSocket = new ServerSocket(port2);
            threadPool = Executors.newCachedThreadPool();

            while(!closed) {
                Socket clientSocket = chatServerSocket.accept();
                Socket playerSocket = gameServerSocket.accept();

                ClientHandler ch = new ClientHandler(clientSocket);
                connections.add(ch);
                threadPool.execute(ch);

                PlayerHandler ph = new PlayerHandler(playerSocket);
                players.add(ph);
                threadPool.execute(ph);
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    class PlayerHandler implements Runnable {
        private Socket playerSocket;
        private ObjectOutputStream dataOut;
        private ObjectInputStream dataIn;
        private String color;

        public PlayerHandler(Socket socket) {
            this.playerSocket = socket;
            this.color = switch (players.size()) {
                case 0 -> "White";
                case 1 -> "Black";
                default -> "Spec"; // spectator (later feature maybe)
            };
        }

        @Override
        public void run() {
            try {
                dataOut = new ObjectOutputStream(playerSocket.getOutputStream());
                dataIn = new ObjectInputStream(playerSocket.getInputStream());

                int[] move;
                while ((move = (int[]) dataIn.readObject()) != null) {
                    for (PlayerHandler player : players) if (!player.color.equals(color)) {
                        player.dataOut.writeObject(move);
                        player.dataOut.flush();
                    }
                    System.out.print(color + " "); // debug
                }
            } catch (IOException | ClassNotFoundException e) {
                shutdown();
            }
        }

        public void shutdown() {
            try {
                dataIn.close(); dataOut.close();
                if (!playerSocket.isClosed()) {
                    playerSocket.close();
                }
            } catch (IOException _) {}
        }
    }

    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true); // send to client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // recieve from client

                username = in.readLine();
                serverLog(username + " connected");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/username")) { // change username
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            serverLog(username + " set username to " + messageSplit[1]);
                            username = messageSplit[1];
                        } else {
                            out.println("Invalid username");
                        }
                    } else if (message.startsWith("/quit")) { // disconnect
                        serverLog(username + " disconnected");
                    } else {
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        // sends data to all users
        public void broadcast(String message) {
            for (ClientHandler client : connections) if (!client.username.equals(this.username)) { // to avoid repeat send
                client.out.println(message);
            }
        }

        public void serverLog(String message) {
            System.out.println(message);
            for (ClientHandler client : connections) {
                client.out.println("SERVER: " + message);
            }
        }

        public void shutdown() {
            try {
                in.close(); out.close();
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException _) {}
        }
    }

    public void shutdown() {
        System.out.println("Server shutting down");
        try {
            closed = true;
            if (!threadPool.isShutdown())
                threadPool.shutdown();
            if (!chatServerSocket.isClosed())
                chatServerSocket.close();
            if (!gameServerSocket.isClosed())
                gameServerSocket.close();
            for (ClientHandler ch : connections)
                ch.shutdown();
            for (PlayerHandler ph : players)
                ph.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}