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

import engineer.tobiasbriones.gencesk_2d_prototype_2018.Scene;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.audio.Sound;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.graphics.Bitmap;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.io.Key;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.io.KeyEventHandler;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Dimension2D;
import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Rect;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Implements the game scene. It takes care of loading, updating and composing
 * the virtual frame buffer by using the Gencesk 2D library.
 *
 * @author Tobias Briones
 */
final class MainScene extends Scene {

    @SuppressWarnings("unused")
    public static final int SCENE_LENGTH_TX = 12600;
    private static final int WORLD_INITIAL_SPEED_X_PIXELS_PER_SECOND = -200;
    private final Dimension2D size;
    private final Bitmap background;
    private final Bitmap ground;
    private final Bitmap abyss1;
    private final Bitmap abyss2;
    private final Bitmap los;
    private final Bitmap losWalk1;
    private final Bitmap losWalk2;
    private final Bitmap tree1;
    private final Bitmap tree2;
    private final Bitmap birdFly1;
    private final Bitmap birdFly2;
    private final Bitmap birdFly3;
    private final Bitmap lava;
    private final Bitmap lavaFly1;
    private final Bitmap lavaFly2;
    private final Bitmap lavaFly3;
    private final Bitmap spacecraft;
    private final Bitmap lostScreen;
    private final Bitmap wonScreen;
    private final Sound jumpSound;
    private final Sound fallSound1;
    private final Sound fallSound2;
    private final Sound hurtSound1;
    private final Sound hurtSound2;
    private final Font g2Font;
    private final LinkedList<Integer> obstaclesPosition;
    private final LinkedList<Obstacle.Object> obstacles;
    private final LinkedList<Obstacle> visibleObstacles;
    private final KeyEventHandler keyHandler;
    private final LOSController losController;
    private final WinAnimation victory;
    private final int offY;
    private final boolean drawObjectFrame;
    private int cameraSpeed;
    private int cameraTotalTx;
    private int numberOfDrawnObstacles;
    private boolean updateCameraSpeed;
    private boolean gameWon;
    private int lastFPSCount;
    private long fpsCountLastUpdate;
    private volatile boolean gameLost;

    MainScene(LOSGame game) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        super(game);
        this.size = game.getGameConfig().getResolution();
        this.background = Bitmap.createBitmap(new File("assets/background.png"));
        this.ground = Bitmap.createBitmap(new File("assets/ground.png"));
        this.abyss1 = Bitmap.createBitmap(new File("assets/abyss_1.png"));
        this.abyss2 = Bitmap.createBitmap(new File("assets/abyss_2.png"));
        this.los = Bitmap.createBitmap(new File("assets/los.png"));
        this.losWalk1 = Bitmap.createBitmap(new File("assets/los_walk_1.png"));
        this.losWalk2 = Bitmap.createBitmap(new File("assets/los_walk_2.png"));
        this.tree1 = Bitmap.createBitmap(new File("assets/tree_1.png"));
        this.tree2 = Bitmap.createBitmap(new File("assets/tree_2.png"));
        this.birdFly1 = Bitmap.createBitmap(new File("assets/bird_fly_1.png"));
        this.birdFly2 = Bitmap.createBitmap(new File("assets/bird_fly_2.png"));
        this.birdFly3 = Bitmap.createBitmap(new File("assets/bird_fly_3.png"));
        this.lava = Bitmap.createBitmap(new File("assets/lava.png"));
        this.lavaFly1 = Bitmap.createBitmap(new File("assets/lava_1.png"));
        this.lavaFly2 = Bitmap.createBitmap(new File("assets/lava_2.png"));
        this.lavaFly3 = Bitmap.createBitmap(new File("assets/lava_3.png"));
        this.spacecraft = Bitmap.createBitmap(new File("assets/spacecraft.png"));
        this.lostScreen = Bitmap.createBitmap(new File("assets/game_lost.png"));
        this.wonScreen = Bitmap.createBitmap(new File("assets/game_won.png"));
        this.jumpSound = new Sound(new File("assets/jump.wav"));
        this.fallSound1 = new Sound(new File("assets/fall_1.wav"));
        this.fallSound2 = new Sound(new File("assets/fall_2.wav"));
        this.hurtSound1 = new Sound(new File("assets/hurt_1.wav"));
        this.hurtSound2 = new Sound(new File("assets/hurt_2.wav"));
        this.g2Font = new Font("Monospaced", Font.BOLD, 32);
        this.obstaclesPosition = new LinkedList<>();
        this.obstacles = new LinkedList<>();
        this.visibleObstacles = new LinkedList<>();
        this.keyHandler = game.getKeyHandler();
        this.losController = new LOSController(size.getWidth());
        this.victory = new WinAnimation(this, spacecraft, los.getRect());
        this.offY = 30;
        this.cameraSpeed = WORLD_INITIAL_SPEED_X_PIXELS_PER_SECOND;
        this.cameraTotalTx = 0;
        this.numberOfDrawnObstacles = 0;
        this.updateCameraSpeed = false;
        this.drawObjectFrame = false;
        this.gameLost = false;
        this.gameWon = false;
        this.lastFPSCount = 0;
        this.fpsCountLastUpdate = System.currentTimeMillis();

