package Project;

import java.util.ArrayList;

public class Wall implements IMapElement{

    private Vector2d position;
    private int health = 2;
    ArrayList<IMapElementDestroyedObserver> destroyObservers = new ArrayList<>();

    public Wall(Vector2d position){
        this.position = position;
    }

    public Vector2d getPosition(){
        return position;
    }

    public void decreaseHealth(int amount){
        health -= amount;
        if(health <= 0)
            destroy();
    }

    public int getHealth(){
        return health;
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
}
