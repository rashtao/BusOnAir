package boa.server.webapp.webappjson;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Coordinate
{

    private double lat;
    private double lon;

    public Coordinate()
    {
        super();
    }

    public Coordinate( double lat, double lon )
    {
        super();
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat( double lat )
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLon( double lon )
    {
        this.lon = lon;
    }

    @Override
    public String toString()
    {
        return "Coordinate [lat=" + lat + ", lon=" + lon + "]";
    }

}
