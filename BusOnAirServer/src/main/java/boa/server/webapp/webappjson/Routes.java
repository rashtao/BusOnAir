package boa.server.webapp.webappjson;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

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
