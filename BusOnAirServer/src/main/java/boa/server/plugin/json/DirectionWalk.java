package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "directionWalk" )
public class DirectionWalk
{

    private boolean isChange;
    private int duration;
    private int distance;
    private Coordinate departure;
    private Coordinate arrival;
//    public int stazId;
    
    public DirectionWalk()
    {
    }

    public DirectionWalk( boolean isChange, int duration, int distance, Coordinate departure, Coordinate arrival ){
    	super();
    	this.isChange = isChange;
    	this.duration = duration;
    	this.distance = distance;
    	this.departure = departure;
    	this.arrival = arrival;
    }

    public DirectionWalk( boolean isChange, int duration, int distance, Coordinate departure, Coordinate arrival, int stazId){
    	this(isChange, duration, distance, departure, arrival);
//    	this.stazId = stazId;
    }

	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Coordinate getDeparture() {
		return departure;
	}

	public void setDeparture(Coordinate departure) {
		this.departure = departure;
	}

	public Coordinate getArrival() {
		return arrival;
	}

	public void setArrival(Coordinate arrival) {
		this.arrival = arrival;
	}
	
    @Override
	public String toString(){
		return ("\nDirectionWalk: " +
				"\n\tisChange: " + isChange + 
				"\n\tduration: " + duration + 
				"\n\tdistance: " + distance +
				"\n\tlatdep: " + departure.getLat() + 
				"\n\tlondep: " + departure.getLon() + 
				"\n\tlatarr: " + arrival.getLat() + 
				"\n\tlonarr: " + arrival.getLon());
//		+				"\n\tstazId: " + stazId); 
  
    }	
   
}
