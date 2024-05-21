package org.cockyadolescents.truechessthegame;

import java.util.TimerTask;

class WaitingRoom extends TimerTask {
    public static int i = 0;
    public void run() {
        System.out.println("Timer ran" + ++i);
    }
}
