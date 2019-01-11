package demo;

import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class Mover extends Thread {

    private static boolean moving = false;
    public static final int STEPS = 90;
    private int delay = 5;
    private ArrayList<MoverInfo> infos = null;
    private Runnable action = null;

    public Mover(ArrayList<MoverInfo> infos, Runnable action) {
        this.infos = infos;
        this.action = action;
    }

    public static synchronized boolean isMoving() {
        return moving;
    }

    public static synchronized void setMoving(boolean moving) {
        Mover.moving = moving;
    }

    @Override
    public void run() {
        if (!isMoving()) {
            setMoving(true);
            try {
                //move first half.
                for (int i = 0; i < STEPS / 2; i++) {
                    Thread.sleep(delay);

                    for (int index = 0; index < infos.size(); index++) {
                        MoverInfo info = infos.get(index);
                        int width = info.getOldWidth() + (int) (i * info.getWidthChangeStep());
                        int height = info.getOldHeight() + (int) (i * info.getHeightChangeStep());
                        info.getNode().setSize(width, height);

                        int movementX = (int) (info.getCenterChangeX() / 2 * (1 - Math.cos(Math.toRadians(i * 2))));
                        int x = info.getOldCenterX() + movementX;
                        int movementY = (int) (info.getCenterChangeY() / 2 * (1 - Math.cos(Math.toRadians(i * 2))));
                        int y = info.getOldCenterY() + movementY;
                        info.getNode().setCenterLocation(x, y);
                    }
                }
                for (int i = 0; i < STEPS / 2; i++) {
                    Thread.sleep(delay);

                    for (int index = 0; index < infos.size(); index++) {
                        MoverInfo info = infos.get(index);
                        int width = info.getOldWidth() + (int) ((STEPS / 2 + i) * info.getWidthChangeStep());
                        int height = info.getOldHeight() + (int) ((STEPS / 2 + i) * info.getHeightChangeStep());
                        info.getNode().setSize(width, height);

                        int movementX = (int) (info.getCenterChangeX() / 2 * Math.sin(Math.toRadians(i * 2)));
                        int x = info.getOldCenterX() + info.getCenterChangeX() / 2 + movementX;
                        int movementY = (int) (info.getCenterChangeY() / 2 * Math.sin(Math.toRadians(i * 2)));
                        int y = info.getOldCenterY() + info.getCenterChangeY() / 2 + movementY;
                        info.getNode().setCenterLocation(x, y);
                    }
                }
                if (action != null) {
                    SwingUtilities.invokeLater(action);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            setMoving(false);
        }
    }
}
