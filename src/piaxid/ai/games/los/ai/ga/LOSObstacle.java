/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los.ai.ga;

import piaxid.ai.games.los.Obstacle;

public final class LOSObstacle {

    final Obstacle.Object obstacle;
    final int distance;

    public LOSObstacle(Obstacle.Object obstacle, int distance) {
        this.obstacle = obstacle;
        this.distance = distance;
    }

}
