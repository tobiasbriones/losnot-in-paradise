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

import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Bounds;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Rect;

/**
 * Controls the Los character. It has the character state and performs the
 * character's game logic for each game tick.
 *
 * @author Tobias Briones
 */
public final class LOSController {

    static final int LOS_ANIM_NORMAL = 0;
    static final int LOS_ANIM_WALK_1 = 1;
    static final int LOS_ANIM_WALK_2 = 2;
    static final int JUMP_UP = 10;
    static final int JUMP_UP_LEFT = 11;
    static final int JUMP_UP_RIGHT = 12;
    private static final int LOS_ANIM_CHANGE_MS = 100;
    private static final int LOS_WALKING_SPEED_PIXELS_S = 350;
    private static final int LOS_JUMPING_INITIAL_SPEED_X_PIXELS = 220;
    private static final int LOS_JUMPING_INITIAL_SPEED_Y_PIXELS = -650; // Towards up is negative
    private static final int INITIAL_LOS_LEFT = 200;
    private final int sceneWidth;
    private Rect losRect;
    private int groundTop;
    private int losAnimNumber;
    private long lastLOSAnim;
    private boolean isJumping;
    private int jumpSpeedX;
    private int jumpSpeedY;
    private boolean isFalling; // Used when los fell off an abyss

    public LOSController(int sceneWidth) {
        this.sceneWidth = sceneWidth;
        reset();
    }

    private boolean isAnimChangeNeeded() {
        return System.currentTimeMillis() - lastLOSAnim >= LOS_ANIM_CHANGE_MS;
    }

    int getLOSAnim() {
        return losAnimNumber;
    }

    boolean isJumping() {
        return isJumping;
    }

    boolean isFalling() {
        return isFalling;
    }

    public void reset() {
        this.losRect = null;
        this.groundTop = -1;
        this.losAnimNumber = LOS_ANIM_NORMAL;
        this.lastLOSAnim = -1;
        this.isJumping = false;
        this.jumpSpeedX = 0;
        this.jumpSpeedY = 0;
        this.isFalling = false;
    }

    public void initController(Bounds losBounds, int groundTop) {
        this.losRect = losBounds.getRect();
        this.groundTop = groundTop;
        this.lastLOSAnim = System.currentTimeMillis();
        this.losAnimNumber = LOS_ANIM_NORMAL;
        this.jumpSpeedX = 0;
        this.jumpSpeedY = 0;

        putOnGround(INITIAL_LOS_LEFT);
    }

    void walkBack(long tickMS) {
        int tx = (int) (-LOS_WALKING_SPEED_PIXELS_S * (tickMS / 1000.0F));

        if (tx == 0) {
            tx = -1;
        }
        losRect.translateX(tx);
        checkForAnimChange();
    }

    void walkForward(long tickMS) {
        int tx = (int) (LOS_WALKING_SPEED_PIXELS_S * (tickMS / 1000.0F));

        if (tx == 0) {
            tx = 1;
        }
        losRect.translateX(tx);
        checkForAnimChange();
    }

    void jump(int direction) {
        if (isJumping()) {
            return;
        }
        switch (direction) {
            case JUMP_UP:
                isJumping = true;
                jumpSpeedX = 0;
                jumpSpeedY = LOS_JUMPING_INITIAL_SPEED_Y_PIXELS;
                break;

            case JUMP_UP_LEFT:
                isJumping = true;
                jumpSpeedX = -LOS_JUMPING_INITIAL_SPEED_X_PIXELS;
                jumpSpeedY = LOS_JUMPING_INITIAL_SPEED_Y_PIXELS;
                break;

            case JUMP_UP_RIGHT:
                isJumping = true;
                jumpSpeedX = LOS_JUMPING_INITIAL_SPEED_X_PIXELS;
                jumpSpeedY = LOS_JUMPING_INITIAL_SPEED_Y_PIXELS;
                break;
        }
    }

    void fall() {
        isFalling = true;
    }

    void now(long tickMS, int worldSpeed) {
        losRect.translateX(worldSpeed);
        if (isFalling) {
            // Game lost
            losRect.translateY(5);
            return;
        }
        if (isJumping()) {
            int tx = (int) (jumpSpeedX * (tickMS / 1000.0F));
            int ty = (int) (jumpSpeedY * (tickMS / 1000.0F));

            if (tx == 0 && jumpSpeedX != 0) {
                tx = jumpSpeedX / Math.abs(jumpSpeedX);
            }
            if (ty == 0 && jumpSpeedY != 0) {
                ty = jumpSpeedY / Math.abs(jumpSpeedY);
            }
            losRect.translateX(tx);
            losRect.translateY(ty);

            // Speed X is constant
            int speedYChange = (int) ((LOS_JUMPING_INITIAL_SPEED_Y_PIXELS) * (tickMS / 333.0F));

            if (speedYChange == 0) {
                speedYChange = -1;
            }
            jumpSpeedY -= speedYChange;

            if (fixVertical()) {
                // end of jump
                jumpSpeedX = 0;
                isJumping = false;
            }
        }
        fixHorizontal();
    }

    private void putOnGround(int left) {
        losRect.set(groundTop - losRect.getHeight() - 2, left, losRect.getWidth(), losRect.getHeight());
    }

    private void putOnGround() {
        putOnGround(losRect.getLeft());
    }

    private void fixHorizontal() {
        if (losRect.getLeft() < 0) {
            losRect.set(losRect.getTop(), 0, losRect.getWidth(), losRect.getHeight());
        }
        else if (losRect.getRight() + 30 > sceneWidth) {
            losRect
                .set(losRect.getTop(), sceneWidth - losRect.getWidth() - 30, losRect.getWidth(), losRect.getHeight());
        }
    }

    private boolean fixVertical() {
        if (losRect.getBottom() > groundTop) {
            putOnGround();
            return true;
        }
        return false;
    }

    private void switchLOSAnim() {
        switch (losAnimNumber) {
            case LOS_ANIM_NORMAL:
                losAnimNumber = LOS_ANIM_WALK_1;
                break;

            case LOS_ANIM_WALK_1:
                losAnimNumber = LOS_ANIM_WALK_2;
                break;

            case LOS_ANIM_WALK_2:
                losAnimNumber = LOS_ANIM_NORMAL;
                break;
        }
    }

    private void checkForAnimChange() {
        if (isAnimChangeNeeded()) {
            switchLOSAnim();
            lastLOSAnim = System.currentTimeMillis();
        }
    }

}
