package com.leus.controller;

import com.leus.model.fields.GameField;
import com.leus.model.graphics.figures.Figure;
import com.leus.model.graphics.sprites.Sprite;
import com.leus.view.displays.GameFrame;
import com.leus.view.panels.GamePanel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BallsController extends KeyAdapter {

    private GameField gameField = GameFrame.getGameField();
    private GamePanel gamePanel = gameField.getGamePanel();
    private Figure figure;
    private Sprite[] spritesInFigure;

    @Override
    public void keyPressed(KeyEvent e) {

        figure = gameField.getFigure();
        spritesInFigure = figure.getSpritesInFigure();

        if (!gameField.isGameOver()) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                for (Sprite sprite : spritesInFigure) {
                    if (!sprite.isFrozen()) {
                        figure.fastMoveDown();
                    }
                }
            }


            if (e.getKeyCode() == KeyEvent.VK_UP) {
                for (Sprite sprite : spritesInFigure) {
                    if (!sprite.isFrozen()) {
                        figure.rotate();
                    }
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                for (Sprite sprite : spritesInFigure) {
                    if (!sprite.isFrozen()) {
                        figure.moveLeft();
                    }
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                for (Sprite sprite : spritesInFigure) {
                    if (!sprite.isFrozen()) {
                        figure.moveRight();
                    }
                }
            }
        }

        gamePanel.repaint();
    }
}
