package boa.server.webapp.xml;

import java.util.ArrayList;
import java.util.List;

public class Routes {
    private List<Route> routeList;

    public Routes() {
        super();
        routeList = new ArrayList<Route>();
    }

    public List<Route> getRouteList() {
        return routeList;
    }

}
