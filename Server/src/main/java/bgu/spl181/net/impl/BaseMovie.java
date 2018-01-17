package bgu.spl181.net.impl;

public class BaseMovie {

    protected int id;
    protected String name;

    public BaseMovie(String name, int id) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
