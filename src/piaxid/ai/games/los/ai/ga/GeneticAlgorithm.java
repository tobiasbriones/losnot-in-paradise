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

import engineer.tobiasbriones.gencesk_2d_prototype_2018.models.Dimension2D;
import piaxid.ai.games.los.ai.Algorithm;

import java.awt.*;
import java.util.Random;

public final class GeneticAlgorithm extends Algorithm {

    public static final int POPULATION_SIZE = 15;
    private static final Random RANDOM = new Random();
    private static final int MUTATION_CHANCE_PERCENTAGE = 45;
    private static final int UPDATE_TIME_MS = 10;
    private final Dimension2D size;
    private final SpaceObserver spaceObserver;
    private final IndividualA[] population;
    private IndividualA mostAdapted;
    private IndividualA secondMostAdapted;
    private int generation;
    private int lostPopulation;
    private int notVisiblePopulation;
    private int bestScore;
    private int geneIdLock;
    private int geneLockRadius;

    public GeneticAlgorithm(Dimension2D size, Dimension2D losSize, SpaceObserver spaceObserver) {
        super(UPDATE_TIME_MS);
        this.size = size;
        this.spaceObserver = spaceObserver;
        this.population = new IndividualA[POPULATION_SIZE];
        this.mostAdapted = null;
        this.secondMostAdapted = null;
        this.generation = 0;
        this.lostPopulation = 0;
        this.notVisiblePopulation = 0;
        this.bestScore = 0;

        for (int i = 0; i < population.length; i++) {
            population[i] = new IndividualA(size.getWidth(), losSize);
        }
    }

    public Individual[] getPopulation() {
        return population;
    }

    public int getLostPopulationCount() {
        return lostPopulation;
    }

    public int getNotVisiblePopulation() {
        return notVisiblePopulation;
    }

    private IndividualA getOffspring() {
        if (mostAdapted.eval() >= secondMostAdapted.eval()) {
            return mostAdapted;
        }
        return secondMostAdapted;
    }

    private IndividualA getLeastAdapted() {
        IndividualA leastAdapted = population[0];
        int leastAdaptedFitValue = leastAdapted.eval();

        for (IndividualA individual : population) {
            if (individual.eval() < leastAdaptedFitValue) {
                leastAdapted = individual;
                leastAdaptedFitValue = leastAdapted.eval();
            }
        }
        return leastAdapted;
    }

    private int getBestScore() {
        if (bestScore == 0) {
            return spaceObserver.getCurrentTx();
        }
        return bestScore;
    }

    private int getCurrentGeneId() {
        return spaceObserver.getCurrentTx() / IndividualA.STEP_TX;
    }

    private boolean isPastLockPoint() {
        return spaceObserver.getCurrentTx() > geneIdLock * IndividualA.STEP_TX;
    }

    public void setLost(Individual individual) {
        individual.setLost();
        lostPopulation++;
    }

    public void setNotVisible(Individual individual) {
        individual.setNotVisible();
        notVisiblePopulation++;
    }

    @Override
    protected void update(long tickMS) {
        //System.out.println(spaceObserver.getCurrentTx() +" . "+spaceObserver.getCurrentObstacles().length);
        //System.out.println(tickMS);
        final int geneId = getCurrentGeneId();
        LOSGeneA[] currentIndividualGenes;

        for (IndividualA individual : population) {
            if (individual.hasLost()) {
                continue;
            }
            currentIndividualGenes = individual.getGenes();

            individual.fitGeneId = geneId;
            individual.getInput().putInput(currentIndividualGenes[geneId].getTerrestrialAction());
            individual.getInput().putInput(currentIndividualGenes[geneId].getAirAction());
        }
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        final boolean areGenesLocked = areGenesLocked();
        final boolean pastLockPoint = isPastLockPoint();
        final int x = size.getWidth() - 200;

        graphics2D.setFont(getFont());
        graphics2D.drawString("Gen " + generation, x, 40);
        graphics2D.setFont(getSmallFont());
        graphics2D.drawString("Best TX " + getBestScore(), x, 110);
        graphics2D.drawString("Lock radius " + geneLockRadius, x, 150);
        graphics2D.setColor((areGenesLocked) ? Color.decode("#e53935") : Color.YELLOW);
        graphics2D.drawString(((areGenesLocked) ? "Locked genes" : "Unlocked genes"), x, 190);
        graphics2D.setColor((!pastLockPoint) ? Color.decode("#e53935") : Color.YELLOW);
        graphics2D.drawString(((pastLockPoint) ? "Past lock" : "Before lock"), x, 230);
        graphics2D.setColor(Color.YELLOW);
    }

    public void nextGeneration(int groundTop) {
        if (isPlaying()) {
            throw new IllegalStateException("Illegal algorithm state: playing");
        }
        // GA
        select();
        lockGenes();
        crossover();
        if (RANDOM.nextInt(100) > 100 - MUTATION_CHANCE_PERCENTAGE) {
            mutate();
        }
        setMostAdaptedOffspring();
        bestScore = (mostAdapted.getTx() > bestScore) ? mostAdapted.getTx() : bestScore;

        // Prepare next generation
        lostPopulation = 0;
        notVisiblePopulation = 0;
        generation++;

        for (IndividualA individual : population) {
            individual.init(groundTop);
        }
        mostAdapted.setScoreRank(1);
        secondMostAdapted.setScoreRank(2);
        /*
        System.out.println("Genes generation " + generation);
        for(IndividualA individual : population) {
            for(LOSGeneA g : individual.getGenes()) {
                System.out.print(g+" ");
            }
            System.out.println();
        }
        System.out.println("_________________________________________________________________________________");
        System.out.println();
        */
    }

