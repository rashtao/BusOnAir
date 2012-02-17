package json;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "directions" )
public class Direction
{

	private int departureTime;
	private int arrivalTime;
	private int numChanges;
	private int minChangeTime;
	private List<DirectionRoute> dirRoute;
    private List<DirectionWalk> dirWalk;

    public Direction()
    {
    }
        
    public Direction(int departureTime, int arrivalTime, int numChanges,
			int minChangeTime, List<DirectionRoute> dirRoute,
			List<DirectionWalk> dirWalk) {
		super();
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.numChanges = numChanges;
		this.minChangeTime = minChangeTime;
		this.dirRoute = dirRoute;
		this.dirWalk = dirWalk;
	}

    public List<DirectionRoute> getRoutes()
    {
        return dirRoute;
    }

    public List<DirectionWalk> getWalks()
    {
        return dirWalk;
    }

    public void setRoutes( List<DirectionRoute> dirRoute )
    {
        this.dirRoute = dirRoute;
    }

    public void setWalks( List<DirectionWalk> dirWalk )
    {
        this.dirWalk = dirWalk;
    }
    
    public int getDepartureTime(){
    	return departureTime;
    }
    
    void setDepartureTime(int departureTime){
    	this.departureTime = departureTime;
    }

    public int getArrivalTime(){
    	return arrivalTime;
    }
    
    void setArrivalTime(int arrivalTime){
    	this.arrivalTime = arrivalTime;
    }

    public int getNumChanges(){
    	return numChanges;
    }
    
    void getNumChanges(int numChanges){
    	this.numChanges = numChanges;
    }

    public int setMinChangeTime(){
    	return minChangeTime;
    }
    
    void setMinChangeTime(int minChangeTime){
    	this.minChangeTime = minChangeTime;
    }

}
