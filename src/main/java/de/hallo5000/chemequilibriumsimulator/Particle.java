package de.hallo5000.chemequilibriumsimulator;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.Vector;

/**
 * This class describes one particle in the simulation
 */
public class Particle {

    private final Circle circle;
    private final int[] direction_vec;
    private final int speed;

    public boolean displayed = false;
    public static int RADIUS = 10;

    public Particle(int x, int y, int[] directionVec, int speed) {
        double[] coords = MainApplication.simulationHandler.calcRandFreeCoords();
        this.circle = new Circle(coords[0], coords[1], RADIUS, Paint.valueOf("#000000"));
        this.direction_vec = directionVec;
        this.speed = speed;
    }

    public Circle getCircle(){
        return circle;
    }

    public int getSpeed() {
        return speed;
    }

    public int[] getDirection_vec() {
        return direction_vec;
    }
}
