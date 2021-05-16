package main.java.models;

import main.java.webserver.JDBCSocket;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This info has been separated from the regular Potion item due to the length of the data in the fields causing
 * issues. This information will be called and constructed as requested by the user.
 * @author areed
 */
public class PotionInfo {
    private final String name;
    private final Integer id;
    private final String description;

    /**
     * Basic constructor
     * @param id Integer id of the potion on the potion table.
     * @throws SQLException if the ID does not exist in the potion table or the query violates the rules of the table
     *                      an error will occur.
     */
    public PotionInfo(Integer id) throws SQLException {
        this.id = id;
        JDBCSocket jdbcSocket = new JDBCSocket();
        ResultSet info = jdbcSocket.getPotionInfo(id);
        info.next();
        this.description = info.getString(1);
        this.name = info.getString(2);
        jdbcSocket.close();
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() { return name; }
}
