package Project;

public interface IPositionChangeObserver{

    void positionChanged(IMapElement mapElement, Vector2d oldPosition, Vector2d newPosition);

}
