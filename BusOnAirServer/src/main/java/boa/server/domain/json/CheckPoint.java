package boa.server.domain.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "checkpoint")
public class CheckPoint {
    private Integer id;
    private Integer dt;
    private Coordinate latLon;
    private Integer from;
    private Integer towards;
    private Integer next;
    private Integer prev;

    public CheckPoint() {
    }

    public CheckPoint(Integer id, Integer dt, Coordinate latLon, Integer from,
                      Integer towards, Integer next, Integer prev) {
        super();
        this.id = id;
        this.dt = dt;
        this.latLon = latLon;
        this.from = from;
        this.towards = towards;
        this.next = next;
        this.prev = prev;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public Coordinate getLatLon() {
        return latLon;
    }

    public void setLatLon(Coordinate latLon) {
        this.latLon = latLon;
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

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public Integer getPrev() {
        return prev;
    }

    public void setPrev(Integer prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (!(other instanceof CheckPoint))
            return false;

        CheckPoint otherCheckPoint = (CheckPoint) other;

        return getId() == otherCheckPoint.getId();

    }

}
