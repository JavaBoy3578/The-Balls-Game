package com.leus.run;

import com.leus.UI.menuItem.Button;
import com.leus.UI.menuItem.Cursor;
import com.leus.UI.menuItem.Logo;
import com.leus.UI.menu.AbstractMenu;
import com.leus.UI.menu.GameOverMenu;
import com.leus.UI.menu.MainMenu;
import com.leus.UI.menu.ScoreMenu;
import com.leus.controller.KeyController;
import com.leus.view.Display;
import com.leus.view.GamePanel;
import com.leus.business.Game;
import com.leus.business.graphics.figure.factory.CruciateFigureFactory;
import com.leus.business.graphics.figure.factory.ThreeBallFigureFactory;
import com.leus.business.graphics.figure.factory.TwoBallFigureFactory;
import com.leus.business.graphics.sprite.factory.RandBallFactory;
import com.leus.business.service.FieldManager;
import com.leus.business.service.FigureManager;
import com.leus.business.service.score.RecordTable;
import com.leus.util.PathsToResources;
import com.leus.util.ResourceLoader;

public class Runner {
    public static void main(String[] args) {
        int figureStartPositionX = Display.WIDTH_WINDOW / 2 - Game.TILE_WIDTH;
        int figureStartPositionY = 0;

        Display display = new Display();

        FigureManager figureManager = new FigureManager();
        figureManager.addFigureFactory(new TwoBallFigureFactory(figureStartPositionX, figureStartPositionY, new RandBallFactory()));
        figureManager.addFigureFactory(new ThreeBallFigureFactory(figureStartPositionX, figureStartPositionY, new RandBallFactory()));
        figureManager.addFigureFactory(new CruciateFigureFactory(figureStartPositionX, figureStartPositionY, new RandBallFactory()));

        Game game = new Game(new FieldManager(), figureManager);

        Logo mainLogo = new Logo(ResourceLoader.loadImage(PathsToResources.LOGO.getPath()), -60, 100, 400, 300);
        Logo gameOverLogo = new Logo(ResourceLoader.loadImage(PathsToResources.GAME_OVER.getPath()), -60, 60, 400, 350);

        Cursor mainCursor = new Cursor(ResourceLoader.loadImage(PathsToResources.MENU_CURSOR.getPath()), 30, 325, 32, 32, 75);
        Cursor scoreCursor = new Cursor(ResourceLoader.loadImage(PathsToResources.MENU_CURSOR.getPath()), Display.WIDTH_WINDOW - 160, Display.HEIGHT_WINDOW - 35, 32, 32, 0);
        Cursor gameOverCursor = new Cursor(ResourceLoader.loadImage(PathsToResources.MENU_CURSOR.getPath()), 20, 325, 32, 32, 75);

        MainMenu mainMenu = new MainMenu(ResourceLoader.loadImage(PathsToResources.BACK_GROUND_MENU.getPath()), mainCursor);
        ScoreMenu scoreMenu = new ScoreMenu(ResourceLoader.loadImage(PathsToResources.BACK_GROUND_MENU.getPath()), scoreCursor, RecordTable.getInstance());
        GameOverMenu gameOverMenu = new GameOverMenu(ResourceLoader.loadImage(PathsToResources.BACK_GROUND_MENU.getPath()), gameOverCursor);

        Button startButton = new Button(ResourceLoader.loadImage(PathsToResources.START_BUTTON.getPath()), game.new ButtonListenerImpl(), 60, 300, 150, 75);
        Button exitButtonMainMenu = new Button(ResourceLoader.loadImage(PathsToResources.EXIT_BUTTON.getPath()), () -> {
            display.destroy();
            System.exit(0);
        },60, 450, 150, 75);
        Button exitButtonGameOverMenu = new Button(ResourceLoader.loadImage(PathsToResources.EXIT_BUTTON.getPath()), () -> {
            display.destroy();
            System.exit(0);
        }, 60, 450, 150, 75);
        Button scoreButton = new Button(ResourceLoader.loadImage(PathsToResources.SCORE_BUTTON.getPath()), scoreMenu.new ButtonListenerImpl(), 60, 375, 150, 75);
        Button restartButton = new Button(ResourceLoader.loadImage(PathsToResources.RESTART_BUTTON.getPath()), game.new ButtonListenerImpl(), 60, 300, 150, 75);
        Button returnButton = new Button(ResourceLoader.loadImage(PathsToResources.RETURN_BUTTON.getPath()), mainMenu.new ButtonListenerImpl(), Display.WIDTH_WINDOW - 125, Display.HEIGHT_WINDOW - 50, 125, 60);

        startButton.setName("Start");
        exitButtonMainMenu.setName("Exit");
        exitButtonGameOverMenu.setName("Exit");
        scoreButton.setName("Score");
        restartButton.setName("Restart");
        returnButton.setName("Return");

        KeyController keyController = new KeyController(display);
        keyController.addListener(mainCursor.new KeyControllerListenerImpl());
        keyController.addListener(scoreCursor.new KeyControllerListenerImpl());
        keyController.addListener(gameOverCursor.new KeyControllerListenerImpl());
        keyController.addListener(game.new KeyListenerImpl());

        mainMenu.addMenuItem(mainLogo);
        mainMenu.addMenuItem(startButton);
        mainMenu.addMenuItem(scoreButton);
        mainMenu.addMenuItem(exitButtonMainMenu);

        scoreMenu.addMenuItem(returnButton);

        gameOverMenu.addMenuItem(gameOverLogo);
        gameOverMenu.addMenuItem(restartButton);
        gameOverMenu.addMenuItem(scoreButton);
        gameOverMenu.addMenuItem(exitButtonGameOverMenu);

        mainCursor.addListener(startButton.new CursorListenerImpl());
        mainCursor.addListener(scoreButton.new CursorListenerImpl());
        mainCursor.addListener(exitButtonMainMenu.new CursorListenerImpl());

        scoreCursor.addListener(returnButton.new CursorListenerImpl());

        gameOverCursor.addListener(restartButton.new CursorListenerImpl());
        gameOverCursor.addListener(scoreButton.new CursorListenerImpl());
        gameOverCursor.addListener(exitButtonGameOverMenu.new CursorListenerImpl());

        mainMenu.addListener(scoreMenu);
        mainMenu.addListener(game.new DeactivateListenerImpl());
        mainMenu.addListener(gameOverMenu);

        scoreMenu.addListener(mainMenu);
        scoreMenu.addListener(game.new DeactivateListenerImpl());
        scoreMenu.addListener(gameOverMenu);

        game.addDeactivateListener(mainMenu);
        game.addDeactivateListener(scoreMenu);
        game.addDeactivateListener(gameOverMenu);
        game.addGameOverListener(gameOverMenu.new GameOverListenerImpl());

        gameOverMenu.addListener(game.new DeactivateListenerImpl());
        gameOverMenu.addListener(mainMenu);
        gameOverMenu.addListener(scoreMenu);

        initializeDisplay(display, game, keyController, mainMenu, scoreMenu, gameOverMenu);
    }

    private static void initializeDisplay(Display display, Game game, KeyController keyController, AbstractMenu... menus) {
        display.create(new GamePanel(game, menus), "Balls");
        display.addKeyController(keyController);
    }
}
