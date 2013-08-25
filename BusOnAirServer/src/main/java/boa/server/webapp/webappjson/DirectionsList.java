package boa.server.webapp.webappjson;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement( name = "List" )
public class DirectionsList
{
    @XmlElement( name = "directionslist" )
    List<Directions> directionsList = new ArrayList<Directions>();

    public DirectionsList()
    {
    }

    public void add( Directions directions )
    {
        directionsList.add( directions );
    }

}
