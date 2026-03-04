module de.hallo5000.chemequilibriumsimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.hallo5000.chemequilibriumsimulator to javafx.fxml;
    exports de.hallo5000.chemequilibriumsimulator;
}