package cockyadolescents.truechessthegame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Client implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done = false;
    private static int port = 9999;

//    private String username = "anonymous" + (int) (Math.random() * 9999);
//    private static Timer timer;
//    private static TimerTask task;

    @Override
    public void run() {
        try {
            Socket clientSocket = new Socket("127.0.0.1", port);
            out =  new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            InputHandler inputHandler = new InputHandler();
            Thread thread = new Thread(inputHandler);
            thread.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            in.close(); out.close();
            if (!clientSocket.isClosed())
                clientSocket.close();
        } catch (IOException e) {
            // ignore
        }
    }

    class InputHandler implements Runnable{
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if (message.equals("/quit")) {
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
