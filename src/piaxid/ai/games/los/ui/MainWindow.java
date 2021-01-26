/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los.ui;

import piaxid.ai.games.los.LOSGame;

import javax.swing.*;
import java.awt.*;

public final class MainWindow extends JFrame {

    public MainWindow() {
        super("PIAXID AI GAMES - LOS Losnot in paradise");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public Container getOutputContainer() {
        return getContentPane();
    }

    public void createUI() {
        setPreferredSize(new Dimension(LOSGame.GAME_SIZE.getWidth(), LOSGame.GAME_SIZE.getHeight()));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
