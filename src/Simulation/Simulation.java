package Simulation;

import Project.*;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Random;

public class Simulation implements IMapElementDestroyedObserver{

    private final int mapSize = 15;
    private final WorldMap map = new WorldMap(mapSize);
    private final Tank playerTank = new Tank(map, new Vector2d(mapSize/2, mapSize/2), 5);
    private final ArrayList<Tank> enemyTanks = new ArrayList<>();
    private final ArrayList<Wall> walls = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private int score = 0;
    private boolean gameOver = false;

    private Scene scene;
    private final Visualiser visualiser = new Visualiser(this, map);;

    private boolean playerTurn = true;

    public Simulation(){
        playerTank.addDestroyObserver(this);
        playerTank.addDestroyObserver(map);
        playerTank.addChangeObserver(map);
        initializeEvents();
        spawnTank(map.randomUnoccupiedPosition(playerTank.getPosition(), true));
    }

    void run(ActionEvent actionEvent) {
        update();
        visualiser.display();
    }

    private void update(){

        if(!playerTurn && !gameOver){
            moveEnemyTanks();
            moveBullets();
            spawnNewObject();
            if(playerTank.getHealth() <= 0)
                gameOver = true;
            playerTurn = true;
        }
    }

    private void spawnTank(Vector2d position){
        Tank objectToSpawn = new Tank(map, position, 1);
        enemyTanks.add((Tank) objectToSpawn);
        ((Tank) objectToSpawn).addChangeObserver(map);
        objectToSpawn.addDestroyObserver(this);
        objectToSpawn.addDestroyObserver(map);
        map.place(objectToSpawn);
    }

    private void spawnNewObject(){
        Random random = new Random();
        double pick = random.nextDouble();
        IMapElement objectToSpawn;

        if(pick < 0.3d){

            pick = random.nextDouble();

            if(pick < 0.75d){
                Vector2d position = map.randomUnoccupiedPosition(playerTank.getPosition(), false);
                objectToSpawn = new Wall(position);
                walls.add((Wall) objectToSpawn);
            }
            else{
                Vector2d position = map.randomUnoccupiedPosition(playerTank.getPosition(), true);
                objectToSpawn = new Tank(map, position, 1);
                enemyTanks.add((Tank) objectToSpawn);
                ((Tank) objectToSpawn).addChangeObserver(map);
            }

            objectToSpawn.addDestroyObserver(this);
            objectToSpawn.addDestroyObserver(map);
            map.place(objectToSpawn);
        }
    }

    private void moveEnemyTanks(){

        Random random = new Random();

        for(Tank tank : enemyTanks){
            double pick = random.nextDouble();

            MapDirection direction = tank.aimAtPlayer(playerTank.getPosition());

            if(pick < 0.5d){
                if(direction == MapDirection.NORTHEAST)
                    direction = !random.nextBoolean() ? MapDirection.NORTH : MapDirection.EAST;
                else if(direction == MapDirection.NORTHWEST)
                    direction = !random.nextBoolean() ? MapDirection.NORTH : MapDirection.WEST;
                else if(direction == MapDirection.SOUTHEAST)
                    direction = !random.nextBoolean() ? MapDirection.SOUTH : MapDirection.EAST;
                else
                    direction = !random.nextBoolean() ? MapDirection.SOUTH : MapDirection.WEST;

                if(tank.move(direction))
                    continue;
            }

            fireBullet(tank.getPosition(), direction);
        }
    }

    public void setScene(Scene scene){
        this.scene = scene;
    }

    private void initializeEvents(){
        scene.setOnKeyPressed(e -> {

            if(!playerTurn)
                return;

            if (e.getCode() == KeyCode.E) {
                playerTank.rotateRight();
                visualiser.display();
            }
            if (e.getCode() == KeyCode.Q) {
                playerTank.rotateLeft();
                visualiser.display();
            }

            MapDirection moveDirection = null;
            if (e.getCode() == KeyCode.W)
                moveDirection = MapDirection.SOUTH;
            if (e.getCode() == KeyCode.A)
                moveDirection = MapDirection.WEST;
            if (e.getCode() == KeyCode.S)
                moveDirection = MapDirection.NORTH;
            if (e.getCode() == KeyCode.D)
                moveDirection = MapDirection.EAST;

            if(moveDirection != null){
                if(playerTank.move(moveDirection)){
                    visualiser.display();
                    playerTurn = false;
                }
            }

            if (e.getCode() == KeyCode.SPACE) {
                fireBullet(playerTank.getPosition(), playerTank.getOrientation());
                visualiser.display();
                playerTurn = false;
            }
        });
    }

    private void fireBullet(Vector2d position, MapDirection direction){
        Bullet bullet = new Bullet(map, position, direction);
        bullet.addChangeObserver(map);
        bullet.addDestroyObserver(this);
        bullet.addDestroyObserver(map);
        bullets.add(bullet);
        map.place(bullet);
    }

    private void moveBullets(){

        ArrayList<Bullet> oldBullets = (ArrayList<Bullet>) bullets.clone();
        for(Bullet bullet : oldBullets)
            bullet.setMovedThisTurn(false);

        for(Bullet bullet : oldBullets){
            if(!bullet.destroyed)
                bullet.move();
        }

        oldBullets.clear();
    }

    public ArrayList<Tank> getEnemyTanks(){
        return enemyTanks;
    }

    public ArrayList<Wall> getWalls(){
        return walls;
    }

    public ArrayList<Bullet> getBullets(){
        return bullets;
    }

    public Tank getPlayerTank(){
        return playerTank;
    }

    public int getScore(){
        return score;
    }

    @Override
    public void mapElementDestroyed(IMapElement mapElement, Vector2d position) {
        if(mapElement.getClass() == Bullet.class){
            bullets.remove(mapElement);
        }
        else if(mapElement.getClass() == Wall.class){
            walls.remove(mapElement);
        }
        else{
            enemyTanks.remove(mapElement);
            score++;

            if(enemyTanks.isEmpty())
                spawnTank(map.randomUnoccupiedPosition(playerTank.getPosition(), true));
        }
    }
}
