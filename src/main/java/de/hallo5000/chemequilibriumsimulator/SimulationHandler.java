package de.hallo5000.chemequilibriumsimulator;

import javafx.animation.AnimationTimer;
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

    private final int COLLISION_THRESHOLD = 500;

    public SimulationHandler(int particleCountA, int particleCountB, double avgInitParticleSpeed, double activationEnergy, AnchorPane simPane){
        this.particleCountA = particleCountA;
        this.particleCountB = particleCountB;
        this.avgInitParticleSpeed = avgInitParticleSpeed;
        this.activationEnergy = activationEnergy;
        this.simPane = simPane;
    }

    public void initSim(){
        allParticles.clear();
        simPane.getChildren().clear();//clear the arraylist and simPane
        for(int i = 0; i < particleCountA; i++){
            double[] coords = MainApplication.simulationHandler.calcRandFreeCoords(100);
            if(coords == null) break;
            allParticles.add(new Particle(coords, new double[]{1, 1}, 0, simPane, Particle.State.A));
        }
        for(int i = 0; i < particleCountB; i++){
            double[] coords = MainApplication.simulationHandler.calcRandFreeCoords(100);
            if(coords == null) break;
            allParticles.add(new Particle(coords, new double[]{1, 1}, 0, simPane, Particle.State.B));
        }
        simLoop(); //start moving particles
    }

    private void simLoop(){ //TODO: replace with solution bound to cpu instead of fps and fixed timestep instead of delta time
        AnimationTimer timer = new AnimationTimer() {
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
        for(Particle p1 : allParticles){
            double maxFactorX = 1;//max factor for the direction vector to hit the border on the x-axis on
            if(p1.getDirection_vec()[0] > 0){
                maxFactorX = ((simPane.getLayoutX()+simPane.getWidth())-(p1.getCircle().getCenterX()+Particle.RADIUS))/p1.getDirection_vec()[0];
            }else if(p1.getDirection_vec()[0] < 0){
                maxFactorX = (simPane.getLayoutX()-(p1.getCircle().getCenterX()+Particle.RADIUS))/p1.getDirection_vec()[0];
            }
            double maxFactorY = 1;//max factor for the direction vector to hit the border on the y-axis on
            if(p1.getDirection_vec()[1] > 0){
                maxFactorY = ((simPane.getLayoutY()+simPane.getHeight())-(p1.getCircle().getCenterY()+Particle.RADIUS))/p1.getDirection_vec()[1];
            }else if(p1.getDirection_vec()[1] < 0){
                maxFactorY = (simPane.getLayoutY()-(p1.getCircle().getCenterY()+Particle.RADIUS))/p1.getDirection_vec()[1];
            }

            for(Particle p2 : allParticles){
                if(p1 == p2) continue;
            }
            System.out.println("maxFactorX: "+maxFactorX);
            System.out.println("maxFactorY: "+maxFactorY);
            if(maxFactorX >= 1 && maxFactorY >= 1){
                p1.getCircle().setCenterX(p1.getCircle().getCenterX()+p1.getDirection_vec()[0]);
                p1.getCircle().setCenterY(p1.getCircle().getCenterY()+p1.getDirection_vec()[1]);
            }
        }
    }

    public void stopSim(){
        particleCountA = 0;
        particleCountB = 0;
        allParticles.clear();
        simPane.getChildren().clear();
    }

    public boolean collision(Particle a, Particle b){
        return false;
    }

    public double[] calcRandFreeCoords(int remainingTries){
        if(remainingTries <= 0) return null;
        double x = new Random().nextDouble() * simPane.getWidth() - Particle.RADIUS;
        double y = new Random().nextDouble() * simPane.getHeight() - Particle.RADIUS;
        System.out.println("x: "+x + " y: "+y);
        boolean intersects = simPane.getChildren().stream().anyMatch(node -> {
            if(node instanceof Circle c){
                if(Math.sqrt((x - c.getCenterX()) * (x - c.getCenterX()) + (y - c.getCenterY()) * (y - c.getCenterY())) < Particle.RADIUS*3){
                    return true;
                }
            }
            return false;
        });
        double[] finalCoords = new double[]{x, y};
        if(x < 0 || y < 0 || intersects) finalCoords = calcRandFreeCoords(remainingTries-1);
        return finalCoords;
    }

    public int getParticleCountA(){
        return particleCountA;
    }

    public int setParticleCountA(int particleCountA){
        if(particleCountA > this.particleCountA){
            for(int i = 0; i < particleCountA - this.particleCountA; i++){
                double[] coords = MainApplication.simulationHandler.calcRandFreeCoords(100);
                if(coords == null) break;
                allParticles.add(new Particle(coords, new double[]{0, 0}, 0, simPane, Particle.State.A));
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
                double[] coords = MainApplication.simulationHandler.calcRandFreeCoords(100);
                if(coords == null) break;
                allParticles.add(new Particle(coords, new double[]{0, 0}, 0, simPane, Particle.State.B));
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
}
