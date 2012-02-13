package json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stations
{
    @XmlElement( name = "stationlist" )
    List<Station> routelist = new ArrayList<Station>();

    public Stations()
    {
    }

    public void add( Station s )
    {
        routelist.add( s );
    }

}
