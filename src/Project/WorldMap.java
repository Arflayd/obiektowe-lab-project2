package Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class WorldMap implements IPositionChangeObserver, IMapElementDestroyedObserver {

    public final int mapSize;

    private final HashMap<Vector2d, ArrayList<IMapElement>> mapElements = new HashMap<>();

    private final Random random = new Random();

    public WorldMap(int mapSize) {
        this.mapSize = mapSize;
    }

    public Vector2d randomUnoccupiedPosition(Vector2d playerPosition, boolean excludeAreaAroundPlayer){

        // Try 'maxAttempts' times to find a random unoccupied position, if that fails
        // try to find any free position, if there are none - return null
        int maxAttempts = 100;

        Vector2d position;
        int attempts = 0;
        if(!excludeAreaAroundPlayer){
            do{
                position = new Vector2d(random.nextInt(mapSize), random.nextInt(mapSize));
                attempts++;
            }while((mapElements.containsKey(position)) && attempts < maxAttempts);

            if(attempts < maxAttempts)
                return position;

            attempts = 0;
            // If we couldn't find a random free position, we'll scan the whole area for a free position
            maxAttempts = mapSize * mapSize;

            do{
                if(position.x == mapSize-1){
                    if(position.y == mapSize-1)
                        position = new Vector2d(0, 0);
                    else
                        position = new Vector2d(0, position.y+1);
                }
                else{
                    position = new Vector2d(position.x + 1, position.y);
                }
                attempts++;
            }while((mapElements.containsKey(position)) && attempts < maxAttempts);
        }
        else{
            Vector2d playerTopLeft = playerPosition.add(new Vector2d(-2,-2));
            Vector2d playerBottomRight = playerPosition.add(new Vector2d(3,3));
            do{
                position = new Vector2d(random.nextInt(mapSize), random.nextInt(mapSize));
                attempts++;
            }while((mapElements.containsKey(position)) || (position.follows(playerTopLeft) && position.precedes(playerBottomRight)) && attempts < maxAttempts);

            if(attempts < maxAttempts)
                return position;

            attempts = 0;
            // If we couldn't find a random free position, we'll scan the whole map for a free position,
            // but since the area is flipped, we need to avoid it
            maxAttempts = mapSize * mapSize;

            do{
                if(position.x == mapSize-1){
                    if(position.y == mapSize-1)
                        position = new Vector2d(0, 0);
                    else
                        position = new Vector2d(0, position.y+1);
                }
                else if(position.x >= playerTopLeft.x && position.x <= playerBottomRight.x && position.y >= playerTopLeft.y && position.y <= playerBottomRight.y){
                    position = new Vector2d(playerBottomRight.x + 1, position.y);
                }
                else{
                    position = new Vector2d(position.x + 1, position.y);
                }

                attempts++;
            }while(mapElements.containsKey(position) && attempts < maxAttempts);

        }

        if(attempts < maxAttempts)
            return position;

        // There is no empty position
        return null;
    }


    public boolean isOccupied(Vector2d position){
        return mapElements.get(position) != null;
    }

    public ArrayList<IMapElement> objectsAt(Vector2d position){
        if(mapElements.get(position) == null)
            return null;
        return mapElements.get(position);
    }

    public void place(IMapElement mapElement){

        Vector2d position = mapElement.getPosition();

        if(mapElements.get(position) == null)
            mapElements.put(position, new ArrayList<>());

        mapElements.get(position).add(mapElement);

    }

    public boolean canMoveTo(Vector2d position){
        if(position.x < 0 || position.x >= mapSize || position.y < 0 || position.y >= mapSize)
            return false;
        if(!isOccupied(position))
            return true;
        return false;
    }

    public void positionChanged(IMapElement mapElement, Vector2d oldPosition, Vector2d newPosition){

        if(mapElement.getClass() == Tank.class || mapElement.getClass() == Wall.class) {
            mapElements.remove(oldPosition);
        }
        else{
            if(mapElements.get(oldPosition).size() == 1){
                mapElements.remove(oldPosition);
            }
            else{
                mapElements.get(oldPosition).remove(mapElement);
            }
        }

        place(mapElement);
    }

    public boolean isInsideMap(Vector2d position){
        return !(position.x < 0 || position.x >= mapSize || position.y < 0 || position.y >= mapSize);
    }

    public void mapElementDestroyed(IMapElement mapElement, Vector2d position) {
        if(mapElements.get(position).size() > 1)
            mapElements.get(position).remove(mapElement);
        else
            mapElements.remove(position);
    }
}
