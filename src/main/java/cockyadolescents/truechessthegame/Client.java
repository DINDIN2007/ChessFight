package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.Socket;

import static cockyadolescents.truechessthegame.ChessPiece.ChessBoard;
import static cockyadolescents.truechessthegame.Main.*;

public class Client implements Runnable {
    private Socket chatSocket, gameSocket;
    private Thread inputThread, gameThread;

    public BufferedReader textIn;
    public PrintWriter textOut;
    public DataOutputStream dataOut;
    public DataInputStream dataIn;

    private boolean connected = true;
    private String address;
    private int port1 = 9999, port2 = 9998;

    public Client(String address) {this.address = address;}

    MoveHandler moveHandler;
    @Override
    public void run() {
        try {
            chatSocket = new Socket(address, port1);
            gameSocket = new Socket(address, port2);

            // Chat
            textOut =  new PrintWriter(chatSocket.getOutputStream(), true);
            textIn = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread inputThread = new Thread(inputHandler);
            inputThread.start();

            // Game
            dataOut = new DataOutputStream(gameSocket.getOutputStream());
            dataIn = new DataInputStream(gameSocket.getInputStream());

            moveHandler = new MoveHandler();
            Thread moveThread = new Thread(moveHandler);
            moveThread.start();

            String inMessage;
            while ((inMessage = textIn.readLine()) != null) {
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            waitingroom.notification("Failed to connect to Server");
            shutdown();
        }
    }

    class MoveHandler implements Runnable {
        @Override
        public void run() {
            try {;
                while (connected) {
                    if (dataIn.available() != 0) { // receive move
                        int x1 = dataIn.readInt(), y1 = dataIn.readInt();
                        int x2 = dataIn.readInt(), y2 = dataIn.readInt();

                        ChessPiece selectedPiece = ChessBoard[x1][y1];
                        ChessPiece.moveChessPiece(selectedPiece, x2, y2);
                        onlinegame.movePiece(selectedPiece);
                        System.out.println("read");
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }

        public void sendMove(int[] move) {
            try {
                for (int i = 0; i < 4; i++) {
                    dataOut.writeInt(move[i]);
                    dataOut.flush();
                }
                onlinegame.move = null;
                System.out.println("sent"); // debug
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
                textOut.println(waitingroom.address);
                while (connected) {
                    String message = inputReader.readLine(); // reads user input
                    if (message.equals("/quit")) { // disconnect
                        textOut.println(message);
                        inputReader.close();
                        shutdown();
                    } else {
                        textOut.println(message); // sends to server
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public void shutdown() {
        connected = false;
        System.out.println("Disconnected");
        try {
            textIn.close(); textOut.close();
            dataIn.close(); dataOut.close();
            if (!chatSocket.isClosed())
                chatSocket.close();
            if (!gameSocket.isClosed())
                gameSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}