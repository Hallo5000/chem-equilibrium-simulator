package de.hallo5000.chemequilibriumsimulator;

import java.util.ArrayList;

public class SimulationHandler {

    private final int particleCountA;
    private final int particleCountB;
    private final int avgInitParticleSpeed;
    private final ArrayList<Particle> particlesA = new ArrayList<Particle>();
    private final ArrayList<Particle> particlesB = new ArrayList<Particle>();

    private final int COLLISION_THRESHOLD = 500;

    public SimulationHandler(int particleCountA, int particleCountB, int avgInitParticleSpeed){
        this.particleCountA = particleCountA;
        this.particleCountB = particleCountB;
        this.avgInitParticleSpeed = avgInitParticleSpeed;
    }

    public void initSim(){
        for(int i = 0; i < particleCountA; i++){
            particlesA.add(new Particle(0, new int[]{0, 0}));
        }
        for(int i = 0; i < particleCountB; i++){
            particlesB.add(new Particle(0, new int[]{0, 0}));
        }
    }

    public void stopSim(){

    }

    public boolean collision(Particle a, Particle B){
        return false;
    }

    public int getParticleCountA(){
        return particleCountA;
    }

    public int getParticleCountB(){
        return particleCountB;
    }

    public int getAvgInitParticleSpeed(){
        return avgInitParticleSpeed;
    }

    public ArrayList<Particle> getParticlesA(){
        return particlesA;
    }

    public ArrayList<Particle> getParticlesB(){
        return particlesB;
    }
}
