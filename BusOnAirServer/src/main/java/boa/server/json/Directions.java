package boa.server.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Directions
{
    @XmlElement( name = "directionlist" )
    List<Direction> directionsList = new ArrayList<Direction>();

    public Directions()
    {
    }

    public void add( Direction direction )
    {
        directionsList.add( direction );
    }

    public List<Direction> getDirectionsList(){
    	return directionsList;
    }
}
