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
