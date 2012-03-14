package boa.server.routing;

import boa.server.domain.Station;
import boa.server.domain.Stop;
import boa.server.domain.utils.GeoUtil;

public class Direction {
	private Stop stop;
	private double lat;
	private double lon;
	
	public Direction(Stop stop, double lat, double lon){
		setStop(stop);
		this.lat = lat;
		this.lon = lon;
	}
		
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public Stop getStop() {
		return stop;
	}
	
	public void setStop(Stop stop) {
		this.stop = stop;
	}
	
	public double getDistance(){
		Station s = stop.getStation();
		return GeoUtil.getDistance2(lat, lon, s.getLatitude(), s.getLongitude());
	}
	
	public int getWalkTime(){
		return (int) (getDistance() / 5.0 * 60);
	}
	
	public int getArrivalTime(){
		return stop.getTime() + getWalkTime();
	}

	public int getNumChanges(){
		return stop.numeroCambi;
	}
	
	public int getMinChangeTime(){
		return stop.minChangeTime;
	}
	
	public int getWalkDistance(){
		return (int)(getDistance() * 1000.0) + stop.walkDistance;
	}
	
	public int departureTime(){
		return stop.departureTime;
	}
	
	
  @Override
	public String toString(){
		return ("Direction: " +
			"\n\tstop: " + stop + 
			"\n\tlatitude: " + lat +
			"\n\tlongitude: " + lon);
  		
  }
	
	
}
