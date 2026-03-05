package de.hallo5000.chemequilibriumsimulator;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Random;

public class SimulationHandler {

    private int particleCountA;
    private int particleCountB;
    private double avgInitParticleSpeed;
    private final ArrayList<Particle> particlesA = new ArrayList<Particle>();
    private final ArrayList<Particle> particlesB = new ArrayList<Particle>();

    private final int COLLISION_THRESHOLD = 500;

    public SimulationHandler(int particleCountA, int particleCountB, double avgInitParticleSpeed){
        this.particleCountA = particleCountA;
        this.particleCountB = particleCountB;
        this.avgInitParticleSpeed = avgInitParticleSpeed;
    }

    public void initSim(){
        for(int i = 0; i < particleCountA; i++){
            particlesA.add(new Particle(0, 0, new int[]{0, 0}, 0));
        }
        for(int i = 0; i < particleCountB; i++){
            particlesB.add(new Particle(0, 0, new int[]{0, 0}, 0));
        }
        updateSim();
        //simLoop(); //start updating simulation every 'tick'
    }

    private void simLoop(){
        while(true){

        }
    }

    private void updateSim(){
        ArrayList<Particle> allParticles = new ArrayList<>(particlesA);
        allParticles.addAll(particlesB);
        AnchorPane simPane = (AnchorPane) MainApplication.scene.lookup("#simPane");
        simPane.getChildren().clear();
        simPane.getChildren().addAll(allParticles.stream().map(p ->{
            if(!p.displayed){
                p.getCircle().setCenterX(simPane.getLayoutX() + p.getCircle().getCenterX() + (double) Particle.RADIUS/2);
                p.getCircle().setCenterY(simPane.getLayoutY() + p.getCircle().getCenterY() + (double) Particle.RADIUS/2);
            }
            p.displayed = true;
            return p.getCircle();
        }).toList());
    }

    public void stopSim(){
        particlesA.clear();
        particlesB.clear();
    }

    public boolean collision(Particle a, Particle B){
        return false;
    }

    public double[] calcRandFreeCoords(){
        double x = 0;
        double y = 0;
        x = new Random().nextDouble() * ((AnchorPane) MainApplication.scene.lookup("#simPane")).getWidth() - Particle.RADIUS;
        y = new Random().nextDouble() * ((AnchorPane) MainApplication.scene.lookup("#simPane")).getHeight() - Particle.RADIUS;
        System.out.println("x: "+x + " y: "+y);
        boolean intersects = false;
        for(Node node : ((AnchorPane) MainApplication.scene.lookup("#simPane")).getChildren()){
            if(node instanceof Circle c){
                if(Math.sqrt(x - c.getCenterX() * x - c.getCenterX() + y - c.getCenterY() * y - c.getCenterY()) - Particle.RADIUS*2 < 0){
                    intersects = true;
                    break;
                }
            }
        }
        double[] finalCoords = new double[]{x, y};
        if(x < 0 || y < 0 || intersects) finalCoords = calcRandFreeCoords();
        return finalCoords;
    }

    public int getParticleCountA(){
        return particleCountA;
    }

    public void setParticleCountA(int particleCountA){
        if(particleCountA > this.particleCountA){
            for(int i = 0; i < particleCountA - this.particleCountA; i++){
                particlesA.add(new Particle(0, 0, new int[]{0, 0}, 0));
            }
        }else if(particleCountA < this.particleCountA){
            for(int i = 0; i < this.particleCountA - particleCountA; i++){
                particlesA.remove(null);
            }
        }
        this.particleCountA = particleCountA;
    }

    public int getParticleCountB(){
        return particleCountB;
    }

    public void setParticleCountB(int particleCountB){
        if(particleCountB > this.particleCountB){
            for(int i = 0; i < particleCountB - this.particleCountB; i++){
                particlesB.add(new Particle(0, 0, new int[]{0, 0}, 0));
            }
        }else if(particleCountB < this.particleCountB){
            for(int i = 0; i < this.particleCountB - particleCountB; i++){
                particlesB.remove(null);
            }
        }
        this.particleCountB = particleCountB;
    }

    public double getAvgInitParticleSpeed(){
        return avgInitParticleSpeed;
    }

    public void setAvgInitParticleSpeed(double avgInitParticleSpeed){
        this.avgInitParticleSpeed = avgInitParticleSpeed;
    }

    public ArrayList<Particle> getParticlesA(){
        return particlesA;
    }

    public ArrayList<Particle> getParticlesB(){
        return particlesB;
    }
}
