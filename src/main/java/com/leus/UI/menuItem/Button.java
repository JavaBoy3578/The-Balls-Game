package com.leus.UI.menuItem;

import java.awt.*;

public class Button extends MenuItem {
    private ButtonListener listener;
    private final Image buttonImg;
    private String name = "Button";

    public Button(Image buttonImg, ButtonListener listener, int coordinateX, int coordinateY, int width, int height) {
        super(coordinateX, coordinateY, width, height);
        this.buttonImg = buttonImg;
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void push() {
        listener.onEvent();
    }

    public void paint(Graphics g) {
        g.drawImage(buttonImg, getCoordinateX(), getCoordinateY(), getWidth(), getHeight(), null);
    }

    public void clear() {
        buttonImg.flush();
    }

    @Override
    public String toString() {
        return "Button '" + (name.equals("Button") ? "" : name) + "'{x=" + getCoordinateX() + ", y=" + getCoordinateY() +
                ", width=" + getWidth() + ", height=" + getHeight() + "}";
    }

    @Override
    public int hashCode() {
        int result = getCoordinateX();
        result = 31 * result + getCoordinateY();
        result = 31 * result + getHeight();
        result = 31 * result + getWidth();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Button)) {
            return false;
        }

        Button that = (Button) obj;

        return  this.getCoordinateY() == that.getCoordinateY() && this.getCoordinateX() == that.getCoordinateX() &&
                this.getWidth() == that.getWidth() && this.getHeight() ==  that.getHeight();
    }

    public class CursorListenerImpl implements CursorListener {
        @Override
        public void onEvent() {
            push();
        }
    }
}
