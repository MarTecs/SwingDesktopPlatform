package demo;

import java.awt.Rectangle;

public class MoverInfo {

    private ImageNode node = null;
    private int newCenterX = 0;
    private int newCenterY = 0;
    private int newWidth = 0;
    private int newHeight = 0;
    private int oldCenterX = 0;
    private int centerChangeX = 0;
    private int oldCenterY = 0;
    private int centerChangeY = 0;
    private int oldWidth = 0;
    private double widthChangeStep = 0;
    private int oldHeight = 0;
    private double heightChangeStep = 0;

    public MoverInfo(ImageNode node, Rectangle newBounds) {
        this(node, (int) newBounds.getCenterX(), (int) newBounds.getCenterY(), newBounds.width, newBounds.height);
    }

    public MoverInfo(ImageNode node, int newCenterX, int newCenterY, int newWidth, int newHeight) {
        this.node = node;
        this.newCenterX = newCenterX;
        this.newCenterY = newCenterY;
        this.newWidth = newWidth;
        this.newHeight = newHeight;

        oldCenterX = node.getCenterLocation().x;
        centerChangeX = newCenterX - oldCenterX;
        oldCenterY = node.getCenterLocation().y;
        centerChangeY = newCenterY - oldCenterY;

        oldWidth = node.getWidth();
        widthChangeStep = (newWidth - oldWidth) / (double) Mover.STEPS;

        oldHeight = node.getHeight();
        heightChangeStep = (newHeight - oldHeight) / (double) Mover.STEPS;
    }

    public int getCenterChangeX() {
        return centerChangeX;
    }

    public int getCenterChangeY() {
        return centerChangeY;
    }

    public double getHeightChangeStep() {
        return heightChangeStep;
    }

    public int getNewCenterX() {
        return newCenterX;
    }

    public int getNewCenterY() {
        return newCenterY;
    }

    public int getNewHeight() {
        return newHeight;
    }

    public int getNewWidth() {
        return newWidth;
    }

    public ImageNode getNode() {
        return node;
    }

    public int getOldCenterX() {
        return oldCenterX;
    }

    public int getOldCenterY() {
        return oldCenterY;
    }

    public int getOldHeight() {
        return oldHeight;
    }

    public int getOldWidth() {
        return oldWidth;
    }

    public double getWidthChangeStep() {
        return widthChangeStep;
    }
}
