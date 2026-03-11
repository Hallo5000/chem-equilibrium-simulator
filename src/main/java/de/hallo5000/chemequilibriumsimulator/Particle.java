package de.hallo5000.chemequilibriumsimulator;

import javafx.geometry.Point2D;
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

    private Point2D coordinates;
    private final Circle circle;
    private Point2D direction_vec;
    private int speed;
    private State state;

    public static int RADIUS = 10;

    public Particle(Point2D coordinates, Point2D directionVec, int speed, AnchorPane simPane, State state) {
        this.coordinates = coordinates;
        Paint paint = state == State.A ? Paint.valueOf("#BA6F02") : Paint.valueOf("#822B4A");
        this.circle = new Circle(simPane.getLayoutX()+coordinates.getX()+(double)RADIUS/2,
                simPane.getLayoutY()+coordinates.getY()+(double)RADIUS/2, RADIUS, paint);
        simPane.getChildren().add(this.circle);
        this.direction_vec = directionVec;
        this.speed = speed;
        this.state = state;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point2D coordinates) {
        this.coordinates = coordinates;
        circle.setCenterX(coordinates.getX());
        circle.setCenterY(coordinates.getY());
    }

    public Circle getCircle(){
        return circle;
    }

    public Point2D getDirection_vec() {
        return direction_vec;
    }

    public void setDirection_vec(Point2D direction_vec) {
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
