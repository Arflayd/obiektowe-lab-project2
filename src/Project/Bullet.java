package Project;

import java.util.ArrayList;

public class Bullet implements IMapElement{

    private Vector2d position;
    private final MapDirection direction;
    private ArrayList<IPositionChangeObserver> changeObservers = new ArrayList<>();
    private ArrayList<IMapElementDestroyedObserver> destroyObservers = new ArrayList<>();
    private final WorldMap map;
    private boolean movedThisTurn = false;
    public boolean destroyed = false;

    public Bullet(WorldMap map, Vector2d position, MapDirection direction){
        this.position = position;
        this.direction = direction;
        this.map = map;
    }

    public Vector2d getPosition(){
        return position;
    }

    public MapDirection getDirection(){
        return direction;
    }

    public void move(){

        Vector2d newPosition = position.add(direction.toUnitVector());
        if(map.canMoveTo(newPosition)){
            updatePosition(newPosition);
        }
        else if(map.isInsideMap(newPosition)){
            collide(newPosition);
        }
        else{
            destroy();
        }
    }

    private void updatePosition(Vector2d newPosition){

        Vector2d oldPosition = position;
        position = newPosition;

        for(IPositionChangeObserver observer : changeObservers){
            observer.positionChanged(this, oldPosition, newPosition);
        }

        movedThisTurn = true;
    }

    private void collide(Vector2d position){
        ArrayList<IMapElement> collidingObjects = map.objectsAt(position);

        IMapElement collidedObject;
        collidedObject = collidingObjects.get(0);

        if(collidedObject.getClass() == Bullet.class){
            Bullet bullet = (Bullet) collidedObject;
            if(bullet.movedThisTurn){
                bullet.destroy();
                bullet.destroyed = true;
            }else{
                updatePosition(position);
                return;
            }
        }
        else if(collidedObject.getClass() == Wall.class){
            Wall wall = (Wall)collidedObject;
            wall.decreaseHealth(1);
        }
        else{
            Tank tank = (Tank)collidedObject;
            tank.decreaseHealth(1);
        }

        destroyed = true;
        destroy();
    }

    public void addChangeObserver(IPositionChangeObserver observer){
        changeObservers.add(observer);
    }

    public void removeChangeObserver(IPositionChangeObserver observer){
        changeObservers.remove(observer);
    }

    public void addDestroyObserver(IMapElementDestroyedObserver observer){
        destroyObservers.add(observer);
    }

    public void removeDestroyObserver(IMapElementDestroyedObserver observer){
        destroyObservers.remove(observer);
    }

    @Override
    public void destroy(){
        for(IMapElementDestroyedObserver observer : destroyObservers)
            observer.mapElementDestroyed(this, position);
    }

    public void setMovedThisTurn(boolean moved){
        movedThisTurn = moved;
    }
}
