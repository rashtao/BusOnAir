package json;

import ie.transportdublin.server.plugin.json.Coordinate;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name = "station" )
public class Station
{
    private int stationId;
    private String stationName;
    private Coordinate latLon;

    public Station()
    {
    }

    public Station( int stationId, String stationName, Coordinate latLon )
    {
    	super();
        this.stationId = stationId;
        this.stationName = stationName;
        this.latLon = latLon;
    }

    public Station( domain.Station s )
    {
   		this(s.getId(), s.getName(), new Coordinate(s.getLatitude(), s.getLongitude()));
    }
    
    public int getStationId(){
    	return stationId;
    }
    
    public void setStationId(int stationId){
    	this.stationId = stationId;
    }
    
    public String getStationName(){
    	return stationName;
    }
    
    public void setStationName(String stationName){
    	this.stationName = stationName;
    }
    
    public Coordinate getLatLon(){
        return latLon;
    }

    public void setLatLon( Coordinate latLon ){
        this.latLon = latLon;
    }   
}
