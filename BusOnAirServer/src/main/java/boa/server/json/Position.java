package boa.server.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Position
{

    private Coordinate latLon;
    private int time;

    public Position(){
		super();
	}

	public Position(Coordinate latLon, int time) {
		super();
		this.latLon = latLon;
		this.time = time;
	}

	public Coordinate getLatLon() {
		return latLon;
	}

	public void setLatLon(Coordinate latLon) {
		this.latLon = latLon;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

    
}
