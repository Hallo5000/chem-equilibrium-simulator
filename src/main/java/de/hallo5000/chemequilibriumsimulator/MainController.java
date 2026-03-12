package de.hallo5000.chemequilibriumsimulator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class MainController {

    @FXML private Slider sliderParticleCountA;
    @FXML private Slider sliderParticleCountB;
    @FXML private Slider sliderAvgSpeed;
    @FXML private Slider sliderActivationEnergy;

    //Output der slider values
    @FXML private Label ParticleCountAOutput;
    @FXML private Label ParticleCountBOutput;
    @FXML private Label avgSpeedOutput;
    @FXML private Label activationEnergyOutput;

    // Particle Bar
    @FXML private HBox particleBar;
    @FXML private Region barA;
    @FXML private Region barB;
    @FXML private Label labelCountA;
    @FXML private Label labelCountB;

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

        sliderActivationEnergy.setMin(0);
        sliderActivationEnergy.setMax(100);
        sliderActivationEnergy.setValue(50);

        sliderParticleCountA.valueProperty().addListener((_, _, newValue) -> {
            if (simulationHandler != null) {
                if(simulationHandler.getGamestate() == 1) sliderParticleCountA.setValue(simulationHandler.setParticleCountA(newValue.intValue()));
                ParticleCountAOutput.setText(Integer.toString((int) sliderParticleCountA.getValue()));
                updateParticleBar((int) sliderParticleCountA.getValue(), (int) sliderParticleCountB.getValue());
            }
        });

        sliderParticleCountB.valueProperty().addListener((_, _, newValue) -> {
            if (simulationHandler != null) {
                if(simulationHandler.getGamestate() == 1) sliderParticleCountB.setValue(simulationHandler.setParticleCountB(newValue.intValue()));
                ParticleCountBOutput.setText(Integer.toString((int) sliderParticleCountB.getValue()));
                updateParticleBar((int) sliderParticleCountA.getValue(), (int) sliderParticleCountB.getValue());
            }
        });

        sliderAvgSpeed.valueProperty().addListener((_, _, newValue) -> {
            if (simulationHandler != null) {
                simulationHandler.setAvgInitParticleSpeed((int)(newValue.doubleValue()*100));
                avgSpeedOutput.setText(Double.toString(simulationHandler.getAvgInitParticleSpeed()/100.0));
            }
        });

        sliderActivationEnergy.valueProperty().addListener((_, _, newValue) -> {
            if (simulationHandler != null) {
                simulationHandler.setActivationEnergy(newValue.intValue());
                activationEnergyOutput.setText(Integer.toString((int) simulationHandler.getActivationEnergy()));
            }
        });
    }

    public void updateParticleBar(int countA, int countB) {
        labelCountA.setText("A: " + countA);
        labelCountB.setText("B: " + countB);
        double total = countA + countB;
        double barlength = particleBar.getWidth();
        if (total == 0 || barlength == 0) {
            barA.setPrefWidth(0);
            barB.setPrefWidth(0);
            return;
        }
        barA.setPrefWidth((countA / total) * barlength);
        barB.setPrefWidth((countB / total) * barlength);

        if (countB == 0) {
            barA.setStyle("-fx-background-color: #BA6F02; -fx-background-radius: 8;");
        } else {
            barA.setStyle("-fx-background-color: #BA6F02; -fx-background-radius: 8 0 0 8;");
        }
        if (countA == 0) {
            barB.setStyle("-fx-background-color: #822B4A; -fx-background-radius: 8;");
        } else {
            barB.setStyle("-fx-background-color: #822B4A; -fx-background-radius: 0 8 8 0;");
        }
    }

    @FXML void startSim() {
        int countA = (int) sliderParticleCountA.getValue();
        int countB = (int) sliderParticleCountB.getValue();
        double avgSpeed = sliderAvgSpeed.getValue();
        double activationEnergy = sliderActivationEnergy.getValue();

        if (simulationHandler != null) {
            simulationHandler.setParticleCountA(countA);
            simulationHandler.setParticleCountB(countB);
            simulationHandler.setAvgInitParticleSpeed(avgSpeed);
            simulationHandler.setActivationEnergy(activationEnergy);
            simulationHandler.initSim();
            updateParticleBar(simulationHandler.getParticleCountA(), simulationHandler.getParticleCountB());
        }
    }

    @FXML public void stopSim() {
        if (simulationHandler != null) {
            simulationHandler.stopSim();
            updateParticleBar(0, 0);
        }
    }
}
