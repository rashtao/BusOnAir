package json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "directionRoute" )
public class DirectionRoute
{
    private String departure;
    private String arrival;
    
    public DirectionRoute( )
    {        
    }

    public DirectionRoute(String departure, String arrival){
    	super();
		this.departure = departure;
		this.arrival = arrival;
    }   
    
    public DirectionRoute(Stop dep, Stop arr){
    	this("/stops/" + dep.getStopId(), "/stops/" + arr.getStopId());
    }
    
    public DirectionRoute(domain.Stop dep, domain.Stop arr){
    	this("/stops/" + dep.getId(), "/stops/" + arr.getId());
    }

    public String getDeparture(){
        return departure;
    }

    public void setDeparture( String departure ){
        this.departure = departure;
    }

    public String getArrival(){
        return arrival;
    }

    public void setArrival( String arrival){
        this.arrival = arrival;
    }
    
    @Override
	public String toString(){
		return ("\nDirectionRoute: " +
				"\n\tdep: " + departure + 
				"\n\tarr: " + arrival); 
  
    }	    
}
