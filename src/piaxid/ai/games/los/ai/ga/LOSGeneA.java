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

final class LOSGeneA implements LOSGene {

    private final int id;
    private char terrestrialAction;
    private char airAction;

    LOSGeneA(int id) {
        this.id = id;
        this.terrestrialAction = LOSGene.RANDOM_T_ACTION();
        this.airAction = LOSGene.RANDOM_A_ACTION();
    }

    int getId() {
        return id;
    }

    char getTerrestrialAction() {
        return terrestrialAction;
    }

    void setTerrestrialAction(char action) {
        this.terrestrialAction = action;
    }

    char getAirAction() {
        return airAction;
    }

    void setAirAction(char action) {
        this.airAction = action;
    }

    @Override
    public String toString() {
        return "(" + terrestrialAction + ", " + airAction + ")";
    }

}
