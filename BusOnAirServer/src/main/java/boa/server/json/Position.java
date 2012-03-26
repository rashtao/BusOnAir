package boa.server.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Position
{

    private Coordinate latLon;
    private long time;

    public Position(){
		super();
	}

	public Position(Coordinate latLon, long time) {
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

    
}
