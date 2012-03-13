package boa.server.json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Routes
{
    @XmlElement( name = "routelist" )
    List<String> routelist = new ArrayList<String>();

    public Routes()
    {
    }

    public void add( Route r )
    {
        routelist.add(r.getUrl());
    }

    public void add( boa.server.domain.Route r )
    {
        routelist.add(r.getUrl());
    }

}
