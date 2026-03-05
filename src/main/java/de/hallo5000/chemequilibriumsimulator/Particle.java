package de.hallo5000.chemequilibriumsimulator;

import java.util.Vector;

/**
 * This class describes one particle in the simulation
 */
public class Particle {

    private final int speed;
    private final int[] direction_vec;

    public Particle(int speed, int[] directionVec) {
        this.speed = speed;
        this.direction_vec = directionVec;
    }

    public int getSpeed() {
        return speed;
    }

    public int[] getDirection_vec() {
        return direction_vec;
    }
}
