/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los;

import piaxid.ai.games.los.ui.MainWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final MainWindow mw = new MainWindow();
            final LOSGame game = new LOSGame();

            game.run();
            game.displayOn(mw.getOutputContainer());
            mw.createUI();
        });
    }

}
