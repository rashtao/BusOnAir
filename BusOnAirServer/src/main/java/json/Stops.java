package json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stops
{
    @XmlElement( name = "stoplist" )
    List<Stop> stoplist = new ArrayList<Stop>();

    public Stops()
    {
    }

    public void add( Stop r )
    {
        stoplist.add( r );
    }

}
