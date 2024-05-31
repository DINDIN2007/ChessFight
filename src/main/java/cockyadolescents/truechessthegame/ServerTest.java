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

public class ServerTest implements Runnable {

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket serversocket;
    private Boolean closed;
    private ExecutorService pool;


    public ServerTest() {
        connections = new ArrayList<>();
        closed = false;
    }

    @Override
    public void run() {
        try {
            serversocket = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while(!closed) {
                Socket client = serversocket.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.equals(handler);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            closed = true;
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
                System.out.println(username + " connected");
                broadcast(username + " joined the chat");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/username")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(username + " renamed themselves to " + messageSplit[1]);
                            System.out.println(username + " renamed themselves to " + messageSplit[1]);
                            username = messageSplit[1];
                            out.println("changed to username to " + username);
                        } else {
                            out.println("no username provided");
                        }
                    } else if (message.startsWith("/quit")) {
                        broadcast(username + " left the chat");
                        // quit
                    } else {
                        broadcast(username + ":" + message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void broadcast (String message) {
            for (ConnectionHandler ch : connections) {
                ch.sendMessage(message);
            }

        }

        public void sendMessage (String message) {
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

    public static void main (String[] args) {
        ServerTest server = new ServerTest();
        server.run();
    }
}
