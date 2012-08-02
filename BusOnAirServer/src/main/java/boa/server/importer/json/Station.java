package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name = "station" )
public class Station
{
    private long id;
    private String name;
    private Coordinate latLon;

    public Station(){
    }

    public Station( long id, String name, Coordinate latLon )
    {
    	super();
        this.id = id;
        this.name = name;
        this.latLon = latLon;
    }

    public Station( boa.server.domain.Station s )
    {
   		this(s.getId(), s.getName(), new Coordinate(s.getLatitude(), s.getLongitude()));
    }
    
    public long getId(){
    	return id;
    }
    
    public void setId(long id){
    	this.id = id;
    }
    
    public String getName(){
    	return name;
    }
    
    public void setName(String name){
    	this.name = name;
    }
    
    public Coordinate getLatLon(){
        return latLon;
    }

    public void setLatLon( Coordinate latLon ){
        this.latLon = latLon;
    }
}
