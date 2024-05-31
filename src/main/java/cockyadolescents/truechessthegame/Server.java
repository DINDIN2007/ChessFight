package cockyadolescents.truechessthegame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    public static Vector<Client> clients = new Vector<Client>();
    public static int port = 56969;

    public static void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            clients.add(new Client(clientSocket));
        }
    }

    public static void main(String[] args) throws IOException {
        startServer();
    }
}
