package org.cockyadolescents.truechessthegame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ServerClient {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private String username = "anonymous" + (int) (Math.random() * 9999);

    private static Timer timer;
    private static TimerTask task;

    public ServerClient(Socket clientSocket) throws IOException {
        try {
            this.clientSocket = clientSocket;
            this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {

        }
    }

    public static void run() {
        timer = new Timer();
        task = new WaitingRoom();
        timer.schedule(task, 2000, 1000);
    }
}
