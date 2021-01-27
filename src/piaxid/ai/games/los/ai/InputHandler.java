/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los.ai;

import java.util.*;

public final class InputHandler {

    private final Set<Character> inputs;

    public InputHandler() {
        this.inputs = Collections.synchronizedSet(new LinkedHashSet<>());
    }

    public void putInput(char input) {
        synchronized (inputs) {
            inputs.add(input);
        }
    }

    public List<Character> retrieveEvents() {
        synchronized (inputs) {
            final List<Character> inputsList = new ArrayList<>(inputs);

            inputs.clear();
            return inputsList;
        }
    }

}
