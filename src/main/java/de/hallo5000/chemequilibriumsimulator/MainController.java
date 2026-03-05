package de.hallo5000.chemequilibriumsimulator;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;

public class MainController {

    @FXML private Slider sliderParticleCountA;
    @FXML private Slider sliderParticleCountB;
    @FXML private Slider sliderAvgSpeed;

    private SimulationHandler simulationHandler;

    public void setSimulationHandler(SimulationHandler simulationHandler) {
        this.simulationHandler = simulationHandler;
    }

    @FXML public void initialize() {
        sliderParticleCountA.setMin(0);
        sliderParticleCountA.setMax(100);
        sliderParticleCountA.setValue(10);

        sliderParticleCountB.setMin(0);
        sliderParticleCountB.setMax(100);
        sliderParticleCountB.setValue(10);

        sliderAvgSpeed.setMin(0);
        sliderAvgSpeed.setMax(1);
        sliderAvgSpeed.setValue(0.5);
    }

    @FXML void startSim() {
        int countA = (int) sliderParticleCountA.getValue();
        int countB = (int) sliderParticleCountB.getValue();
        int avgSpeed = (int) sliderAvgSpeed.getValue();

        simulationHandler = new SimulationHandler(countA, countB, avgSpeed);
        simulationHandler.initSim();
    }

    @FXML public void stopSim() {
        if (simulationHandler != null) {
            simulationHandler.stopSim();
        }
    }
}
