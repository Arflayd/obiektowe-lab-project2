package Simulation;

import Project.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import static java.lang.Math.sqrt;

public class Visualiser {

    private GraphicsContext map2D;

    private final int squareSize = 60;
    private final int mapSize;
    private final Simulation simulation;
    private final WorldMap map;

    public Visualiser(Simulation simulation, WorldMap map){
        this.simulation = simulation;
        this.map = map;
        this.mapSize = map.mapSize;
        initialize();
    }

    private void initialize(){
        Stage stage = new Stage();
        stage.setTitle("Simulation");

        Canvas canvas = new Canvas();
        canvas.setWidth(mapSize * squareSize);
        canvas.setHeight(mapSize * squareSize);
        map2D = canvas.getGraphicsContext2D();

        HBox container = new HBox(canvas);
        container.setBackground(new Background(new BackgroundFill(Color.valueOf("#eeeeee"), CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(container);
        simulation.setScene(scene);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void display(){
        drawMap();
        drawStats();
    }

    private void drawMap(){
        map2D.setFill(Color.valueOf("#333333"));
        map2D.fillRect(0, 0, mapSize * squareSize, mapSize * squareSize);

        map2D.setStroke(Color.valueOf("#444444"));
        map2D.setLineWidth(3d);
        for(int i = 0; i <= mapSize; i++){
            map2D.strokeLine(i * squareSize, 0, i * squareSize, mapSize * squareSize);
            map2D.strokeLine(0, i * squareSize, mapSize * squareSize, i * squareSize);
        }

        for(Tank tank : simulation.getEnemyTanks()){
            map2D.setFill(Color.valueOf("#ddaaaa"));
            Vector2d position = tank.getPosition();
            MapDirection orientation = tank.getOrientation();

            map2D.fillRect(position.x * squareSize + squareSize/4d, position.y * squareSize + squareSize/4d, squareSize- squareSize/2d, squareSize- squareSize/2d);
            drawTankBarrel(position, orientation);
        }

        for(Wall wall : simulation.getWalls()){
            map2D.setFill(Color.valueOf("#222222"));
            Vector2d position = wall.getPosition();
            if(wall.getHealth() == 1)
                map2D.setFill(Color.valueOf("#111111"));
            else
                map2D.setFill(Color.valueOf("#222222"));

            map2D.fillRoundRect(position.x * squareSize, position.y * squareSize, squareSize, squareSize, 5d, 5d);
            map2D.setStroke(Color.valueOf("#111111"));
            map2D.setLineWidth(3d);
            map2D.strokeRoundRect(position.x * squareSize, position.y * squareSize, squareSize, squareSize, 5d, 5d);
        }

        for(Bullet bullet : simulation.getBullets()){
            map2D.setFill(Color.valueOf("#ff7777"));
            Vector2d position = bullet.getPosition();
            drawBulletTrail(position, bullet.getDirection());
            drawBullet(position, bullet.getDirection());
        }

        // Player
        map2D.setFill(Color.valueOf("#aaaadd"));
        Vector2d position = simulation.getPlayerTank().getPosition();
        MapDirection orientation = simulation.getPlayerTank().getOrientation();

        map2D.fillRect(position.x * squareSize + squareSize/4d, position.y * squareSize + squareSize/4d, squareSize- squareSize/2d, squareSize- squareSize/2d);
        drawTankBarrel(position, orientation);
    }


    private void drawBullet(Vector2d position, MapDirection direction) {
        int xMult = 0;
        int yMult = 0;
        if(direction.toUnitVector().x == 1)
            xMult = 2;
        if(direction.toUnitVector().x == 0)
            xMult = 1;
        if(direction.toUnitVector().y == 1)
            yMult = 2;
        if(direction.toUnitVector().y == 0)
            yMult = 1;
        map2D.setFill(new Color(1, 0, 0, 1));
        map2D.fillOval(position.x * squareSize + xMult*squareSize/3d, position.y * squareSize + yMult*squareSize/3d, squareSize- squareSize/1.5d, squareSize- squareSize/1.5d);
        map2D.setStroke(Color.valueOf("#111111"));
        map2D.setLineWidth(3d);
        map2D.strokeOval(position.x * squareSize + xMult*squareSize/3d, position.y * squareSize + yMult*squareSize/3d, squareSize- squareSize/1.5d, squareSize- squareSize/1.5d);
    }

    private void drawTankBarrel(Vector2d position, MapDirection orientation){

        Vector2d barrelEnd = orientation.toUnitVector();

        map2D.setStroke(Color.valueOf("#eeeeff"));
        map2D.setLineWidth(5d);
        if(barrelEnd.x == 0 || barrelEnd.y == 0)
            map2D.strokeLine(position.x * squareSize+ squareSize/2d, position.y * squareSize+ squareSize/2d, position.x * squareSize+ squareSize/2d + barrelEnd.x * squareSize/2.5d, position.y * squareSize+ squareSize/2d + barrelEnd.y * squareSize/2.5d);
        else
            map2D.strokeLine(position.x * squareSize+ squareSize/2d, position.y * squareSize+ squareSize/2d, position.x * squareSize+ squareSize/2d + barrelEnd.x/sqrt(2) * squareSize/2.5d, position.y * squareSize+ squareSize/2d + barrelEnd.y/sqrt(2) * squareSize/2.5d);

    }

    private void drawBulletTrail(Vector2d position, MapDirection orientation){

        int xMult = 0;
        int yMult = 0;
        if(orientation.toUnitVector().x == 1)
            xMult = 2;
        if(orientation.toUnitVector().x == 0)
            xMult = 1;
        if(orientation.toUnitVector().y == 1)
            yMult = 2;
        if(orientation.toUnitVector().y == 0)
            yMult = 1;

        int xMultEnd = 1;
        int yMultEnd = 1;
        if(xMult == 0)
            xMultEnd = 2;
        if(xMult == 2)
            xMultEnd = 0;
        if(yMult == 0)
            yMultEnd = 2;
        if(yMult == 2)
            yMultEnd = 0;

        LinearGradient lg = new LinearGradient(xMultEnd, yMultEnd, xMult, yMult, true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, new Color(1, 1, 1, 0)),
                new Stop(1.0, new Color(1, 0, 0, 1)));

        map2D.setStroke(lg);
        map2D.setLineWidth(5d);
        map2D.strokeLine(position.x * squareSize + xMultEnd*squareSize/2d, position.y * squareSize + yMultEnd*squareSize/2d, position.x * squareSize + xMult*squareSize/6d + squareSize/3d, position.y * squareSize + yMult*squareSize/6d + squareSize/3d);
    }

    private void drawStats(){
        int health = simulation.getPlayerTank().getHealth();
        int score = simulation.getScore();

        map2D.setStroke(new Color(1, 1, 1, 1));
        map2D.setLineWidth(1);

        map2D.strokeText("Health: " + health, 10, 20);
        map2D.strokeText("Score: " + score, 10, 35);
    }

}
