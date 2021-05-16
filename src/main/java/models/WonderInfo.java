package main.java.models;

import main.java.webserver.JDBCSocket;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This info has been separated from the regular Wonder item due to the length of the data in the fields causing
 * issues. This information will be called and constructed as requested by the user.
 * @author areed
 */
public class WonderInfo {
    private final Integer id;
    private final String description;
    private final Boolean attunement;
    private final String limits;
    private final String name;

    /**
     * Basic constructor
     * @param id Integer id of the item on the item table.
     * @throws SQLException if the ID does not exist in the item table or the query violates the rules of the table
     *                      an error will occur.
     */
    public WonderInfo(Integer id) throws SQLException {
        this.id = id;
        JDBCSocket jdbcSocket = new JDBCSocket();
        ResultSet info = jdbcSocket.getWonderInfo(id);
        info.next();
        this.description = info.getString(1);
        this.attunement = info.getBoolean(2);
        this.limits = info.getString(3);
        this.name = info.getString(4);
        jdbcSocket.close();
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getAttunement() {
        return attunement;
    }

    public String getLimits() {
        return limits;
    }

    public String getName() { return name; }
}
