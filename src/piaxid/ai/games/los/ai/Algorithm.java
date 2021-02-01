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

package piaxid.ai.games.los.ai;

import engineer.tobiasbriones.gencesk_2d_prototype_2018.graphics.Drawable;

import java.awt.*;

public abstract class Algorithm extends Drawable {

    private final int updateTimeMS;
    private final Font g2Font;
    private final Font g2FontSmall;
    private Thread playingThread;
    private long lastTimeMS;
    private volatile boolean isRunning;
    private volatile boolean isPaused;

    protected Algorithm(int updateTimeMS) {
        this.updateTimeMS = updateTimeMS;
        this.g2Font = new Font("Monospaced", Font.BOLD, 32);
        this.g2FontSmall = new Font("Monospaced", Font.BOLD, 18);
        this.playingThread = null;
        this.isRunning = false;
        this.isPaused = false;
        this.lastTimeMS = -1;
    }

    protected final Font getFont() {
        return g2Font;
    }

    protected final Font getSmallFont() {
        return g2FontSmall;
    }

    public final boolean isRunning() {
        return isRunning;
    }

    public final boolean isPlaying() {
        return isRunning && !isPaused;
    }

    public final void run() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        playingThread = new Thread(() -> {
            int timeMS = 0;
            lastTimeMS = System.currentTimeMillis();

            while (isRunning) {
                if (isPaused) {
                    continue;
                }

                update(timeMS);
                try {
                    Thread.sleep((timeMS < updateTimeMS) ? updateTimeMS - timeMS : 5);//fix
                }
                catch (InterruptedException ignore) {
                }
                timeMS = (int) (System.currentTimeMillis() - lastTimeMS);
                lastTimeMS = System.currentTimeMillis();
            }
        });

        playingThread.start();
    }

    public void resume() {
        isPaused = false;
    }

    public void pause() {
        isPaused = true;
    }

    public void stop() {
        isRunning = false;

        try {
            playingThread.join();
        }
        catch (InterruptedException ignore) {
        }
        playingThread = null;
        isPaused = false;
    }

    protected abstract void update(long tickMS);

}
