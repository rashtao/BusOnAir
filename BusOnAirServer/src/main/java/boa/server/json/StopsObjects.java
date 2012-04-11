package boa.server.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class StopsObjects
{
    @XmlElement( name = "stopsObjectsList" )
    List<Stop> stoplist = new ArrayList<Stop>();

    public StopsObjects()
    {
    }

    public void add(Stop s)
    {
        stoplist.add(s);
    }

    public void add( boa.server.domain.Stop s )
    {
        stoplist.add(new Stop(s));
    }

}
