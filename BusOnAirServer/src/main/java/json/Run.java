package json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "run" )
public class Run
{
    private int runId;

    public Run()
    {
    }

    public Run( int runId )
    {
        this.runId = runId;
    }

    public Run( domain.Run r )
    {
    	this(r.getId());
    }
    
    public int getRunId(){
    	return runId;
    }
    
    public void setRunId(int runId){
    	this.runId = runId;
    }
}
