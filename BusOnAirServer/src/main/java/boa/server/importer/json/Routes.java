package boa.server.importer.json;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "List" )
public class Routes{
    public List<Route> routesObjectsList;

	public Routes() {
		routesObjectsList = new LinkedList<Route>();
	};

	public Routes(Collection<Route> routesObjectsList) {
		super();
		this.routesObjectsList = (routesObjectsList != null) ? new LinkedList<Route>(routesObjectsList) : new LinkedList<Route>();
	}
}
