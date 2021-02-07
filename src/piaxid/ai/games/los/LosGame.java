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

package piaxid.ai.games.los;

import engineer.tobiasbriones.gencesk_2d_prototype_2018.Game;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.Scene;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.config.GameConfig;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.io.KeyEventHandler;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Dimension2D;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.view.RenderView;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.view.RenderViewTickCallback;

import javax.swing.*;

/**
 * Implements the game by setting up the game's config, scene and render view.
 *
 * @author Tobias Briones
 */
public final class LosGame extends Game {

    private static final int GAME_FPS = 60;
    public static final Dimension2D gameSize = new Dimension2D(960, 540);
    private final GameConfig gameConfig;
    private KeyEventHandler keyHandler;
    private Scene currentScene;

    public LosGame() {
        super();
        this.gameConfig = new GameConfig();
        this.keyHandler = null;
        this.currentScene = null;

        gameConfig.setResolution(gameSize.getWidth(), gameSize.getHeight());
        gameConfig.setFps(GAME_FPS);
    }

    KeyEventHandler getKeyHandler() {
        return keyHandler;
    }

    @Override
    protected GameConfig getGameConfig() {
        return gameConfig;
    }

    @Override
    protected Scene getCurrentScene() {
        return currentScene;
    }

    @Override
    protected RenderView onCreateRenderView(RenderViewTickCallback renderViewTickCallback) {
        final var renderView = new RenderView(renderViewTickCallback, gameConfig);
        keyHandler = new KeyEventHandler(renderView);
        return renderView;
    }

    @Override
    protected void onPrepare() {
        try {
            currentScene = new MainScene(this);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Fail to load resources. " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
