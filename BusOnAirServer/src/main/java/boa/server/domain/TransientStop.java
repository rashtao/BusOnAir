package boa.server.domain;

public class TransientStop extends Stop {
    protected static int count = 0;
    protected int id;
    protected int time;
    protected String type = "TransientStop";
    protected Stop nis;
    protected Station stazione;

    public TransientStop() {
        super();
        id = count++;
    }

    @Override
    public Stop getNextInStation() {
        return nis;
    }

    @Override
    public Integer getId() {
        return (Integer) id;
    }

    @Override
    public void setNextInStation(Stop stop) {
        nis = stop;
    }

    @Override
    public Stop getNextInRun() {
        return null;
    }


    @Override
    public String getType() {
        return type;
    }

    @Override
    public Station getStation() {
        return stazione;
    }

    public void setStazione(Station stazione) {
        this.stazione = stazione;
    }

    @Override
    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public Integer getTime() {
        return (Integer) time;
    }

    @Override
    public String toString() {
        return ("TransientStop: " +
                "\n\ttime: " + getTime());
    }

    @Override
    public boolean equals(final Object otherStop) {
        if (otherStop == null)
            return false;

        if (otherStop instanceof TransientStop) {
            return (id == ((TransientStop) otherStop).getId());
        }
        return false;
    }


}
