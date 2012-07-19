package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "route" )
public class Route
{
    private long id;
    private String line;
    private long from;
    private long towards;

    
    public Route()
    {
    }

    public Route(long id, String line, long from, long towards) {
		super();
		this.id = id;
		this.line = line;
		this.from = from;
		this.towards = towards;
	}

	public Route( boa.server.domain.Route r )
    {
    	this(r.getId(),r.getLine(),r.getFrom().getId(),r.getTowards().getId());
    }
    
    public long getId(){
    	return id;
    }
    
    public void setId(long id){
    	this.id = id;
    }
    
    public String getline(){
    	return line;
    }
    
    public void setline(String line){
    	this.line = line;
    }

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTowards() {
		return towards;
	}

	public void setTowards(long towards) {
		this.towards = towards;
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", line=" + line + ", from=" + from
				+ ", towards=" + towards + "]";
	}    
	
	
}
