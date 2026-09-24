package de.hallo5000.chemequilibriumsimulator;

import javafx.geometry.Point2D;

/**
 * This class describes one particle in the simulation
 */
public abstract class Particle {

    public enum State {
        A,
        B
    }

    private Point2D coordinates;
    private Point2D direction_vec;
    private double speed;
    private State state;

    public static int RADIUS = 10;

    public Particle(Point2D coordinates, Point2D directionVec, double speed, State state) {
        this.coordinates = coordinates;
        this.direction_vec = directionVec;
        this.speed = speed;
        this.state = state;
    }

    public Point2D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point2D coordinates) {
        this.coordinates = coordinates;
    }

    public Point2D getDirection_vec() {
        return direction_vec;
    }

    public void setDirection_vec(Point2D direction_vec) {
        this.direction_vec = direction_vec;
    }

    public double getSpeed() {
        return speed;
    }

    public Point2D getVelocity() {
        return direction_vec.multiply(speed);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
