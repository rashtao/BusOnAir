package json;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Runs
{
    @XmlElement( name = "runlist" )
    List<Run> runlist = new ArrayList<Run>();

    public Runs()
    {
    }

    public void add( Run r )
    {
        runlist.add( r );
    }

}
