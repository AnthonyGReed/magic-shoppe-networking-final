package main.java.models;

import main.java.webserver.JDBCSocket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This info has been separated from the regular Wonder item due to the length of the data in the fields causing
 * issues. This information will be called and constructed as requested by the user.
 * @author areed
 */
public class ScrollInfo {
    private final Integer id;
    private final String school;
    private final Boolean concentration;
    private final String range;
    private final ArrayList<String> classes;
    private final String castingTime;
    private final String components;
    private final String duration;
    private final Boolean ritual;
    private final String name;

    /**
     * Basic constructor
     * @param id Integer id of the item on the item table.
     * @throws SQLException if the ID does not exist in the item table or the query violates the rules of the table
     *                      an error will occur.
     */
    public ScrollInfo(Integer id) throws SQLException {
        this.id = id;
        JDBCSocket jdbcSocket = new JDBCSocket();
        ResultSet info = jdbcSocket.getScrollInfo(id);
        info.next();
        this.school = info.getString(1);
        this.concentration = info.getBoolean(2);
        this.range = info.getString(3);
        this.classes = new ArrayList<>(Arrays.asList(info.getString(4).split(",")));
        this.ritual = info.getBoolean(5);
        this.castingTime = info.getString(6);
        this.components = info.getString(7);
        this.duration = info.getString(8);
        this.name = info.getString(9);
        jdbcSocket.close();
    }

    public Integer getId() {
        return id;
    }

    public String getSchool() {
        return school;
    }

    public Boolean getConcentration() {
        return concentration;
    }

    public String getRange() {
        return range;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public String getCastingTime() {
        return castingTime;
    }

    public String getComponents() {
        return components;
    }

    public String getDuration() {
        return duration;
    }

    public Boolean getRitual() {
        return ritual;
    }

    public String getName() { return name; }
}
