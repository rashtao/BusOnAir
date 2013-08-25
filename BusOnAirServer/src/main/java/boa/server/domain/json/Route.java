package boa.server.domain.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "route")
public class Route {
    private Integer id;
    private String line;
    private Integer from;
    private Integer towards;


    public Route() {
    }


    public Route(Integer id, String line, Integer from, Integer towards) {
        super();
        this.id = id;
        this.line = line;
        this.from = from;
        this.towards = towards;
    }

    public Route(boa.server.domain.Route r) {
        this(r.getId(), r.getLine(), r.getFrom().getId(), r.getTowards().getId());
    }

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getLine() {
        return line;
    }


    public void setLine(String line) {
        this.line = line;
    }


    public Integer getFrom() {
        return from;
    }


    public void setFrom(Integer from) {
        this.from = from;
    }


    public Integer getTowards() {
        return towards;
    }


    public void setTowards(Integer towards) {
        this.towards = towards;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (!(other instanceof Route))
            return false;

        Route otherRoute = (Route) other;

        return getId() == otherRoute.getId();

    }


    @Override
    public String toString() {
        return "Route [id=" + id + ", line=" + line + ", from=" + from
                + ", towards=" + towards + "]";
    }


}
