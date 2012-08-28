package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Coordinate
{

    private Double lat;
    private Double lon;

    public Coordinate(){
    }

	public Coordinate(Double lat, Double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	@Override
	public String toString() {
		return "Coordinate [lat=" + lat + ", lon=" + lon + "]";
	}

}
