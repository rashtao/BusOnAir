package json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "route" )
public class Route
{
    private int routeId;
    private String routeLine;
    private String from;
    private String towards;
    
    public Route()
    {
    }

    public Route( int routeId, String routeLine)
    {
        this.routeId = routeId;
        this.routeLine = routeLine;
    }

    public Route( domain.Route r )
    {
    	this(r.getId(),r.getLine());
    }
    
    public int getRouteId(){
    	return routeId;
    }
    
    public void setRouteId(int routeId){
    	this.routeId = routeId;
    }
    
    public String getRouteLine(){
    	return routeLine;
    }
    
    public void setRouteLine(String routeLine){
    	this.routeLine = routeLine;
    }
    
    public String getFrom(){
    	return from;
    }
    
    public void setFrom(String from){
    	this.from = from;
    }
    
    public String getTowards(){
    	return towards;
    }
    
    public void setTowards(String towards){
    	this.towards = towards;
    }
}
