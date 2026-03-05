package de.hallo5000.chemequilibriumsimulator;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * This class describes one particle in the simulation
 */
public class Particle {

    public enum State {
        A,
        B
    }
    private final Circle circle;
    private int[] direction_vec;
    private int speed;
    private State state;

    public static int RADIUS = 10;

    public Particle(int[] directionVec, int speed, AnchorPane simPane, State state) {
        double[] coords = MainApplication.simulationHandler.calcRandFreeCoords();
        this.circle = new Circle(simPane.getLayoutX()+coords[0]+(double)RADIUS/2, simPane.getLayoutY()+coords[1]+(double)RADIUS/2, RADIUS, Paint.valueOf("#000000"));
        simPane.getChildren().add(this.circle);
        this.direction_vec = directionVec;
        this.speed = speed;
        this.state = state;
    }

    public Circle getCircle(){
        return circle;
    }

    public int[] getDirection_vec() {
        return direction_vec;
    }

    public int getSpeed() {
        return speed;
    }

    public State getState() {
        return state;
    }
}
