package de.hallo5000.chemequilibriumsimulator;

import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class SimulationHandler {

    private int particleCountA;
    private int particleCountB;
    private double avgInitParticleSpeed;
    private final ArrayList<Particle> allParticles = new ArrayList<>();
    private final AnchorPane simPane;

    private final int COLLISION_THRESHOLD = 500;

    public SimulationHandler(int particleCountA, int particleCountB, double avgInitParticleSpeed, AnchorPane simPane){
        this.particleCountA = particleCountA;
        this.particleCountB = particleCountB;
        this.avgInitParticleSpeed = avgInitParticleSpeed;
        this.simPane = simPane;
    }

    public void initSim(){
        stopSim();
        for(int i = 0; i < particleCountA; i++){
            double[] coords = MainApplication.simulationHandler.calcRandFreeCoords(100);
            if(coords == null) break;
            allParticles.add(new Particle(coords, new int[]{0, 0}, 0, simPane, Particle.State.A));
        }
        for(int i = 0; i < particleCountB; i++){
            double[] coords = MainApplication.simulationHandler.calcRandFreeCoords(100);
            if(coords == null) break;
            allParticles.add(new Particle(coords, new int[]{0, 0}, 0, simPane, Particle.State.B));
        }
        updateSim();
        //simLoop(); //start updating simulation every 'tick'
    }

    private void simLoop(){
        while(true){

        }
    }

    private void updateSim(){
        for(Particle p : allParticles){
            if(!simPane.getChildren().contains(p.getCircle())){
                p.getCircle().setCenterX(simPane.getLayoutX()+p.getCircle().getCenterX()+ (double) Particle.RADIUS /2);
                p.getCircle().setCenterY(simPane.getLayoutY()+p.getCircle().getCenterY()+ (double) Particle.RADIUS /2);
                simPane.getChildren().add(p.getCircle());
            }
        }
    }

    public void stopSim(){
        allParticles.clear();
        simPane.getChildren().clear();
        updateSim();
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
                allParticles.add(new Particle(coords, new int[]{0, 0}, 0, simPane, Particle.State.A));
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
                allParticles.add(new Particle(coords, new int[]{0, 0}, 0, simPane, Particle.State.B));
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

    public ArrayList<Particle> getParticlesA(){
        return allParticles.stream().filter(p -> p.getState() == Particle.State.A).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Particle> getParticlesB(){
        return allParticles.stream().filter(p -> p.getState() == Particle.State.B).collect(Collectors.toCollection(ArrayList::new));
    }
}
