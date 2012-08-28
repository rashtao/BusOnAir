package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "route" )
public class Route
{
    private Long id;
    private String line;
    private Long from;
    private Long towards;

    
    public Route(){
    }

    
	public Route(Long id, String line, Long from, Long towards) {
		super();
		this.id = id;
		this.line = line;
		this.from = from;
		this.towards = towards;
	}

	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getLine() {
		return line;
	}


	public void setLine(String line) {
		this.line = line;
	}


	public Long getFrom() {
		return from;
	}


	public void setFrom(Long from) {
		this.from = from;
	}


	public Long getTowards() {
		return towards;
	}


	public void setTowards(Long towards) {
		this.towards = towards;
	}
    
    @Override
    public boolean equals(Object other){
        if (this == other)
        	return true;
        
        if (!(other instanceof Route)) 
        	return false;
        
        Route otherRoute = (Route) other;
        
        if(getId() != otherRoute.getId())
        	return false;
        
        return true;
    }


	@Override
	public String toString() {
		return "Route [id=" + id + ", line=" + line + ", from=" + from
				+ ", towards=" + towards + "]";
	}
    
    
}
