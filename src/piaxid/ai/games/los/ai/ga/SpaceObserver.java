/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los.ai.ga;

public interface SpaceObserver {

    int getCurrentTx();

    LOSObstacle[] getCurrentObstacles();

}
