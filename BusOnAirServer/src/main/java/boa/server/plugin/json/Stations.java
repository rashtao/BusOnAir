package boa.server.plugin.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stations
{
    @XmlElement( name = "stationlist" )
    List<String> routelist = new ArrayList<String>();

    public Stations()
    {
    }

    public void add( Station s )
    {
        routelist.add(s.getUrl());
    }

    public void add( boa.server.domain.Station s )
    {
        routelist.add(s.getUrl());
    }

}
