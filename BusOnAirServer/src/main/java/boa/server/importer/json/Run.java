package boa.server.importer.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "run" )
public class Run
{
    private Integer id;
    private Long route;
    private Integer firstStop;
    private Long firstCheckPoint;
        
	public Run() {
	}

	public Run(Integer id, Long route, Integer firstStop, Long firstCheckPoint) {
		super();
		this.id = id;
		this.route = route;
		this.firstStop = firstStop;
		this.firstCheckPoint = firstCheckPoint;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getRoute() {
		return route;
	}

	public void setRoute(Long route) {
		this.route = route;
	}

	public Integer getFirstStop() {
		return firstStop;
	}

	public void setFirstStop(Integer firstStop) {
		this.firstStop = firstStop;
	}

	public Long getFirstCheckPoint() {
		return firstCheckPoint;
	}

	public void setFirstCheckPoint(Long firstCheckPoint) {
		this.firstCheckPoint = firstCheckPoint;
	}    
	
    @Override
    public boolean equals(Object other){
        if (this == other)
        	return true;
        
        if (!(other instanceof Run)) 
        	return false;
        
        Run otherRun = (Run) other;
        
        if(getId() != otherRun.getId())
        	return false;
        
        return true;
    }

	@Override
	public String toString() {
		return "Run [id=" + id + ", route=" + route + ", firstStop="
				+ firstStop + ", firstCheckPoint=" + firstCheckPoint + "]";
	}

}
