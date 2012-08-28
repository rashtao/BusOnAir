package boa.server.importer.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Routes{
//    @XmlElement( name = "stationsObjectsList" )
    public List<Route> routesObjectsList;

	public Routes() {};

	public Routes(Collection<Route> routesObjectsList) {
		super();
		this.routesObjectsList = (routesObjectsList != null) ? new LinkedList<Route>(routesObjectsList) : null;
	}
}
