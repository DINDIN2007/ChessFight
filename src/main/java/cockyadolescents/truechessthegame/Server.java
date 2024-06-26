package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<ClientHandler> connections;
    private ArrayList<PlayerHandler> players;
    private ServerSocket chatServerSocket, gameServerSocket;
    private ExecutorService threadPool;

    public boolean closed;
    private int chatPort = 9999, gamePort = 9998;

    public Server() {
        connections = new ArrayList<>();
        players = new ArrayList<>();
        closed = false;
    }

    @Override
    public void run() {
        System.out.println("Server started");
        try {
            chatServerSocket = new ServerSocket(chatPort);
            gameServerSocket = new ServerSocket(gamePort);
            threadPool = Executors.newCachedThreadPool();

            // constantly listen for connection requests from client
            while(!closed) {
                Socket clientSocket = chatServerSocket.accept();
                Socket playerSocket = gameServerSocket.accept();

                // creates handlers for each client connection and executes process in thread pool
                connections.add(new ClientHandler(clientSocket));
                threadPool.execute(connections.getLast());

                players.add(new PlayerHandler(playerSocket));
                threadPool.execute(players.getLast());
            }
        } catch (Exception e) {
            shutdown();
        }
    }

    class PlayerHandler implements Runnable {
        private Socket playerSocket;
        private DataOutputStream dataOut;
        private DataInputStream dataIn;
        private String color = "";

        public PlayerHandler(Socket socket) {
            this.playerSocket = socket;
        }

        @Override
        public void run() {
            try {
                dataOut = new DataOutputStream(playerSocket.getOutputStream());
                dataIn = new DataInputStream(playerSocket.getInputStream());

                // sets colors for players
                switch (connections.size()) {
                    case 1: {
                        connections.getFirst().out.println("/white");
                        this.color = "White";
                        break;
                    }
                    case 2: {
                        connections.getLast().out.println("/black");
                        this.color = "Black";
                        break;
                    }
                    default: break;
                }

                // replays moves made by players
                while (!closed) {
                    int i = dataIn.readInt();
                    for (PlayerHandler player : players) if (!player.color.equals(this.color)) {
                        player.dataOut.writeInt(i);
                        player.dataOut.flush();
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void shutdown() {
            try {
                dataIn.close(); dataOut.close();
                if (!playerSocket.isClosed()) {
                    playerSocket.close();
                }
                players.remove(this);
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
                    if (message.charAt(0) == '/') {
                        processClientCommand(message);
                    } else {
                        broadcast(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void processClientCommand(String message) {
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
            }
        }

        // sends message to all users
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
                connections.remove(this);
            } catch (IOException _) {}
        }
    }

    // shuts down threadpool and handlers, closes sockets
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

// to run from cmd
// cd IdeaProjects\ChessFight\target\classes
// java cockyadolescents.truechessthegame.Main