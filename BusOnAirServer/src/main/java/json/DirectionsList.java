package json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
