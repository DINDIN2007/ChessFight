package cockyadolescents.truechessthegame;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;

import static cockyadolescents.truechessthegame.GameApplication.*;

public class Client implements Runnable {
    public BufferedReader textIn;
    public PrintWriter textOut;
    public DataOutputStream dataOut;
    public DataInputStream dataIn;

    public boolean connected = false;
    public Socket chatSocket, gameSocket;
    private String address;
    private int chatPort = 9999, gamePort = 9998;

    public Client(String address) {this.address = address;}

    InputHandler inputHandler;
    MoveHandler moveHandler;
    Thread inputThread; Thread moveThread;

    @Override
    public void run() {
        try {
            chatSocket = new Socket(address, chatPort);
            gameSocket = new Socket(address, gamePort);

            // Chat
            textOut =  new PrintWriter(chatSocket.getOutputStream(), true);
            textIn = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
            connected = true;

            inputHandler = new InputHandler();
            inputThread = new Thread(inputHandler);
            inputThread.start();

            // Game
            dataOut = new DataOutputStream(gameSocket.getOutputStream());
            dataIn = new DataInputStream(gameSocket.getInputStream());

            moveHandler = new MoveHandler();
            moveThread = new Thread(moveHandler);
            moveThread.start();

            // reads incoming chat messages then prints in console
            String inMessage;
            while ((inMessage = textIn.readLine()) != null) {
                if (inMessage.charAt(0) == '/') processServerCommand(inMessage);
                else System.out.println(inMessage);
            }

        } catch (IOException e) {
            shutdown();
            Platform.runLater(() -> lobby.notification("Failed to connect to Server"));
        }
    }

    public void processServerCommand(String message) {
        switch (message) {
            case "/black": {
                OnlineGame.playerColor = "Black";
                Platform.runLater(() -> {
                    onlinegame.newGame();
                    onlinegame.turnBoard();
                    onlinegame.buttonBoard.setRotate(180);
                });
                break;
            }
            case "/white": {
                OnlineGame.playerColor = "White";
                Platform.runLater(() -> onlinegame.newGame());
                break;
            }
        }
    }

    class MoveHandler implements Runnable {
        @Override
        public void run() {
            try {;
                while (connected) if (dataIn.available() != 0) { // receive move
                    int x1 = dataIn.readInt(), y1 = dataIn.readInt();
                    int x2 = dataIn.readInt(), y2 = dataIn.readInt();
                    Platform.runLater(() -> onlinegame.updateMove(x1, y1, x2, y2));
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
                textOut.println(lobby.address);
                while (connected) {
                    String message = inputReader.readLine(); // reads user input
                    if (message.equals("/quit")) { // disconnect
                        textOut.println(message);
                        inputReader.close();
                        Platform.runLater(() -> lobby.disconnect());
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
        System.out.println("Client shutting down");
        try {
            // closes io streams
            if (textIn != null) {
                textIn.close(); textOut.close();
                dataIn.close(); dataOut.close();
            }
            // closes sockets
            if (chatSocket != null && !chatSocket.isClosed()) {
                chatSocket.close();
                gameSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}