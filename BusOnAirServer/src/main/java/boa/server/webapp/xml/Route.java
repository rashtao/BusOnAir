package boa.server.webapp.xml;

import java.util.List;

public class Route
{

    public String type;
    public String direction;
    public String towards;
    public String routeId;
    public String routeName;
    public Double[] deptTimes;
    public List<Stop> stopList;

    public Route()
    {
        super();
    }

    public List<Stop> getStopList()
    {
        return stopList;
    }

}
