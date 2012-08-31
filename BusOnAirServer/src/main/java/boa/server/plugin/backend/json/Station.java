package boa.server.plugin.backend.json;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement( name = "station" )
public class Station
{
    private Integer id;
    private String name;
    private Coordinate latLon;

    public Station(){
    }

    public Station( Integer id, String name, Coordinate latLon )
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
    
    public Integer getId(){
    	return id;
    }
    
    public void setId(Integer id){
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
    
    @Override
    public boolean equals(Object other){
        if (this == other)
        	return true;
        
        if (!(other instanceof Station)) 
        	return false;
        
        Station otherStation = (Station) other;
        
        if(getId() != otherStation.getId())
        	return false;
        
        return true;
    }

	@Override
	public String toString() {
		return "Station [id=" + id + ", name=" + name + ", latLon=" + latLon
				+ "]";
	}
    
}
