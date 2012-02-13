package json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Routes
{
    @XmlElement( name = "routelist" )
    List<Route> routelist = new ArrayList<Route>();

    public Routes()
    {
    }

    public void add( Route r )
    {
        routelist.add( r );
    }

}
