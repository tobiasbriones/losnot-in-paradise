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
final class LosController {

    static final int LOS_ANIM_NORMAL = 0;
    static final int LOS_ANIM_WALK_1 = 1;
    static final int LOS_ANIM_WALK_2 = 2;
    static final int JUMP_UP = 10;
    static final int JUMP_UP_LEFT = 11;
    static final int JUMP_UP_RIGHT = 12;
    private static final int LOS_ANIM_CHANGE_MS = 100;
    private static final int LOS_WALKING_SPEED_PX_S = 350;
    private static final int LOS_JUMPING_INITIAL_SPEED_X_PX = 220;
    private static final int LOS_JUMPING_INITIAL_SPEED_Y_PX = -650; // Towards up is negative
    private static final int ESTIMATED_LOS_WIDTH_PADDING_PX = 30;
    private static final int INITIAL_LOS_LEFT_PX = 200;
    private final int sceneWidth;
    private Rect losRect;
    private int groundTop;
    private int losAnimNumber;
    private long lastLosAnim;
    private boolean isJumping;
    private int jumpSpeedX;
    private int jumpSpeedY;
    private boolean isFalling; // Used when Los fell off an abyss

    LosController(int sceneWidth) {
        this.sceneWidth = sceneWidth;
        reset();
    }

    int getLosAnim() {
        return losAnimNumber;
    }

    boolean isJumping() {
        return isJumping;
    }

    boolean isFalling() {
        return isFalling;
    }

    void initController(Bounds losBounds, int initialGroundTop) {
        this.losRect = losBounds.getRect();
        this.groundTop = initialGroundTop;
        this.lastLosAnim = System.currentTimeMillis();
        this.losAnimNumber = LOS_ANIM_NORMAL;
        this.jumpSpeedX = 0;
        this.jumpSpeedY = 0;

        placeOnGround(INITIAL_LOS_LEFT_PX);
    }

    void now(long tickMs, int worldSpeed) {
        losRect.translateX(worldSpeed);

        if (isFalling) {
            onUpdateWhenLost();
        }
        else {
            onUpdateWhenAlive(tickMs);
        }
    }

    void walkForward(long tickMs) {
        int tx = (int) (LOS_WALKING_SPEED_PX_S * (tickMs / 1000.0F));

        if (tx == 0) {
            tx = 1;
        }
        losRect.translateX(tx);
        onRectChanged();
    }

    void walkBack(long tickMs) {
        int tx = (int) (-LOS_WALKING_SPEED_PX_S * (tickMs / 1000.0F));

        if (tx == 0) {
            tx = -1;
        }
        losRect.translateX(tx);
        onRectChanged();
    }

    void jump(int direction) {
        if (isJumping()) {
            return;
        }
        switch (direction) {
            case JUMP_UP:
                isJumping = true;
                jumpSpeedX = 0;
                jumpSpeedY = LOS_JUMPING_INITIAL_SPEED_Y_PX;
                break;

            case JUMP_UP_LEFT:
                isJumping = true;
                jumpSpeedX = -LOS_JUMPING_INITIAL_SPEED_X_PX;
                jumpSpeedY = LOS_JUMPING_INITIAL_SPEED_Y_PX;
                break;

            case JUMP_UP_RIGHT:
                isJumping = true;
                jumpSpeedX = LOS_JUMPING_INITIAL_SPEED_X_PX;
                jumpSpeedY = LOS_JUMPING_INITIAL_SPEED_Y_PX;
                break;
        }
    }

    void fall() {
        isFalling = true;
    }

    void reset() {
        this.losRect = null;
        this.groundTop = -1;
        this.losAnimNumber = LOS_ANIM_NORMAL;
        this.lastLosAnim = -1L;
        this.isJumping = false;
        this.jumpSpeedX = 0;
        this.jumpSpeedY = 0;
        this.isFalling = false;
    }

    private void onUpdateWhenAlive(long tickMs) {
        if (isJumping()) {
            int tx = (int) (jumpSpeedX * (tickMs / 1000.0F));
            int ty = (int) (jumpSpeedY * (tickMs / 1000.0F));

            if (tx == 0 && jumpSpeedX != 0) {
                tx = jumpSpeedX / Math.abs(jumpSpeedX);
            }
            if (ty == 0 && jumpSpeedY != 0) {
                ty = jumpSpeedY / Math.abs(jumpSpeedY);
            }
            losRect.translateX(tx);
            losRect.translateY(ty);

            // Speed X is constant
            int speedYChange = (int) ((LOS_JUMPING_INITIAL_SPEED_Y_PX) * (tickMs / 333.0F));

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

    private void onUpdateWhenLost() {
        losRect.translateY(5);
    }

    private void onRectChanged() {
        if (checkForAnimChange()) {
            switchLosAnim();
            lastLosAnim = System.currentTimeMillis();
        }
    }

    private boolean fixVertical() {
        if (losRect.getBottom() > groundTop) {
            placeOnGround();
            return true;
        }
        return false;
    }

    private void fixHorizontal() {
        if (losRect.getLeft() < 0) {
            losRect.set(losRect.getTop(), 0, losRect.getWidth(), losRect.getHeight());
        }
        else if (losRect.getRight() + ESTIMATED_LOS_WIDTH_PADDING_PX > sceneWidth) {
            losRect.set(
                losRect.getTop(),
                sceneWidth - losRect.getWidth() - ESTIMATED_LOS_WIDTH_PADDING_PX,
                losRect.getWidth(),
                losRect.getHeight()
            );
        }
    }

    private void placeOnGround() {
        placeOnGround(losRect.getLeft());
    }

    private void placeOnGround(int left) {
        losRect.set(
            groundTop - losRect.getHeight() - 2,
            left,
            losRect.getWidth(),
            losRect.getHeight()
        );
    }

    private boolean checkForAnimChange() {
        return System.currentTimeMillis() - lastLosAnim >= LOS_ANIM_CHANGE_MS;
    }

    private void switchLosAnim() {
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

}
