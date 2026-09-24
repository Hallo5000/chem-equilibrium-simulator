package de.hallo5000.chemequilibriumsimulator;

import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class ParticleWrapper extends Particle {

    private final AnchorPane simPane;
    private final Circle circle;

    public ParticleWrapper(Point2D coordinates, Point2D directionVec, double speed, State state, AnchorPane simPane){
        super(coordinates, directionVec, speed, state);
        this.simPane = simPane;
        Paint paint = getState() == State.A ? getColorOfState(State.A) : getColorOfState(State.B);
        circle = new Circle(getCoordinates().getX()+(double)RADIUS/2,
                getCoordinates().getY()+(double)RADIUS/2, RADIUS, paint);
        simPane.getChildren().add(circle);
    }

    public ParticleWrapper(Point2D coordinates, Point2D directionVec, double speed, State state){
        this(coordinates, directionVec, speed, state, MainApplication.simulationHandler.getSimPane());
    }

    public void updateView() {
        circle.setCenterX(getCoordinates().getX());
        circle.setCenterY(getCoordinates().getY());
        Paint targetPaint = getState() == State.A ? getColorOfState(State.A) : getColorOfState(State.B);
        if (!circle.getFill().equals(targetPaint)) {
            circle.setFill(targetPaint);
        }
    }

    public static Paint getColorOfState(State state){
        return switch (state) {
            case A -> Paint.valueOf("#BA6F02");
            case B -> Paint.valueOf("#822B4A");
        };
    }

    public AnchorPane getSimPane() {
        return simPane;
    }

    public Circle getCircle() {
        return circle;
    }
}
