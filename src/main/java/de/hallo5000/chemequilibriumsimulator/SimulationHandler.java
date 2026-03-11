package de.hallo5000.chemequilibriumsimulator;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class SimulationHandler {

    private int particleCountA;
    private int particleCountB;
    private double avgInitParticleSpeed;
    private double activationEnergy;
    private final ArrayList<Particle> allParticles = new ArrayList<>();
    private final AnchorPane simPane;

    // gamestate == 0 -> simulation is stopped/paused
    // gamestate == 1 -> simulation is running
    private int gamestate = 0;
    private AnimationTimer timer;

    public SimulationHandler(int particleCountA, int particleCountB, double avgInitParticleSpeed, double activationEnergy, AnchorPane simPane){
        this.particleCountA = particleCountA;
        this.particleCountB = particleCountB;
        this.avgInitParticleSpeed = avgInitParticleSpeed;
        this.activationEnergy = activationEnergy;
        this.simPane = simPane;
    }

    public void initSim(){
        if(gamestate == 1) return;
        gamestate = 1;
        allParticles.clear();
        simPane.getChildren().clear();//clear the arraylist and simPane
        for(int i = 0; i < particleCountA; i++){
            Point2D coords = MainApplication.simulationHandler.genRandomCoords(100);
            if(coords == null) break;
            allParticles.add(new Particle(coords, genRandomVector(), 0, simPane, Particle.State.A));
        }
        for(int i = 0; i < particleCountB; i++){
            Point2D coords = MainApplication.simulationHandler.genRandomCoords(100);
            if(coords == null) break;
            allParticles.add(new Particle(coords, genRandomVector(), 0, simPane, Particle.State.B));
        }
        simLoop(); //start moving particles
    }

    private void simLoop(){ //TODO: replace with solution bound to cpu instead of fps and fixed timestep instead of delta time
        timer = new AnimationTimer() {
            long lastUpdate = 0;
            @Override
            public void handle(long now) {
                double delta = (now-lastUpdate) / 1000000.0; //in milliseconds
                if(delta >= 10){//100 updates per second
                    lastUpdate = now;
                    updateSim();
                }
            }
        };
        timer.start();
    }

    private void updateSim(){
        ArrayList<Particle> compareTo = new ArrayList<>(allParticles);
        for(Particle p1 : allParticles){
            double maxFactorX = 1;//max factor for the direction vector to hit the border on the x-axis on
            if(p1.getDirection_vec().getX() > 0){
                maxFactorX = ((simPane.getLayoutX()+simPane.getWidth())-(p1.getCircle().getCenterX()+Particle.RADIUS))/p1.getDirection_vec().getX();
            }else if(p1.getDirection_vec().getX() < 0){
                maxFactorX = (simPane.getLayoutX()-(p1.getCircle().getCenterX()-Particle.RADIUS))/p1.getDirection_vec().getX();
            }
            double maxFactorY = 1;//max factor for the direction vector to hit the border on the y-axis on
            if(p1.getDirection_vec().getY() > 0){
                maxFactorY = ((simPane.getLayoutY()+simPane.getHeight())-(p1.getCircle().getCenterY()+Particle.RADIUS))/p1.getDirection_vec().getY();
            }else if(p1.getDirection_vec().getY() < 0){
                maxFactorY = (simPane.getLayoutY()-(p1.getCircle().getCenterY()-Particle.RADIUS))/p1.getDirection_vec().getY();
            }

            compareTo.remove(p1);
            for(Particle p2 : compareTo){

                /* solve for t (factor for the vectors at which the particles collide and "end of collision"):
                   a1 := coords of p1
                   b1 := vector of p1
                   a2 := coords of p2
                   b2 := vector of p2
                    |(a1+t*b1)-(a2+t*b2)|=2R
                    -----use d(x) := delta function
                    |d(a)+t*d(b)|=2R
                    -----absolute value of vectors |v| := sqrt( (vx)^2 + (vy)^2 )
                    sqrt((d(ax)+t*d(bx))^2 + (d(ay)+t*d(by))^2)=2R
                    -----square to get rid of the square root
                    ((d(ax)+t*d(bx))^2+(d(ay)+t*d(by))^2)=4R^2
                    -----binomial theorem
                    ((d(ax)^2+2d(ax)t*d(bx)+t^2d(bx)^2)+(d(ay)^2+2d(ay)d(by)+t^2d(by)^2))=4R^2
                    -----get rid of a few unnecessary brackets
                    d(ax)^2+2d(ax)t*d(bx)+t^2d(bx)^2+d(ay)^2+2d(ay)t*d(by)+t^2d(by)^2=4R^2
                    -----quadratic form
                    (d(bx)^2+d(by)^2)*t^2+(2d(ax)d(bx)+2d(ay)d(by))*t+(-4R^2+d(ax)^2+d(ay)^2)=0
                    -----quadratic formula
                    t1,2=( -(2d(ax)d(bx)+2d(ay)d(by)) +/- sqrt( (2d(ax)d(bx)+2d(ay)d(by))^2 - 4(d(bx)^2+d(by)^2)(-4R^2+d(ax)^2+d(ay)^2) ) )/2(d(bx)^2+d(by)^2)
                 */


                Point2D deltaP = p1.getCoordinates().subtract(p2.getCoordinates());
                Point2D deltaV = p1.getDirection_vec().subtract(p2.getDirection_vec());

                double factorA = deltaV.dotProduct(deltaV);
                double factorB = 2*deltaP.getX()*deltaV.getX()+2*deltaP.getY()*deltaV.getY();
                double factorC = -4*Math.pow(Particle.RADIUS, 2)+deltaP.dotProduct(deltaP);

                if(2*factorA == 0) continue; //extremely unlikely: movement of p1 and p2 is synchronized (e.g. both still standing or same movement vector) this would cause division by 0

                double discriminant = Math.pow(factorB, 2)
                        - 4*factorA*factorC; // b² - 4ac

                if(discriminant >= 0){ //collision
                    double t1 = ( -factorB - Math.sqrt(discriminant) )  / (2*factorA); // (-b - sqrt(b^2 - 4ac)) / 2a
                    double t2 = ( -(factorB) + Math.sqrt(discriminant) ) / (2*factorA); // (-b + sqrt(b^2 - 4ac)) / 2a
                    System.out.println("t1: "+t1);
                    System.out.println("t2: "+t2);
                    if((t1 >= 0 && t1 <= 1) || (t2 <= 1 && t1 < 0)){
                        System.out.println("COLLISION");
                        p1.setDirection_vec(p1.getDirection_vec().multiply(-1));
                        p2.setDirection_vec(p2.getDirection_vec().multiply(-1));
                        if(p1.getState() == p2.getState()){
                            if(p1.getState() == Particle.State.A){
                                p1.setState(Particle.State.B);
                                p2.setState(Particle.State.B);
                            }else{
                                p1.setState(Particle.State.A);
                                p1.setState(Particle.State.A);
                            }
                        }
                    }
                }
            }

            if(maxFactorX >= 1 && maxFactorY >= 1){
                p1.setCoordinates(new Point2D(
                    p1.getCircle().getCenterX()+p1.getDirection_vec().getX(),
                    p1.getCircle().getCenterY()+p1.getDirection_vec().getY()
                ));
            }else{
                double beforeReflect = Math.min(maxFactorX, maxFactorY); //the percentage of the vector applied before reflecting of a wall

                p1.getCircle().setCenterX(p1.getCircle().getCenterX()+p1.getDirection_vec().getX()*beforeReflect);
                p1.getCircle().setCenterY(p1.getCircle().getCenterY()+p1.getDirection_vec().getY()*beforeReflect);

                if(maxFactorX < maxFactorY) p1.setDirection_vec(new Point2D(p1.getDirection_vec().getX()*(-1), p1.getDirection_vec().getY()));
                else p1.setDirection_vec(new Point2D(p1.getDirection_vec().getX(), p1.getDirection_vec().getY()*(-1)));

                p1.setCoordinates(new Point2D(
                    p1.getCircle().getCenterX()+p1.getDirection_vec().getX()*(1-beforeReflect),
                    p1.getCircle().getCenterY()+p1.getDirection_vec().getY()*(1-beforeReflect)
                ));
            }
        }
    }

    public void stopSim(){
        timer.stop();
        gamestate = 0;
        particleCountA = 0;
        particleCountB = 0;
        allParticles.clear();
        simPane.getChildren().clear();
    }

    public boolean collision(Particle a, Particle b){
        return false;
    }

    public Point2D genRandomCoords(int remainingTries){
        if(remainingTries <= 0) return null;
        double x = new Random().nextDouble() * simPane.getWidth() - Particle.RADIUS;
        double y = new Random().nextDouble() * simPane.getHeight() - Particle.RADIUS;
        System.out.println("x: "+x + " y: "+y);
        boolean intersects = simPane.getChildren().stream().anyMatch(node -> {
            if(node instanceof Circle c){
                return Math.sqrt((x - c.getCenterX()) * (x - c.getCenterX()) + (y - c.getCenterY()) * (y - c.getCenterY())) < Particle.RADIUS * 3;
            }
            return false;
        });
        Point2D finalCoords = new Point2D(x, y);
        if(x < 0 || y < 0 || intersects) finalCoords = genRandomCoords(remainingTries-1);
        return finalCoords;
    }

    public Point2D genRandomVector(){
        double x = new Random().nextDouble(2.0)-1;//upper bound is excluded,
        double y = new Random().nextDouble(2.0)-1;//but it doesn't matter since the chance of getting it would be near impossible anyway

        double length = Math.sqrt(x*x + y*y);
        x = x/length;
        y = y/length;

        return new Point2D(x, y);
    }

    public int getParticleCountA(){
        return particleCountA;
    }

    public int setParticleCountA(int particleCountA){
        if(particleCountA > this.particleCountA){
            for(int i = 0; i < particleCountA - this.particleCountA; i++){
                Point2D coords = MainApplication.simulationHandler.genRandomCoords(100);
                if(coords == null) break;
                allParticles.add(new Particle(coords, genRandomVector(), 0, simPane, Particle.State.A));
            }
        }else if(particleCountA < this.particleCountA){
            for(int i = 0; i < this.particleCountA - particleCountA; i++){
                Particle toRemove = getParticlesA().get(new Random().nextInt(getParticlesA().size()));
                allParticles.remove(toRemove);
                simPane.getChildren().remove(toRemove.getCircle());
            }
        }
        this.particleCountA = particleCountA;
        if(allParticles.size() != particleCountA+particleCountB) this.particleCountA = allParticles.size()-particleCountB;
        return this.particleCountA;
    }

    public int getParticleCountB(){
        return particleCountB;
    }

    public int setParticleCountB(int particleCountB){
        if(particleCountB > this.particleCountB){
            for(int i = 0; i < particleCountB - this.particleCountB; i++){
                Point2D coords = MainApplication.simulationHandler.genRandomCoords(100);
                if(coords == null) break;
                allParticles.add(new Particle(coords, genRandomVector(), 0, simPane, Particle.State.B));
            }
        }else if(particleCountB < this.particleCountB){
            for(int i = 0; i < this.particleCountB - particleCountB; i++){
                Particle toRemove = getParticlesB().get(new Random().nextInt(getParticlesB().size()));
                allParticles.remove(toRemove);
                simPane.getChildren().remove(toRemove.getCircle());
            }
        }
        this.particleCountB = particleCountB;
        if(allParticles.size() != particleCountB+particleCountA) this.particleCountB = allParticles.size()-particleCountA;
        return this.particleCountB;
    }

    public double getAvgInitParticleSpeed(){
        return avgInitParticleSpeed;
    }

    public void setAvgInitParticleSpeed(double avgInitParticleSpeed){
        this.avgInitParticleSpeed = avgInitParticleSpeed;
    }

    public double getActivationEnergy() {
        return activationEnergy;
    }

    public void setActivationEnergy(double activationEnergy) {
        this.activationEnergy = activationEnergy;
    }

    public ArrayList<Particle> getParticlesA(){
        return allParticles.stream().filter(p -> p.getState() == Particle.State.A).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Particle> getParticlesB(){
        return allParticles.stream().filter(p -> p.getState() == Particle.State.B).collect(Collectors.toCollection(ArrayList::new));
    }

    public int getGamestate() {
        return gamestate;
    }
}
