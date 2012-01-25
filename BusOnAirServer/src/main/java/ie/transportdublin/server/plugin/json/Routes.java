package ie.transportdublin.server.plugin.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Routes
{
    @XmlElement( name = "routelist" )
    List<RouteStop> routelist = new ArrayList<RouteStop>();

    public Routes()
    {
    }

    public void add( RouteStop stop )
    {
        routelist.add( stop );
    }

}
