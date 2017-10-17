package com.leus.business.service.cleaner;

import com.leus.view.Display;
import com.leus.business.graphics.sprite.AbstractSprite;
import com.leus.business.graphics.sprite.Ball;
import com.leus.business.service.score.CountingScoreStrategy;
import com.leus.business.service.score.ScoreFactor;
import com.leus.business.service.score.ScoreManager;

import java.util.HashSet;
import java.util.Set;

public class CleanerBalls implements CleaningSpritesStrategy {

    private static final int COUNT_FOR_CLEAR_BALLS = 4;
    private final Set<Ball> ballsReadyToClear = new HashSet<>((Display.HEIGHT_WINDOW_IN_TILES + 1) * Display.WIDTH_WINDOW_IN_TILES);
    private final Set<Ball> chainsBalls = new HashSet<>((Display.HEIGHT_WINDOW_IN_TILES + 1) * Display.WIDTH_WINDOW_IN_TILES);
    private AbstractSprite[][] gameFieldMatrix;
    private CountingScoreStrategy counter;

    public CleanerBalls() {
        counter = () -> {
            int scoreForFourBalls = 10;
            ScoreManager.addScore((scoreForFourBalls * chainsBalls.size()) * ScoreFactor.getFactor());
        };
    }

    public CleanerBalls(CountingScoreStrategy counter) {
        this.counter = counter;
    }

    public int getAmountBallsInChains() {
        return chainsBalls.size();
    }

    public void clearChainsSprites(AbstractSprite[][] gameFieldMatrix) {
        this.gameFieldMatrix = gameFieldMatrix;
        findBallsReadyToClear();
        clearBalls();
    }

    private void findBallsReadyToClear() {
        for (int i = 0; i < gameFieldMatrix.length; i++) {
            for (int j = 0; j < gameFieldMatrix[i].length; j++) {
                if (gameFieldMatrix[i][j] != null && gameFieldMatrix[i][j].getClass() == Ball.class) {
                    Ball currentBall = (Ball) gameFieldMatrix[i][j];
                    if (!ballsReadyToClear.contains(currentBall)) {
                        findChainsBalls(currentBall, i, j + 1);
                        findChainsBalls(currentBall, i, j - 1);
                        findChainsBalls(currentBall, i - 1, j);
                        findChainsBalls(currentBall, i + 1, j);
                    }

                    if (chainsBalls.size() >= COUNT_FOR_CLEAR_BALLS) {
                        ballsReadyToClear.addAll(chainsBalls);
                        counter.countingScore();
                        chainsBalls.clear();
                        ScoreFactor.incrementFactor();
                    } else {
                        chainsBalls.clear();
                    }
                }
            }
        }
    }

    private void clearBalls() {
        for (Ball elem : ballsReadyToClear) {
            elem.clear();
        }

        ballsReadyToClear.clear();
    }

    private void findChainsBalls(Ball prevBall, int line, int column) {
        if (isBoundOfGameFieldMatrix(column, line) || isNotBallOrDifferentColor(prevBall, column, line)) {
            if (!chainsBalls.contains(prevBall)) {
                chainsBalls.add(prevBall);
            }
        } else {
            Ball currentBall = (Ball) gameFieldMatrix[line][column];

            if (!chainsBalls.contains(prevBall)) {
                chainsBalls.add(prevBall);
            }

            if (!chainsBalls.contains(currentBall)) {
                findChainsBalls(currentBall, line, column + 1);
                findChainsBalls(currentBall, line, column - 1);
                findChainsBalls(currentBall, line + 1, column);
                findChainsBalls(currentBall, line - 1, column);
            }
        }
    }

    private boolean isBoundOfGameFieldMatrix(int column, int line) {
        return column < 0 || line > gameFieldMatrix.length - 1 || line < 0 || column > gameFieldMatrix[line].length - 1;
    }

    private boolean isNotBallOrDifferentColor(Ball prevBall, int column, int line) {
        return gameFieldMatrix[line][column] == null || gameFieldMatrix[line][column].getClass() != prevBall.getClass() || ((Ball) gameFieldMatrix[line][column]).getColor() != prevBall.getColor();
    }
}
