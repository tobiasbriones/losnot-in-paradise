/*
 * Copyright (c) 2018-2021 Tobias Briones. All rights reserved.
 *
 * This file is part of Losnot in Paradise.
 */

package piaxid.ai.games.los;

import engineer.tobiasbriones.gencesk_2d_prototype_2018.graphics.Bitmap;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Rect;

final class WinAnimation {

    private final MainScene ms;
    private final Rect rect;
    private final Rect losRect;
    private int speed;
    private boolean isRunning;
    private int spacecraftTx;
    private int totalTx;

    WinAnimation(MainScene ms, Bitmap spacecraft, Rect losRect) {
        this.ms = ms;
        this.rect = spacecraft.getRect();
        this.losRect = losRect;
        this.speed = 200;
        this.spacecraftTx = -spacecraft.getWidth();
        this.totalTx = 0;
    }

    boolean isRunning() {
        return isRunning;
    }

    void init(int w) {
        rect.set(losRect.getBottom() - rect.getHeight() + 2, w - 16, rect.getWidth(), rect.getHeight());
    }

    void start(int speed) {
        this.speed = speed;
        this.isRunning = true;
    }

    void end() {
        speed = 200;
        isRunning = false;
    }

    void now(long tickMS) {
        if (!isRunning) {
            return;
        }
        final int tx = (int) (speed * (tickMS / 1000.0F)) * -2;
        totalTx += tx;

        if (spacecraftTx < 0) {
            rect.translateX(tx / -2);
            spacecraftTx -= tx / -2;
        }
        losRect.translateX(tx);
        if (totalTx >= 1600) {
            ms.won();
        }
    }

}