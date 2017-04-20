package com.leus.game.graphics;

import com.leus.displays.GameFrame;
import com.leus.game.fields.GameField;

import java.awt.event.KeyEvent;

public class Figure {

    private Ball firstBall;
    private Ball secondBall;

    private int x;
    private int y;

    private boolean isLeftSecondBall = true;
    private boolean isRightSecondBall = true;
    private boolean isVertical;

    private int[][] matrix = GameField.getMatrix();

    public boolean isVertical() {
        return isVertical;
    }

    public Ball getFirstBall() {
        return firstBall;
    }

    public Ball getSecondBall() {
        return secondBall;
    }

    public Figure(Ball firstBall, Ball secondBall) {
        this.firstBall = firstBall;
        this.secondBall = secondBall;
        this.x = firstBall.getX();
        this.y = firstBall.getY();
    }

    public void rotate() {
        //if(!isWall(description)){
        if ((secondBall.getY() == firstBall.getY()) && (secondBall.getX() > firstBall.getX())) {
            secondBall.setX(firstBall.getX());
            secondBall.setY(firstBall.getY() + GameField.TILE_HEIGHT);
            x = firstBall.getX();
            y = firstBall.getY();
            isLeftSecondBall = false;
            isRightSecondBall = false;
            isVertical = true;
        } else if ((secondBall.getY() == firstBall.getY()) && (secondBall.getX() < firstBall.getX())) {
            secondBall.setX(firstBall.getX());
            secondBall.setY(firstBall.getY() - GameField.TILE_HEIGHT);
            x = secondBall.getX();
            y = secondBall.getY();
            isLeftSecondBall = false;
            isRightSecondBall = false;
            isVertical = true;
        } else if ((secondBall.getY() > firstBall.getY()) && (secondBall.getX() == firstBall.getX())) {
            secondBall.setY(firstBall.getY());
            secondBall.setX(firstBall.getX() - GameField.TILE_WIDTH);
            x = secondBall.getX();
            y = secondBall.getY();
            isLeftSecondBall = true;
            isRightSecondBall = false;
            isVertical = false;
        } else if ((secondBall.getY() < firstBall.getY()) && (secondBall.getX() == firstBall.getX())) {
            secondBall.setY(firstBall.getY());
            secondBall.setX(firstBall.getX() + GameField.TILE_WIDTH);
            x = firstBall.getX();
            y = firstBall.getY();
            isLeftSecondBall = false;
            isRightSecondBall = true;
            isVertical = false;
        }
       // }
    }

    public void move(int description) {
        if (!isWall(description)) {
            x += (description - 38) * GameField.TILE_WIDTH;
            if (isRightSecondBall) {
                firstBall.setX(x);
                secondBall.setX(x + GameField.TILE_WIDTH);
            } else if (isLeftSecondBall) {
                firstBall.setX(x + GameField.TILE_WIDTH);
                secondBall.setX(x);
            } else if (isVertical) {
                firstBall.setX(x);
                secondBall.setX(x);
            }
        }
    }

    public void fastMoveDown() {
        while (!firstBall.isFrozen() && !secondBall.isFrozen()) {
            firstBall.moveDown();
            secondBall.moveDown();
        }
    }

    private boolean isWall(int description) {
        if (description == KeyEvent.VK_LEFT && (x < 0 + GameField.TILE_WIDTH || matrix[firstBall.getY() / GameField.TILE_HEIGHT][firstBall.getX() / GameField.TILE_WIDTH - 1] > 0))
            return true;

        if (description == KeyEvent.VK_RIGHT && (x > GameFrame.WIDTH_GAME_FRAME - GameField.TILE_WIDTH * 2 || matrix[secondBall.getY() / GameField.TILE_HEIGHT][secondBall.getX() / GameField.TILE_WIDTH + 1] > 0))
            return true;

        return false;
    }
}
