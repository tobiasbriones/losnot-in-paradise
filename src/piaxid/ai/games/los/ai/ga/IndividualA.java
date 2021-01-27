/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los.ai.ga;

import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Bounds;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Dimension2D;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Rect;
import piaxid.ai.games.los.LOSController;
import piaxid.ai.games.los.MainScene;
import piaxid.ai.games.los.ai.InputHandler;

final class IndividualA implements Individual {

    static final int STEP_TX = 30;
    int fitGeneId; // gene position in which has been passed until it
    private final LOSGeneA[] genes;
    private final LOSController losController;
    private final Bounds losBounds;
    private final InputHandler input;
    private int tx;
    private boolean hasLost;
    private boolean isVisible;
    private int scoreRank;

    IndividualA(int sceneWidth, Dimension2D losSize) {
        this.genes = new LOSGeneA[MainScene.SCENE_LENGTH_TX / STEP_TX];
        this.losController = new LOSController(sceneWidth);
        this.losBounds = new Bounds();
        this.input = new InputHandler();
        this.tx = 0;
        this.hasLost = false;
        this.isVisible = true;
        this.scoreRank = -1;

        getRect().setWidth(losSize.getWidth());
        getRect().setHeight(losSize.getHeight());
        for (int i = 0; i < genes.length; i++) {
            genes[i] = new LOSGeneA(i);
        }
    }

    LOSGeneA[] getGenes() {
        return genes;
    }

    @Override
    public LOSController getLosController() {
        return losController;
    }

    @Override
    public Rect getRect() {
        return losBounds.getRect();
    }

    @Override
    public InputHandler getInput() {
        return input;
    }

    @Override
    public int getTx() {
        return tx;
    }

    @Override
    public void setTx(int tx) {
        this.tx = tx;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public int getScoreRank() {
        return scoreRank;
    }

    @Override
    public void setLost() {
        hasLost = true;
    }

    @Override
    public void setNotVisible() {
        isVisible = false;
    }

    @Override
    public boolean hasLost() {
        return hasLost;
    }

    @Override
    public void init(int groundTop) {
        reset();
        losController.initController(losBounds, groundTop);
    }

    @Override
    public void reset() {
        tx = 0;
        hasLost = false;
        isVisible = true;
        scoreRank = -1;

        losController.reset();
    }

    @Override
    public int eval() {
        return (int) (((float) tx / (float) MainScene.SCENE_LENGTH_TX) * 100);
    }

    void setScoreRank(int scoreRank) {
        this.scoreRank = scoreRank;
    }

}
