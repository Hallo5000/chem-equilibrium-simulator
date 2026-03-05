package de.hallo5000.chemequilibriumsimulator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends javafx.application.Application {

    public static Scene scene;
    static SimulationHandler simulationHandler;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 800, 450);

        stage.setTitle("ChemicalEquilibriumSimulator");
        stage.setScene(scene);
        stage.show();

        MainController controller = fxmlLoader.getController();
        simulationHandler = new SimulationHandler(0, 0, 0.0, 0.0,  (AnchorPane) scene.lookup("#simPane"));
        controller.setSimulationHandler(simulationHandler);
    }
}