        setObstacles();
        setObjects();
    }

    @Override
    protected void update(long l) {
        if (gameLost || gameWon) {
            keyHandler.retrieve(key -> {
                if (key.getCharacter() == Key.KEY_ENTER) {
                    restart();
                }
            });
            return;
        }
        final Iterator<Obstacle> visibleObstaclesIterator = visibleObstacles.iterator();
        int tx = (int) (cameraSpeed * (l / 1000.0F)); // Camera speed
        cameraTotalTx += tx;

        if (tx == 0) {
            tx = -1;
        }

        // Update fps count
        if (System.currentTimeMillis() - fpsCountLastUpdate > 200) {
            lastFPSCount = (int) (1000.0F / l);
            fpsCountLastUpdate = System.currentTimeMillis();
        }

        // Retrieve key events
        keyHandler.retrieve(key -> {
            switch (key.getCharacter()) {
                case 'a':
                    losController.walkBack(l);
                    break;

                case 'd':
                    losController.walkForward(l);
                    break;

                case ' ':
                    if (!losController.isJumping() && !losController.isFalling()) {
                        jumpSound.play();
                    }
                    losController.jump(LOSController.JUMP_UP);
                    break;

                case 'q':
                    if (!losController.isJumping()) {
                        jumpSound.play();
                    }
                    losController.jump(LOSController.JUMP_UP_LEFT);
                    break;

                case 'e':
                    if (!losController.isJumping()) {
                        jumpSound.play();
                    }
                    losController.jump(LOSController.JUMP_UP_RIGHT);
                    break;
            }
        });
        losController.now(l, tx);

        // Handle obstacles
        while (visibleObstaclesIterator.hasNext()) {
            final Obstacle obstacle = visibleObstaclesIterator.next();

            // Object collide
            if (obstacle.getObject() == Obstacle.Object.ABYSS1 || obstacle.getObject() == Obstacle.Object.ABYSS2) {
                if (!losController.isJumping() && los.getRect().isAbove(obstacle.getRect())) {
                    losController.fall();
                    lost();
                    if (!fallSound1.isPlaying() && !fallSound2.isPlaying()) {
                        final Random random = new Random();

                        if (random.nextBoolean()) {
                            fallSound1.play();
                        }
                        else {
                            fallSound2.play();
                        }
                    }
                }
            }
            else if (los.getRect().overlapsRect(obstacle.getRect())) {
                lost();
                if (!hurtSound1.isPlaying() && !hurtSound2.isPlaying()) {
                    final Random random = new Random();

                    if (random.nextBoolean()) {
                        hurtSound1.play();
                    }
                    else {
                        hurtSound2.play();
                    }
                }
            }

            // Move camera
            obstacle.getRect().translateX(tx);

            // Update obstacle animation
            obstacle.now(l);

            // If obstacle is lava, manual position is needed
            if (obstacle.getObject() == Obstacle.Object.LAVA) {
                final int left = obstacle.getRect().getLeft();
                final int a = size.getHeight() - ground.getHeight() - offY + lava.getHeight();
                final Rect rect = obstacle.getRect();

                switch (obstacle.getAnimationNumber()) {
                    case 0:
                        rect.set(a - lava.getHeight(), left, lava.getWidth(), lava.getHeight());
                        break;

                    case 1:
                        rect.set(a - lavaFly1.getHeight(), left, lavaFly1.getWidth(), lavaFly1.getHeight());
                        break;

                    case 2:
                        rect.set(a - lavaFly2.getHeight(), left, lavaFly2.getWidth(), lavaFly2.getHeight());
                        break;

                    case 3:
                        rect.set(a - lavaFly3.getHeight(), left, lavaFly3.getWidth(), lavaFly3.getHeight());
                        break;
                }
            }

            // Check if it's gone
            if (obstacle.getRect().getRight() < 0) {
                visibleObstaclesIterator.remove();

                // Check if game is done
                if (!victory.isRunning() && obstaclesPosition.isEmpty()) {
                    victory.start(cameraSpeed);
                }
            }
        }

        // Check for adding new visible obstacles
        if (hasToDrawNextObstacle()) {
            final Obstacle nextObstacle = createObstacle(obstacles.pop());

            obstaclesPosition.pop();
            addObstacle(nextObstacle);
        }

        // If game has been won victory will update
        victory.now(l);

        // Update camera speed
        if (updateCameraSpeed) {
            cameraSpeed -= 20;
            updateCameraSpeed = false;
        }
    }

    @Override
    protected void composeFrameBuffer(Graphics2D graphics2D) {
        background.draw(graphics2D);
        ground.draw(graphics2D);

        // Obstacles
        for (Obstacle obstacle : visibleObstacles) {
            obstacle.getAnimation().draw(graphics2D);
            drawFrame(graphics2D, obstacle.getRect());
        }

        // Los
        switch (losController.getLOSAnim()) {
            case LOSController.LOS_ANIM_NORMAL:
                los.draw(graphics2D);
                break;

            case LOSController.LOS_ANIM_WALK_1:
                losWalk1.getRect().set(los.getRect().getTop(), los.getRect().getLeft());
                losWalk1.draw(graphics2D);
                break;

            case LOSController.LOS_ANIM_WALK_2:
                losWalk2.getRect().set(los.getRect().getTop(), los.getRect().getLeft());
                losWalk2.draw(graphics2D);
                break;
        }
        drawFrame(graphics2D, los.getRect());

        // Game lost/won
        if (gameLost) {
            lostScreen.draw(graphics2D);
        }
        else if (victory.isRunning()) {
            spacecraft.draw(graphics2D);

            if (gameWon) {
                wonScreen.draw(graphics2D);
            }
        }

        // FPS Count
        graphics2D.setColor(Color.YELLOW);
        graphics2D.setFont(g2Font);
        graphics2D.drawString(String.valueOf(lastFPSCount), 20, 40);
    }

    // Called from WinAnimation when won animation is done and then finishes the game
    void won() {
        gameWon = true;
    }

    private void setObstacles() {
        obstaclesPosition.add(-240);
        obstaclesPosition.add(-600);
        obstaclesPosition.add(-900);
        obstaclesPosition.add(-1360);
        obstaclesPosition.add(-1800);
        obstaclesPosition.add(-2220);
        obstaclesPosition.add(-2540);
        obstaclesPosition.add(-2900);
        obstaclesPosition.add(-3200);
        obstaclesPosition.add(-3600);
        obstaclesPosition.add(-3900);
        obstaclesPosition.add(-4240);
        obstaclesPosition.add(-4600);
        obstaclesPosition.add(-4900);
        obstaclesPosition.add(-5360);
        obstaclesPosition.add(-5800);
        obstaclesPosition.add(-6220);
        obstaclesPosition.add(-6540);
        obstaclesPosition.add(-6900);
        obstaclesPosition.add(-7400);
        obstaclesPosition.add(-7900);
        obstaclesPosition.add(-8300);
        obstaclesPosition.add(-8600);
        obstaclesPosition.add(-9000);
        obstaclesPosition.add(-9200);
        obstaclesPosition.add(-9300);
        obstaclesPosition.add(-9500);
        obstaclesPosition.add(-10000);
        obstaclesPosition.add(-10800);
        obstacles.add(Obstacle.Object.ABYSS1); // Initial obstacle
        obstacles.add(Obstacle.Object.TREE1);
        obstacles.add(Obstacle.Object.ABYSS2);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.LAVA);
        obstacles.add(Obstacle.Object.TREE2);
        obstacles.add(Obstacle.Object.TREE1);
        obstacles.add(Obstacle.Object.ABYSS2);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.TREE2);
        obstacles.add(Obstacle.Object.LAVA);
        obstacles.add(Obstacle.Object.TREE1);
        obstacles.add(Obstacle.Object.ABYSS1);
        obstacles.add(Obstacle.Object.TREE2);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.LAVA);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.ABYSS2);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.TREE2);
        obstacles.add(Obstacle.Object.LAVA);
        obstacles.add(Obstacle.Object.TREE2);
        obstacles.add(Obstacle.Object.TREE1);
        obstacles.add(Obstacle.Object.TREE1);
        obstacles.add(Obstacle.Object.LAVA);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.BIRD);
        obstacles.add(Obstacle.Object.LAVA);
        obstacles.add(Obstacle.Object.ABYSS2);
    }

    private void setObjects() {
        ground.getRect().set(0, 0, ground.getWidth(), ground.getHeight());
        ground.getRect().translateY(size.getHeight() - offY - ground.getHeight());
        losController.initController(los.getBounds(), ground.getRect().getTop());
        victory.init(size.getWidth());

        // Register initial visible obstacles
        addObstacle(createObstacle(obstacles.pop()), size.getWidth() - 350);
    }

    private Obstacle createObstacle(Obstacle.Object object) {
        switch (object) {
            case ABYSS1:
                return new Obstacle(Obstacle.Object.ABYSS1, abyss1);
            case ABYSS2:
                return new Obstacle(Obstacle.Object.ABYSS2, abyss2);
            case TREE1:
                return new Obstacle(Obstacle.Object.TREE1, tree1);
            case TREE2:
                return new Obstacle(Obstacle.Object.TREE2, tree2);
            case BIRD:
                return new Obstacle(-150, 5, Obstacle.Object.BIRD, birdFly1, birdFly2, birdFly3);
            case LAVA:
                return new Obstacle(Obstacle.Object.LAVA, lava, lavaFly1, lavaFly2, lavaFly3);
        }
        return new Obstacle(Obstacle.Object.TREE1, tree1);
    }

    private void addObstacle(Obstacle obstacle, int left) {
        final int height = obstacle.getHeight();
        final int top;

        switch (obstacle.getObject()) {
            case ABYSS1:
                top = size.getHeight() - height - offY;
                break;

            case ABYSS2:
                top = size.getHeight() - height - offY;
                break;

            case TREE1:
                top = size.getHeight() - height - ground.getHeight() - offY;
                break;

            case TREE2:
                top = size.getHeight() - height - ground.getHeight() - offY;
                break;

            case BIRD:
                top = size.getHeight() / 4 - offY;
                obstacle.setSinVertical();
                obstacle.setAnimationChangeTime(150);
                break;

            case LAVA:
                top = size.getHeight() - height - ground.getHeight() + 20 - offY;
                obstacle.setAnimationChangeTime(600);
                break;

            default:
                top = 0;
                break;
        }
        obstacle.setPosition(top, left);
        visibleObstacles.add(obstacle);
        numberOfDrawnObstacles++;

        if (numberOfDrawnObstacles % 4 == 0) {
            updateCameraSpeed = true;
        }
    }

    private void addObstacle(Obstacle obstacle) {
        addObstacle(obstacle, size.getWidth());
    }

    private boolean hasToDrawNextObstacle() {
        return !obstaclesPosition.isEmpty() && obstaclesPosition.peek() >= cameraTotalTx;
    }

    private void drawFrame(Graphics2D g2, Rect rect) {
        if (!drawObjectFrame) {
            return;
        }
        g2.drawLine(rect.getLeft(), rect.getTop(), rect.getLeft(), rect.getBottom());
        g2.drawLine(rect.getLeft(), rect.getTop(), rect.getRight(), rect.getTop());
        g2.drawLine(rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getBottom());
        g2.drawLine(rect.getRight(), rect.getTop(), rect.getRight(), rect.getBottom());
    }

    private void restart() {
        cameraSpeed = WORLD_INITIAL_SPEED_X_PIXELS_PER_SECOND;
        cameraTotalTx = 0;
        numberOfDrawnObstacles = 0;
        updateCameraSpeed = false;
        gameLost = false;
        gameWon = false;
        lastFPSCount = 0;
        fpsCountLastUpdate = System.currentTimeMillis();

        victory.end();
        visibleObstacles.clear();
        obstacles.clear();
        obstaclesPosition.clear();
        losController.reset();
        setObstacles();
        setObjects();
    }

    private void lost() {
        if (losController.isFalling()) {
            new Thread(() -> {
                try {
                    Thread.sleep(800);
                }
                catch (InterruptedException ignore) {
                }
                gameLost = true;
            }).start();
        }
        else {
            gameLost = true;
        }
    }

}
