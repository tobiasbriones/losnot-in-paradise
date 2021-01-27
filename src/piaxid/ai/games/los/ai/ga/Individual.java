/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los.ai.ga;

import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Rect;
import piaxid.ai.games.los.LOSController;
import piaxid.ai.games.los.ai.InputHandler;

public interface Individual {

    LOSController getLosController();

    Rect getRect();

    InputHandler getInput();

    int getTx();

    void setTx(int tx);

    boolean isVisible();

    int getScoreRank();

    void setLost();

    void setNotVisible();

    boolean hasLost();

    void init(int groundTop);

    void reset();

    int eval();

}
