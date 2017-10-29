package com.leus.business;

import com.leus.UI.menuItem.ButtonListener;
import com.leus.controller.KeyControllerListener;
import com.leus.business.graphics.figure.AbstractFigure;
import com.leus.business.graphics.sprite.AbstractSprite;
import com.leus.business.service.FieldManager;
import com.leus.business.service.FigureManager;
import com.leus.business.service.score.ScoreFactor;
import com.leus.business.service.score.ScoreManager;
import com.leus.util.SettingsInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

    public static final int TILE_WIDTH = Integer.valueOf(SettingsInitializer.getPropertyValue("WidthTile"));
    public static final int TILE_HEIGHT = Integer.valueOf(SettingsInitializer.getPropertyValue("HeightTile"));

    private static volatile int delay = 500;

    private static final AbstractSprite[][] GAME_FIELD_MATRIX =
            new AbstractSprite[Integer.valueOf(SettingsInitializer.getPropertyValue("HeightWindowInTile")) + 1]
                    [Integer.valueOf(SettingsInitializer.getPropertyValue("WidthWindowInTile"))];

    private static final UpSpeedGameStrategy DEFAULT_UP_SPEED_GAME = () -> {
        long currentScore = ScoreManager.getScore();
        boolean lowScore = currentScore > 1000 && currentScore < 2000;
        boolean midScore = currentScore > 2000 && currentScore < 3000;
        boolean highScore = currentScore > 3000 && currentScore < 6000;
        boolean veryHighScore = currentScore > 6000 && currentScore < 9000;
        boolean superScore = currentScore > 9000;
        if (lowScore) {
            delay = 450;
        } else if (midScore) {
            delay = 375;
        } else if (highScore) {
            delay = 300;
        } else if (veryHighScore) {
            delay = 200;
        } else if (superScore) {
            delay = 125;
        }
    };

    private List<DeactivateListener> deactivateListeners = new ArrayList<>();
    private List<GameOverListener> gameOverListeners = new ArrayList<>();
    private FigureManager figureManager;
    private UpSpeedGameStrategy speedGameStrategy = DEFAULT_UP_SPEED_GAME;
    private AbstractFigure figure;
    private FieldManager fieldManager;
    private AbstractSprite[] spritesInFigure;
    private Timer timer = new Timer(delay, e -> startHelper());
    private boolean active;
    private boolean gameOver;

    public Game(FieldManager fieldManager, FigureManager figureManager) {
        if (fieldManager == null || figureManager == null) {
            throw new NullPointerException("'Field Manager' or 'Figure Manager' can't be null");
        }

        this.figureManager = figureManager;
        this.fieldManager = fieldManager;
        figure = this.figureManager.createRandomFigure();
        this.spritesInFigure = figure.getSpritesInFigure();
    }

    public static int getDelay() {
        return delay;
    }

    public static void setDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay can't be negative");
        }

        Game.delay = delay;
    }

    public static AbstractSprite[][] getGameFieldMatrix() {
        return Arrays.copyOf(GAME_FIELD_MATRIX, GAME_FIELD_MATRIX.length);
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    public boolean isActive() {
        return active;
    }

    public void setSpeedGameStrategy(UpSpeedGameStrategy speedGameStrategy) {
        this.speedGameStrategy = speedGameStrategy;
    }

    public void start() {
        initHelper();
        timer.start();
    }

    public void restart() {
        restartHelper();
        timer.restart();
    }

    public void restart(int delay) {
        restartHelper(delay);
        timer.restart();
    }

    @Override
    public String toString() {
        return "Game{" + "figure=" + figure +
                ", active=" + active + '}';
    }

    public void paint(Graphics g) {
        figure.paint(g);
        for (AbstractSprite[] line : GAME_FIELD_MATRIX) {
            for (AbstractSprite elem : line) {
                if (elem != null) {
                    elem.paint(g);
                }
            }
        }

        ScoreManager.setPositionScoreX(ScoreManager.DEFAULT_POSITION_SCORE_X);
        ScoreManager.setPositionScoreY(ScoreManager.DEFAULT_POSITION_SCORE_Y);
        ScoreManager.setScoreFont(ScoreManager.DEFAULT_SCORE_FONT);
        ScoreManager.drawScore(g, ScoreFactor.getFactorAsString());
    }

    public void addDeactivateListener(DeactivateListener listener) {
        deactivateListeners.add(listener);
    }

    public void removeDeactivateListener(DeactivateListener listener) {
        deactivateListeners.remove(listener);
    }

    public void addGameOverListener(GameOverListener listener) {
        gameOverListeners.add(listener);
    }

    public void removeGameOverListener(GameOverListener listener) {
        gameOverListeners.remove(listener);
    }

    private void startHelper() {

        if (!isGameOver()) {
            if (figure.isFrozen()) {
                figure.leaveOnTheField();
                moveDownAndClearSprite();
                speedGameStrategy.upSpeedGame();
                figure = figureManager.createRandomFigure();
                spritesInFigure = figure.getSpritesInFigure();
                ScoreFactor.checkCollapsed();
            } else {
                figure.moveDown();
            }

            timer.setDelay(delay);
        }
    }

    private void initHelper() {
        ScoreFactor.setCollapsed();
    }

    private void restartHelper() {
        delay = 500;
        gameOver = false;
        clearField();
        ScoreManager.resetScore();
        ScoreFactor.resetFactor();
        ScoreFactor.setCollapsed();
    }

    private void restartHelper(int delay) {
        this.delay = delay;
        gameOver = false;
        clearField();
        ScoreManager.resetScore();
        ScoreFactor.resetFactor();
        ScoreFactor.setCollapsed();
    }

    private void moveDownAndClearSprite() {
        boolean isInAir = true;
        while (isInAir) {
            moveDownSpritesInAir();
            fieldManager.clearChainsSpritesFromField(GAME_FIELD_MATRIX);
            isInAir = fieldManager.isSpritesInSpace(GAME_FIELD_MATRIX);
        }
    }

    private void moveDownSpritesInAir() {
        if (!fieldManager.isSpritesInSpace(GAME_FIELD_MATRIX)) {
            return;
        }

        fieldManager.moveDownSpritesInSpace(GAME_FIELD_MATRIX);
        moveDownSpritesInAir();
    }

    private boolean isGameOver() {
        for (AbstractSprite elem : spritesInFigure) {
            if (elem.isOutField()) {
                active = false;
                gameOver = true;
                timer.stop();
                for (GameOverListener listener : gameOverListeners) {
                    listener.onEvent();
                }
                return true;
            }
        }

        return false;
    }

    private void clearField() {
        for (AbstractSprite[] line : GAME_FIELD_MATRIX) {
            for (AbstractSprite elem : line) {
                if (elem != null) {
                    elem.clear();
                }
            }
        }
    }

    public class KeyListenerImpl implements KeyControllerListener {
        @Override
        public boolean isActive() {
            return active;
        }

        @Override
        public void onEvent(KeyEvent e) {
            if (active) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    keyDown();
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    keyUp();
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    keyLeft();
                }

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    keyRight();
                }
            }
        }

        private void keyUp() {
            if (!figure.isFrozen()) {
                figure.rotate();
            }
        }

        private void keyLeft() {
            if (!figure.isFrozen()) {
                figure.moveLeft();
            }
        }

        private void keyRight() {
            if (!figure.isFrozen()) {
                figure.moveRight();
            }
        }

        private void keyDown() {
            if (!figure.isFrozen()) {
                figure.fastMoveDown();
            }
        }
    }

    public class ButtonListenerImpl implements ButtonListener {
        @Override
        public void onEvent() {
            for (DeactivateListener listener : deactivateListeners) {
                listener.deactivate();
            }

            active = true;
            if (!gameOver) {
                start();
            } else {
                restart();
            }
        }
    }

    public class DeactivateListenerImpl implements DeactivateListener {
        @Override
        public void deactivate() {
            active = false;
        }
    }

}