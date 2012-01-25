package ie.transportdublin.server.plugin.json;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "directions" )
public class Directions
{
    private List<DirectionsRoute> routes;
    private List<DirectionsWalk> walks;

    public Directions()
    {
    }

    @XmlElement
    public List<DirectionsRoute> getRoutes()
    {
        return routes;
    }

    @XmlElement
    public List<DirectionsWalk> getWalks()
    {
        return walks;
    }

    public Directions( List<DirectionsRoute> routes, List<DirectionsWalk> walks )
    {
        super();
        this.routes = routes;
        this.walks = walks;
    }

    public void setRoutes( List<DirectionsRoute> routes )
    {
        this.routes = routes;
    }

    public void setWalks( List<DirectionsWalk> walks )
    {
        this.walks = walks;
    }

}
