package com.leus.model.fields;


import com.leus.model.graphics.figures.AbstractFigure;
import com.leus.model.graphics.figures.TwoBallFigure;
import com.leus.model.graphics.sprites.AbstractSprite;
import com.leus.model.service.FieldService;
import com.leus.model.service.ScoreManager;
import com.leus.view.displays.GameFrame;
import com.leus.view.panels.GamePanel;

import javax.swing.*;

public class GameField {

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final long DELAY = 500;

    public static GameField gameField;

    private AbstractSprite[][] spritesOnField;
    private AbstractFigure figure;
    private JPanel gamePanel;
    private FieldService fieldService;
    private ScoreManager scoreManager;
    private AbstractSprite[] spritesInFigure;

    private GameField() {
        this(new AbstractSprite[GameFrame.FIELD_HEIGHT_IN_TILE + 2][GameFrame.FIELD_WIDTH_IN_TILE], new TwoBallFigure(), new GamePanel(), new FieldService(), new ScoreManager());
    }

    private GameField(AbstractSprite[][] spritesOnField, AbstractFigure figure, JPanel gamePanel, FieldService fieldService, ScoreManager scoreManager) {
        this.spritesOnField = spritesOnField;
        this.figure = figure;
        this.fieldService = fieldService;
        this.scoreManager = scoreManager;
        this.spritesInFigure = figure.getSpritesInFigure();
    }

    public static GameField getGameFieldInstance(AbstractSprite[][] spritesOnField, AbstractFigure figure, JPanel gamePanel, FieldService fieldService, ScoreManager scoreManager) {
        if (gameField == null) {
            gameField = new GameField(spritesOnField, figure, gamePanel, fieldService, scoreManager);
        }

        return gameField;
    }

    public JPanel getGamePanel() {
        return gamePanel;
    }

    public void setGamePanel(JPanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public AbstractSprite[][] getSpritesOnField() {
        return spritesOnField;
    }

    public void setSpritesOnField(AbstractSprite[][] spritesOnField) {
        this.spritesOnField = spritesOnField;
    }

    public AbstractFigure getFigure() {
        return figure;
    }

    public void setFigure(AbstractFigure figure) {
        this.figure = figure;
    }

    public void startGameProcess() {
        initializeGameLoop();
        gameLoop();
    }

    public boolean isGameOver() {
        for (AbstractSprite abstractSprite : spritesInFigure) {
            if (abstractSprite.isOutField()) {
                return true;
            }
        }

        return false;
    }

    private void initializeGameLoop() {
        for (int i = 0; i < spritesInFigure.length; i++) {
            spritesOnField[spritesInFigure[i].getY() / TILE_HEIGHT][spritesInFigure[i].getX() / TILE_WIDTH] = spritesInFigure[i];
        }
    }

    private void gameLoop() {
        while (!isGameOver()) {
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAllFrozenSprite(spritesInFigure)) {
                leaveFrozenSprite(spritesInFigure);
                initializeGameLoop();

                try {
                    figure = figure.getClass().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (isFrozenSprite(spritesInFigure)) {
                for (AbstractSprite abstractSprite : spritesInFigure) {
                    if (abstractSprite.isFrozen()) {
                        abstractSprite.leaveOnTheField();
                    } else {
                        abstractSprite.moveDown();
                    }
                }
            } else {
                figure.moveDownFigure();
            }

            gamePanel.repaint();
        }
    }

    private boolean isFrozenSprite(AbstractSprite[] spritesInFigure) {
        for (AbstractSprite abstractSprite : spritesInFigure) {
            if (abstractSprite.isFrozen()) {
                return true;
            }
        }

        return false;
    }

    private boolean isAllFrozenSprite(AbstractSprite[] spritesInFigure) {
        int count = 0;
        for (AbstractSprite abstractSprite : spritesInFigure) {
            if (abstractSprite.isFrozen()) {
                count++;
            }
        }

        if (count == spritesInFigure.length) {
            return true;
        }

        return false;
    }

    private void leaveFrozenSprite(AbstractSprite[] spritesInFigure) {
        for (AbstractSprite abstractSprite : spritesInFigure) {
            if (abstractSprite.isFrozen()) {
                abstractSprite.leaveOnTheField();
            }
        }
    }
}
