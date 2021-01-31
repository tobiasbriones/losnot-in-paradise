/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * This file is part of Losnot in Paradise.
 *
 * This source code is licensed under the BSD-3-Clause License found in the
 * LICENSE file in the root directory of this source tree or at
 * https://opensource.org/licenses/BSD-3-Clause.
 */

package piaxid.ai.games.los.ui;

import piaxid.ai.games.los.LOSGame;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public final class MainWindow extends JFrame {

    private static final String ICON_PATH = new File("assets/icon.png").getPath();

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
        setIconImage(Toolkit.getDefaultToolkit().getImage(ICON_PATH));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
