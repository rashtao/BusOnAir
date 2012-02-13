package json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "stop" )
public class Stop
{
    private int stopId;

    public Stop()
    {
    }

    public Stop( int stopId )
    {
        this.stopId = stopId;
    }

    public Stop( domain.Stop r )
    {
    	this(r.getId());
    }
}
