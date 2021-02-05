package Project;

import java.util.ArrayList;

public interface IMapElement {

    Vector2d position = null;
    ArrayList<IMapElementDestroyedObserver> destroyObservers = new ArrayList<>();

    Vector2d getPosition();

    void addDestroyObserver(IMapElementDestroyedObserver observer);

    void removeDestroyObserver(IMapElementDestroyedObserver observer);

    void destroy();


}
