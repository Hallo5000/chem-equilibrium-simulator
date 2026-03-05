package de.hallo5000.chemequilibriumsimulator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class MainController {

    @FXML private Slider sliderParticleCountA;
    @FXML private Slider sliderParticleCountB;
    @FXML private Slider sliderAvgSpeed;

    //Output der slider values
    @FXML private Label ParticleCountAOutput;
    @FXML private Label ParticleCountBOutput;

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

        sliderParticleCountA.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (simulationHandler != null) {
                sliderParticleCountA.setValue(simulationHandler.setParticleCountA((int) sliderParticleCountA.getValue()));
                ParticleCountAOutput.setText(Integer.toString(simulationHandler.getParticleCountA()));
            }
        });

        sliderParticleCountB.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (simulationHandler != null) {
                sliderParticleCountB.setValue(simulationHandler.setParticleCountB((int) sliderParticleCountB.getValue()));
                ParticleCountBOutput.setText(Integer.toString(simulationHandler.getParticleCountB()));
            }
        });

        sliderAvgSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (simulationHandler != null) {
                simulationHandler.setAvgInitParticleSpeed(sliderAvgSpeed.getValue());
            }
        });
    }

    @FXML void startSim() {
        int countA = (int) sliderParticleCountA.getValue();
        int countB = (int) sliderParticleCountB.getValue();
        double avgSpeed = sliderAvgSpeed.getValue();

        if (simulationHandler != null) {
            simulationHandler.setParticleCountA(countA);
            simulationHandler.setParticleCountB(countB);
            simulationHandler.setAvgInitParticleSpeed(avgSpeed);
            simulationHandler.initSim();
        }
    }

    @FXML public void stopSim() {
        if (simulationHandler != null) {
            simulationHandler.stopSim();
        }
    }
}
