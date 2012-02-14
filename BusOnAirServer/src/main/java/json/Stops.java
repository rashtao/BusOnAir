package json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stops
{
    @XmlElement( name = "stoplist" )
    List<String> stoplist = new ArrayList<String>();

    public Stops()
    {
    }

    public void add( Stop r )
    {
        stoplist.add("/stops/" + r.getStopId() );
    }

}
