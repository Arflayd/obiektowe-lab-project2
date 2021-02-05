package Simulation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class SimulationEngine {

    private Timeline createLoop(Simulation simulation, int millisecondsPerEra) {
        final Duration duration = Duration.millis(millisecondsPerEra);
        final KeyFrame frame = new KeyFrame(duration, simulation::run);
        Timeline timeline = new Timeline(frame);
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    public void start() throws Exception{

        Simulation simulation = new Simulation();
        Timeline gameLoop = createLoop(simulation, 5);
        gameLoop.playFromStart();

    }

}
