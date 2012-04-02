package boa.server.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class RoutesObjects
{
    @XmlElement( name = "routesObjectsList" )
    List<Route> routelist = new ArrayList<Route>();

    public RoutesObjects()
    {
    }

    public void add( Route r )
    {
        routelist.add(r);
    }

    public void add( boa.server.domain.Route r )
    {
        routelist.add(new Route(r));
    }
}
