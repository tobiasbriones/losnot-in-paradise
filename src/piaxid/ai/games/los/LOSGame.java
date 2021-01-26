/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
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
public final class LOSGame extends Game {

    public static final Dimension2D GAME_SIZE = new Dimension2D(960, 540);
    private final GameConfig gameConfig;
    private KeyEventHandler keyHandler;
    private Scene currentScene;

    public LOSGame() {
        this.gameConfig = new GameConfig();
        this.keyHandler = null;
        this.currentScene = null;

        gameConfig.setResolution(GAME_SIZE.getWidth(), GAME_SIZE.getHeight());
        //gameConfig.setFps(20);
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
        final RenderView renderView = new RenderView(renderViewTickCallback, gameConfig);
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
