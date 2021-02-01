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

import piaxid.ai.games.los.Obstacle;

public final class LOSObstacle {

    final Obstacle.Object obstacle;
    final int distance;

    public LOSObstacle(Obstacle.Object obstacle, int distance) {
        this.obstacle = obstacle;
        this.distance = distance;
    }

}
