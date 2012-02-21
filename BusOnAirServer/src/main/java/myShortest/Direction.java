package myShortest;

import utils.GeoUtil;
import domain.Station;
import domain.Stop;

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
		Station s = stop.getStazione();
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
	
}
