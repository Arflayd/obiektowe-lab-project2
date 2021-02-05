package Simulation;

import javafx.application.Application;
import javafx.stage.Stage;

public class SimulationLauncher extends Application {

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {


        SimulationEngine simulationEngine = new SimulationEngine();
        simulationEngine.start();

    }
}
