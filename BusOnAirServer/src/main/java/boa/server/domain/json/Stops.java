package boa.server.domain.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Stops{
    public List<Stop> stopsObjectsList;

	public Stops() {
		stopsObjectsList = new LinkedList<Stop>();
	};

	public Stops(Collection<Stop> stopsObjectsList) {
		super();
		this.stopsObjectsList = (stopsObjectsList != null) ? new LinkedList<Stop>(stopsObjectsList) : new LinkedList<Stop>();
	}
}
