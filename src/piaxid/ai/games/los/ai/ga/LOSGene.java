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

interface LOSGene {

    char[] TERRESTRIAL_ACTIONS = { 'a', 'd', '-' };

    char[] AIR_ACTIONS = { 'q', 'e', ' ', '-' };

    static char RANDOM_T_ACTION() {
        char a = TERRESTRIAL_ACTIONS[(int) (Math.random() * TERRESTRIAL_ACTIONS.length)];
        if (a == 'a') {
            a = TERRESTRIAL_ACTIONS[(int) (Math.random() * TERRESTRIAL_ACTIONS.length)];
        }
        return a;
    }

    static char RANDOM_A_ACTION() {
        char a = AIR_ACTIONS[(int) (Math.random() * AIR_ACTIONS.length)];
        if (a == 'q' || a == ' ') {
            a = AIR_ACTIONS[(int) (Math.random() * AIR_ACTIONS.length)];
        }
        return a;
    }
    
    /*
    void setReasoning(int reasoning) {
        this.reasoning = reasoning;
        
    }
    
    char getSense(int distance) {
        final int steps = (int) (reasoning * evalSigmoid(distance));
        return ACTIONS[steps % ACTIONS.length];
        
    }
    
    private static double evalSigmoid(int value) {
        return -1 / (1 - Math.pow(Math.E, -(value - 100))) + 1;
        
    }*/
}
