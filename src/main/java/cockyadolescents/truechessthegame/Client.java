package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.Socket;

import static cockyadolescents.truechessthegame.ChessPiece.ChessBoard;
import static cockyadolescents.truechessthegame.OnlineGame.move;
import static cockyadolescents.truechessthegame.Main.*;

public class Client implements Runnable {
    private Socket socket;
    public BufferedReader in;
    public PrintWriter out;

    public DataOutputStream dataOut;
    public DataInputStream dataIn;

    private boolean connected = true;
    private String address;
    private int port = 9999;

    public Client(String address) {this.address = address;}

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            // Chat
            out =  new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread inputThread = new Thread(inputHandler);
            inputThread.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }

            // Game
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataIn = new DataInputStream(socket.getInputStream());

            while (connected) {
                receiveMove();
                sendMove();
            }

        } catch (IOException e) {
            waitingroom.notification("Failed to connect to Server");
            shutdown();
        }
    }

    public void sendMove() {
        if (move != null) try {
            for (int i = 0; i < 4; i++) {
                dataOut.writeInt(move[i]);
                System.out.print(move[i] + " "); // debug
                dataOut.flush();
            }
            System.out.println();
            move = null;
        } catch (IOException e) {
            shutdown();
        }
    }

    public void receiveMove() {
        try {
            if (dataIn.available() != 0) {
                int x1 = dataIn.readInt(), y1 = dataIn.readInt();
                int x2 = dataIn.readInt(), y2 = dataIn.readInt();
                System.out.println(x1 + " " + y1 + " " + x2 + " " + y2); // debug

                ChessPiece selectedPiece = ChessBoard[x1][y1];
                ChessPiece.moveChessPiece(selectedPiece, x2, y2);
                onlinegame.movePiece(selectedPiece);
            }
        } catch (IOException e) {
            shutdown();
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

    public void shutdown() {
        connected = false;
        try {
            in.close(); out.close();
            dataIn.close(); dataOut.close();
            if (!socket.isClosed())
                socket.close();
        } catch (IOException _) {}
    }

    public static void main(String[] args) {
        Client client = new Client("localhost");
        client.run();
    }
}