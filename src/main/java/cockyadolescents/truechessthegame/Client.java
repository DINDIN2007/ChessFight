package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.Socket;

import static cockyadolescents.truechessthegame.ChessPiece.ChessBoard;
import static cockyadolescents.truechessthegame.OnlineGame.move;
import static cockyadolescents.truechessthegame.Main.*;

public class Client implements Runnable {
    private Socket chatSocket;
    private Socket gameSocket;
    public BufferedReader textIn;
    public PrintWriter textOut;
    public ObjectOutputStream dataOut;
    public ObjectInputStream dataIn;

    private boolean connected = true;
    private String address;
    private int port1 = 9999, port2 = 9998;

    public Client(String address) {this.address = address;}

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

            String inMessage;
            while ((inMessage = textIn.readLine()) != null) {
                System.out.println(inMessage);
            }

            // Game
            dataOut = new ObjectOutputStream(gameSocket.getOutputStream());
            dataIn = new ObjectInputStream(gameSocket.getInputStream());

            MoveHandler moveHandler = new MoveHandler();
            Thread moveThread = new Thread(moveHandler);
            moveThread.start();

        } catch (IOException e) {
            waitingroom.notification("Failed to connect to Server");
            shutdown();
        }
    }

    class MoveHandler implements Runnable {
        @Override
        public void run() {
            try {
                int[] mov;
                while (connected) {
                    if ((mov = (int[]) dataIn.readObject()) != null) { // receive move
                        int x1 = mov[0], y1 = mov[1];
                        int x2 = mov[2], y2 = mov[3];

                        ChessPiece selectedPiece = ChessBoard[x1][y1];
                        ChessPiece.moveChessPiece(selectedPiece, x2, y2);
                        onlinegame.movePiece(selectedPiece);

                        mov = null;
                        System.out.println(x1 + " " + y1 + " " + x2 + " " + y2); // debug
                    }
                    if (move != null) { // send move
                        dataOut.writeObject(move); dataOut.flush();
                        for (int i = 0; i < 4; i++) {
                            System.out.print(move[i] + " "); // debug
                        }
                        System.out.println();
                        move = null;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
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
        try {
            textIn.close(); textOut.close();
            dataIn.close(); dataOut.close();
            if (!chatSocket.isClosed())
                chatSocket.close();
            if (!gameSocket.isClosed())
                gameSocket.close();
        } catch (IOException _) {}
    }

    public static void main(String[] args) {
        Client client = new Client("localhost");
        client.run();
    }
}