package de.hallo5000.chemequilibriumsimulator;

import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

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
        particlesA.addAll(particlesB);
        AnchorPane simPane = (AnchorPane) MainApplication.scene.lookup("simPane");
        simPane.getChildren().clear();
        simPane.getChildren().addAll(allParticles.stream().map(Particle::getCircle).toList());
    }

    public void stopSim(){
        particlesA.clear();
        particlesB.clear();
    }

    public boolean collision(Particle a, Particle B){
        return false;
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
