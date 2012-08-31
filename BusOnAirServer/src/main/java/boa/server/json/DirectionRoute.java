package boa.server.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "directionRoute" )
public class DirectionRoute
{
    private String departure;
    private String arrival;

    public DirectionRoute( )
    {        
    }

    private DirectionRoute(int departure, int arrival){
    	super();
		setDeparture(departure);
		setArrival(arrival);
    }   
    
    public DirectionRoute(boa.server.domain.Stop dep, boa.server.domain.Stop arr){
    	this(dep.getId(), arr.getId());
    }

    public String getDeparture(){
        return "/stops/" + departure;
    }

    public void setDeparture( Integer departure ){
        this.departure = departure.toString();
    }

    public void setDeparture( String departure ){
        this.departure = departure;
    }

    public String getArrival(){
        return "/stops/" + arrival;
    }

    public void setArrival( Integer arrival){
        this.arrival = arrival.toString();
    }
    
    public void setArrival( String arrival){
        this.arrival = arrival;
    }
    
    public int getDepId(){
    	return Integer.parseInt(departure);
    }
    
    public int getArrId(){
    	return Integer.parseInt(arrival);
    }
    
    @Override
	public String toString(){
		return ("\nDirectionRoute: " +
				"\n\tdep: " + departure + 
				"\n\tarr: " + arrival); 
    }	    
}
