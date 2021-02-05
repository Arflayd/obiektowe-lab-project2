package Project;

import java.util.ArrayList;
import java.util.Random;

public class Tank implements IMapElement {

    private Vector2d position;
    private MapDirection orientation;
    private final WorldMap map;

    private int health;

    private ArrayList<IPositionChangeObserver> changeObservers = new ArrayList<>();
    ArrayList<IMapElementDestroyedObserver> destroyObservers = new ArrayList<>();

    private Random random = new Random();

    public Tank(WorldMap map, Vector2d initialPosition, int startHealth){
        this.map = map;
        this.position = initialPosition;
        this.orientation = MapDirection.values()[random.nextInt(MapDirection.values().length)];
        this.health = startHealth;
    }

    public void decreaseHealth(int amount){
        health -= amount;
        if(health <= 0)
            destroy();
    }

    public int getHealth(){
        return health;
    }

    public boolean move(MapDirection direction){

        Vector2d newPosition = position.add(direction.toUnitVector());

        if(map.canMoveTo(newPosition)){
            Vector2d oldPosition = position;
            position = newPosition;

            for(IPositionChangeObserver observer : changeObservers){
                observer.positionChanged(this, oldPosition, newPosition);
            }

            return true;
        }
        return false;
    }

    public void rotateLeft(){
        orientation = orientation.rotateBy(1);
    }

    public void rotateRight(){
        this.orientation = this.orientation.rotateBy(-1);
    }

    public void addChangeObserver(IPositionChangeObserver observer){
        changeObservers.add(observer);
    }

    public void removeChangeObserver(IPositionChangeObserver observer){
        changeObservers.remove(observer);
    }

    public Vector2d getPosition(){
        return position;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public void addDestroyObserver(IMapElementDestroyedObserver observer){
        destroyObservers.add(observer);
    }

    public void removeDestroyObserver(IMapElementDestroyedObserver observer){
        destroyObservers.remove(observer);
    }

    public void destroy(){
        for(IMapElementDestroyedObserver observer : destroyObservers)
            observer.mapElementDestroyed(this, position);
    }

    public MapDirection aimAtPlayer(Vector2d playerPosition){
        int x = playerPosition.x - position.x;
        int y = playerPosition.y - position.y;
        double tan = Math.atan(1.0d*y/x);
        MapDirection direction = null;

        if(x == 0 && y > 0)
            direction = MapDirection.NORTH;
        else if(x == 0 && y < 0)
            direction = MapDirection.SOUTH;
        else if(y == 0 && x > 0)
            direction = MapDirection.EAST;
        else if(y == 0 && x < 0)
            direction = MapDirection.WEST;
        else if(x > 0 && y < 0){
            if(tan > -0.4d)
                direction = MapDirection.EAST;
            else if(tan > -1.2d)
                direction = MapDirection.SOUTHEAST;
            else
                direction = MapDirection.SOUTH;
        }
        else if(x > 0 && y > 0){
            if(tan < 0.4d)
                direction = MapDirection.EAST;
            else if(tan < 1.2d)
                direction = MapDirection.NORTHEAST;
            else
                direction = MapDirection.NORTH;
        }
        else if(x < 0 && y < 0){
            if(tan < 0.4d)
                direction = MapDirection.WEST;
            else if(tan < 1.2d)
                direction = MapDirection.SOUTHWEST;
            else
                direction = MapDirection.SOUTH;
        }
        else if(x < 0 && y > 0){
            if(tan > -0.4d)
                direction = MapDirection.WEST;
            else if(tan > -1.2d)
                direction = MapDirection.NORTHWEST;
            else
                direction = MapDirection.NORTH;
        }

        orientation = direction;
        return direction;
    }
}
