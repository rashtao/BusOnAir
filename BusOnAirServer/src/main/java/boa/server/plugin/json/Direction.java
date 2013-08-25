package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;

@XmlRootElement(name = "directions")
public class Direction {

    private int departureTime;
    private int arrivalTime;
    private int numChanges;
    private int minChangeTime;
    private int walkingDistance;
    private LinkedList<DirectionRoute> dirRoute;
    private LinkedList<DirectionWalk> dirWalk;

    public Direction() {
        dirRoute = new LinkedList<DirectionRoute>();
        dirWalk = new LinkedList<DirectionWalk>();

    }

    public Direction(int departureTime, int arrivalTime, int numChanges,
                     int minChangeTime, LinkedList<DirectionRoute> dirRoute,
                     LinkedList<DirectionWalk> dirWalk) {
        super();
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.numChanges = numChanges;
        this.minChangeTime = minChangeTime;
        this.dirRoute = dirRoute;
        this.dirWalk = dirWalk;
    }

    public LinkedList<DirectionRoute> getRoutes() {
        return dirRoute;
    }

    public LinkedList<DirectionWalk> getWalks() {
        return dirWalk;
    }

    public void setRoutes(LinkedList<DirectionRoute> dirRoute) {
        this.dirRoute = dirRoute;
    }

    public void setWalks(LinkedList<DirectionWalk> dirWalk) {
        this.dirWalk = dirWalk;
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getNumChanges() {
        return numChanges;
    }

    public void setNumChanges(int numChanges) {
        this.numChanges = numChanges;
    }

    public int setMinChangeTime() {
        return minChangeTime;
    }

    public void setMinChangeTime(int minChangeTime) {
        this.minChangeTime = minChangeTime;
    }

    public int getWalkingDistance() {
        return walkingDistance;
    }

    public void setWalkingDistance(int walkingDistance) {
        this.walkingDistance = walkingDistance;
    }

    @Override
    public String toString() {
        String out = "Direction: " +
                "\n\tdepartureTime: " + departureTime +
                "\n\tarrivalTime: " + arrivalTime +
                "\n\tnumChanges: " + numChanges +
                "\n\tminChangeTime: " + minChangeTime;

        out = out + "\n\ndirRoute:";
        for (DirectionRoute dr : dirRoute) {
            out = out + dr.toString();
        }

        out = out + "\n\ndirWalk:";
        for (DirectionWalk dw : dirWalk) {
            out = out + dw.toString();
        }

        return out;

    }
}
