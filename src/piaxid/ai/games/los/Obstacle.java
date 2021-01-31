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

package piaxid.ai.games.los;

import engineer.tobiasbriones.gencesk_2d_prototype_2018.graphics.Bitmap;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Rect;

final class Obstacle {

    private static final int DEF_ANIMATION_CHANGE_TIME_MS = 200;

    enum Object { ABYSS1, ABYSS2, TREE1, TREE2, BIRD, LAVA }

    private final int speedX;
    private final int speedY;
    private final Object object;
    private final Bitmap[] animations;
    private final Rect rect;
    private int i;
    private int animationTimeMS;
    private boolean sinVertical;
    private float sinValue;
    private int animationChangeTimeMS;

    Obstacle(int speedX, int speedY, Object object, Bitmap... animations) {
        this.speedX = speedX;
        this.speedY = speedY;
        this.object = object;
        this.animations = animations;
        this.rect = new Rect(0, 0, animations[0].getWidth(), animations[0].getHeight());
        this.i = 0;
        this.animationTimeMS = 0;
        this.sinVertical = false;
        this.sinValue = 0;
        this.animationTimeMS = DEF_ANIMATION_CHANGE_TIME_MS;
    }

    Obstacle(Object object, Bitmap... animations) {
        this(0, 0, object, animations);
    }

    Object getObject() {
        return object;
    }

    Bitmap getAnimation() {
        final Bitmap anim = animations[i];

        anim.getRect().set(rect.getTop(), rect.getLeft(), anim.getWidth(), anim.getHeight());
        return animations[i];
    }

    int getWidth() {
        return animations[0].getWidth();
    }

    int getHeight() {
        return animations[0].getHeight();
    }

    int getAnimationNumber() {
        return i;
    }

    Rect getRect() {
        return rect;
    }

    void setAnimationChangeTime(int timeMS) {
        this.animationChangeTimeMS = timeMS;
    }

    void setPosition(int top, int left) {
        rect.set(top, left, animations[0].getWidth(), animations[0].getHeight());
    }

    void setSinVertical() {
        this.sinVertical = true;
    }

    void now(long tickMS) {
        int tx = (int) (speedX * (tickMS / 1000.0F));
        int ty = (int) (speedY * (tickMS / 1000.0F));
        animationTimeMS += tickMS;

        if (tx == 0 && speedX != 0) {
            tx = speedX / Math.abs(speedX);
        }
        if (ty == 0 && speedY != 0) {
            ty = speedY / Math.abs(speedY);
        }
        if (sinVertical) {
            sinValue += Math.PI / 32;

            ty = (int) (Math.sin(sinValue) * speedY);
        }
        if (animationTimeMS >= animationChangeTimeMS) {
            i++;
            animationTimeMS = 0;
        }
        if (i >= animations.length) {
            i = 0;
        }
        rect.translate(tx, ty);
    }

}
