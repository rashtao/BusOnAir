package boa.server.plugin.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement( name = "List" )
public class CheckPoints
{
    @XmlElement( name = "checkpointlist" )
    public List<String> checkpointlist = new ArrayList<String>();

    public CheckPoints()
    {
    }

    public void add( CheckPoint cp)
    {
    	checkpointlist.add(cp.getUrl());
    }

    public void add( boa.server.domain.CheckPoint cp)
    {
    	checkpointlist.add(cp.getUrl());
    }

}
