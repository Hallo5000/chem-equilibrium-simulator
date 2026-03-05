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

    public Particle(int x, int y, int[] directionVec, int speed) {
        this.circle = new Circle(x, y, 20, Paint.valueOf("#000000"));
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
