package cockyadolescents.truechessthegame;

import java.io.*;
import java.net.Socket;

import static cockyadolescents.truechessthegame.ChessPiece.ChessBoard;
import static cockyadolescents.truechessthegame.OnlineGame.move;
import static cockyadolescents.truechessthegame.Main.*;

public class Client implements Runnable {
    private Socket chatSocket, gameSocket;
    private Thread inputThread, gameThread;

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

            // Game
            dataOut = new ObjectOutputStream(gameSocket.getOutputStream());
            dataIn = new ObjectInputStream(gameSocket.getInputStream());

            MoveHandler moveHandler = new MoveHandler();
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
            try {
                int[] mov;
                while (connected) {
                    if ((mov = (int[]) dataIn.readObject()) != null) { // receive move
                        int x1 = mov[0], y1 = mov[1];
                        int x2 = mov[2], y2 = mov[3];

                        ChessPiece selectedPiece = ChessBoard[x1][y1];
                        ChessPiece.moveChessPiece(selectedPiece, x2, y2);
                        onlinegame.movePiece(selectedPiece);

                        System.out.println(x1 + " " + y1 + " " + x2 + " " + y2); // debug
                        mov = null;
                    }
                    if (move != null) { // send move
                        String s = "";
                        for (int i = 0; i < 4; i++) s+= move[i] + " ";
                        System.out.println(s); // debug

                        dataOut.writeObject(move); dataOut.flush();
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

        } catch (IOException _) {}
    }
}