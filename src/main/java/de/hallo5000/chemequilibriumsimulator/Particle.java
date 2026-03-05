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
    private double[] direction_vec;
    private int speed;
    private State state;

    public static int RADIUS = 10;

    public Particle(double[] coords, double[] directionVec, int speed, AnchorPane simPane, State state) {
        Paint paint = state == State.A ? Paint.valueOf("#BA6F02") : Paint.valueOf("#822B4A");
        this.circle = new Circle(simPane.getLayoutX()+coords[0]+(double)RADIUS/2,
                simPane.getLayoutY()+coords[1]+(double)RADIUS/2, RADIUS, paint);
        simPane.getChildren().add(this.circle);
        this.direction_vec = directionVec;
        this.speed = speed;
        this.state = state;
    }

    public Circle getCircle(){
        return circle;
    }

    public double[] getDirection_vec() {
        return direction_vec;
    }

    public void setDirection_vec(double[] direction_vec) {
        this.direction_vec = direction_vec;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
