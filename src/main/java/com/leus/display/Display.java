package com.leus.display;

import com.leus.game.Game;
import com.leus.game.service.scores.ScoreManager;
import com.leus.utils.SettingsInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

public final class Display {

    public static final int WIDTH_WINDOW_IN_TILES = Integer.valueOf(SettingsInitializer.getPropertyValue("WidthWindowInTile"));
    public static final int HEIGHT_WINDOW_IN_TILES = Integer.valueOf(SettingsInitializer.getPropertyValue("HeightWindowInTile"));
    public static final int WIDTH_WINDOW = Game.TILE_WIDTH * WIDTH_WINDOW_IN_TILES;
    public static final int HEIGHT_GAME_SPACE_WINDOW = Game.TILE_HEIGHT * HEIGHT_WINDOW_IN_TILES;
    public static final int HEIGHT_WINDOW = HEIGHT_GAME_SPACE_WINDOW + Game.TILE_HEIGHT + ScoreManager.getSizeFrameForScore();

    private JPanel canvas;
    private JFrame window;
    private boolean isCreated;

    public void addKeyController(KeyAdapter keyController) {
        window.addKeyListener(keyController);
    }

    public void addMouseController(MouseAdapter mouseController) {
        window.addMouseListener(mouseController);
    }

    public void repaint() {
        if (!isCreated) {
            throw new IllegalStateException("Window has not been created!");
        }

        canvas.repaint();
    }

    public void create(JPanel canvas, String title) {

        if (isCreated) {
            throw new IllegalStateException("Window was created!");
        }

        this.window = new JFrame(title);
        this.canvas = canvas;

        canvas.setPreferredSize(new Dimension(WIDTH_WINDOW, HEIGHT_WINDOW));
        canvas.setLayout(null);
        canvas.setBackground(Color.white);

        window.getContentPane().add(canvas);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setSize(window.getPreferredSize());
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        isCreated = true;
    }

    public void destroy() {
        if (isCreated) {
            isCreated = false;
            window.dispose();
        } else {
            throw new IllegalStateException("Window has not been created!");
        }
    }
}