    private void select() {
        IndividualA best = population[0];
        IndividualA secondBest = population[0];
        int bestFitValue = best.eval();
        int secondBestFitValue = secondBest.eval();
        int currentFitValue;

        for (IndividualA individual : population) {
            currentFitValue = individual.eval();

            if (currentFitValue > bestFitValue) {
                best = individual;
                bestFitValue = currentFitValue;
            }
            else if (currentFitValue > secondBestFitValue) {
                secondBest = individual;
                secondBestFitValue = currentFitValue;
            }
        }
        this.mostAdapted = best;
        this.secondMostAdapted = secondBest;

        //System.out.println("Most adapted fit value: " + mostAdapted.eval());
    }

    private void lockGenes() {
        geneIdLock = secondMostAdapted.fitGeneId;
        geneLockRadius = RANDOM.nextInt(80);

        if (geneLockRadius > geneIdLock) {
            geneLockRadius = geneIdLock;
        }
        for (IndividualA individual : population) {
            final IndividualA copyFrom = (RANDOM.nextBoolean()) ? mostAdapted : secondMostAdapted;

            for (int i = 0; i < geneIdLock - geneLockRadius; i++) {
                individual.getGenes()[i].setTerrestrialAction(copyFrom.getGenes()[i].getTerrestrialAction());
                individual.getGenes()[i].setAirAction(copyFrom.getGenes()[i].getAirAction());
            }
        }
        System.out.println("Locking genes at: " + geneIdLock + ", radius " + geneLockRadius);
    }

    private void crossover() {
        //int crossoverRadius = -15+RANDOM.nextInt(30);
        //int geneLow = secondMostAdapted.fitGeneId - crossoverRadius;
        //int geneHigh = mostAdapted.fitGeneId + crossoverRadius;
        final int crossoverRadius = geneLockRadius;
        int geneLow = geneIdLock - crossoverRadius;
        int geneHigh = mostAdapted.fitGeneId + crossoverRadius;

        if (geneLow < 0) {
            geneLow = 0;
        }
        if (geneHigh > mostAdapted.getGenes().length) {
            geneHigh = mostAdapted.getGenes().length;
        }
        for (int i = geneLow; i < geneHigh; i++) {
            if (RANDOM.nextBoolean()) {
                continue;
            }
            final char tmp1 = mostAdapted.getGenes()[i].getTerrestrialAction();
            final char tmp2 = mostAdapted.getGenes()[i].getAirAction();

            mostAdapted.getGenes()[i].setTerrestrialAction(secondMostAdapted.getGenes()[i].getTerrestrialAction());
            mostAdapted.getGenes()[i].setAirAction(secondMostAdapted.getGenes()[i].getAirAction());
            secondMostAdapted.getGenes()[i].setTerrestrialAction(tmp1);
            secondMostAdapted.getGenes()[i].setAirAction(tmp2);
        }
        //System.out.println("Crossover point from "+geneLow+" to "+geneHigh);
    }

    private void mutate() {
        int mutationPoint1 = -20 + RANDOM.nextInt(40);
        int mutationPoint2 = -20 + RANDOM.nextInt(40);

        mutationPoint1 += mostAdapted.fitGeneId;
        mutationPoint2 += secondMostAdapted.fitGeneId;

        if (mutationPoint1 < 0) {
            mutationPoint1 = 0;
        }
        if (mutationPoint2 < 0) {
            mutationPoint2 = 0;
        }
        mostAdapted.getGenes()[mutationPoint1].setTerrestrialAction(LOSGene.RANDOM_T_ACTION());
        mostAdapted.getGenes()[mutationPoint1].setAirAction(LOSGene.RANDOM_A_ACTION());
        secondMostAdapted.getGenes()[mutationPoint2].setTerrestrialAction(LOSGene.RANDOM_T_ACTION());
        secondMostAdapted.getGenes()[mutationPoint2].setAirAction(LOSGene.RANDOM_A_ACTION());

        // Mutate the rest from the gene lock position
        for (IndividualA individual : population) {
            if (individual == mostAdapted || individual == secondMostAdapted) {
                continue;
            }
            for (int i = geneIdLock - geneLockRadius; i < geneIdLock + geneLockRadius; i++) {
                individual.getGenes()[i].setTerrestrialAction(LOSGene.RANDOM_T_ACTION());
                individual.getGenes()[i].setAirAction(LOSGene.RANDOM_A_ACTION());
            }
        }
        //System.out.println("Mutation at " + mutationPoint1+", "+mutationPoint2);
    }

    private void setMostAdaptedOffspring() {
        final IndividualA offspring = getOffspring();
        final IndividualA leastAdapted = getLeastAdapted();
        final LOSGeneA[] offspringGenes = offspring.getGenes();

        for (int i = 0; i < offspringGenes.length; i++) {
            leastAdapted.getGenes()[i].setTerrestrialAction(offspringGenes[i].getTerrestrialAction());
            leastAdapted.getGenes()[i].setAirAction(offspringGenes[i].getAirAction());
        }
    }

    private boolean areGenesLocked() {
        return spaceObserver
                   .getCurrentTx() <= (geneIdLock * IndividualA.STEP_TX - geneLockRadius * IndividualA.STEP_TX);
    }

}
