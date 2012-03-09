package json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "route" )
public class Route
{
    private int id;
    private String routeLine;
    private String from;
    private String towards;
    private String url;

    
    public Route()
    {
    }

    private Route( int id, String routeLine)
    {
        this.id = id;
        this.routeLine = routeLine;
    }

    public Route( domain.Route r )
    {
    	this(r.getId(),r.getLine());
    	setFrom(r.getFrom().getUrl());
    	setTowards(r.getTowards().getUrl());
    	setUrl(r.getUrl());
    }
    
    public int getId(){
    	return id;
    }
    
    public void setId(int id){
    	this.id = id;
    }
    
    public String getRouteLine(){
    	return routeLine;
    }
    
    public void setRouteLine(String routeLine){
    	this.routeLine = routeLine;
    }

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTowards() {
		return towards;
	}

	public void setTowards(String towards) {
		this.towards = towards;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    
    
}
