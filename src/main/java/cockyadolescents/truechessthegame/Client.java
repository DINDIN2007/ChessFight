package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.Socket;

import static cockyadolescents.truechessthegame.Main.*;

public class Client implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private DataOutputStream dataOut;
    private DataInputStream dataIn;

    private boolean connected = true;
    private String address;
    private int port = 9999;

    public Client(String address) {this.address = address;}

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());

            out =  new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread thread = new Thread(inputHandler);
            thread.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) { // read from server
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            waitingroom.notification("Failed to connect to Server");
            shutdown();
        }
    }

    public void shutdown() {
        connected = false;
        try {
            in.close(); out.close();
            dataIn.close(); dataOut.close();
            if (!socket.isClosed())
                socket.close();
        } catch (IOException _) {}
    }

    /*public void OutMove(int x1, int y1, int x2, int y2) {
        try {
            if (connected) {
                dataOut.writeInt(x1);
                dataOut.writeInt(y1);
                dataOut.writeInt(x2);
                dataOut.writeInt(y2);
                dataOut.flush();
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void InMove() {
        try {
            if (connected) {
                dataIn.readInt();
                dataIn.readInt();
                dataIn.readInt();
                dataIn.readInt();
                dataOut.flush();
            }
        } catch (IOException e) {
            shutdown();
        }
    }*/

    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
                while (connected) {
                    String message = inputReader.readLine(); // reads user input
                    if (message.equals("/quit")) { // disconnect
                        out.println(message);
                        inputReader.close();
                        shutdown();
                    } else {
                        out.println(message); // send to server
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost");
        client.run();
    }
}

// cd IdeaProjects\ChessFight\target\classes
// java cockyadolescents.truechessthegame.Client