package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.Socket;

import static cockyadolescents.truechessthegame.ChessPiece.ChessBoard;
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

            /*out =  new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread thread = new Thread(inputHandler);
            thread.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) { // reads from server
                System.out.println(inMessage);
            }*/

            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());

            /*while () {

            }*/
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

    public void sendMove(int[] move) throws IOException {
        for (int i = 0; i < 4; i++) {
            dataOut.writeInt(move[i]); dataOut.flush();
        }
    }

    class MoveHandler implements Runnable {

        @Override
        public void run() {
            try {
                while (connected) {
                    int x1 = dataIn.readInt();
                    int y1 = dataIn.readInt();
                    int y2 = dataIn.readInt();
                    int x2 = dataIn.readInt();
                    ChessPiece.moveChessPiece(ChessBoard[x1][y1], x2, y2);
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

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
                        out.println(message); // sends to server
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