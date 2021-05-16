package main.java.webserver;

import com.google.gson.Gson;
import main.java.config.NetworkConstants;
import main.java.config.RarityConfig;
import main.java.models.Shop;

import java.sql.*;

/**
 * This class creates and stores a socket to speak to the database. It also creates the prepared statements as strings
 * and executes them. This is the only class responsible for communicating with the database and it returns ResultSet
 * objects when its methods are called.
 *
 * @author areed
 */
public class JDBCSocket implements NetworkConstants {
    Connection connection;
    Statement statement;

    /**
     * This constructor uses the Network Constants to open a connection to the MYSQL database.
     * @throws SQLException failure to connect to the database will throw a SQL error
     */
    public JDBCSocket() throws SQLException {
        this.connection = DriverManager.getConnection(NetworkConstants.JDBC_CONNECTION_STRING + "/" +
                        NetworkConstants.JDBC_SCHEMA,
                NetworkConstants.JDBC_USERNAME,
                NetworkConstants.JDBC_PASSWORD);
        this.statement = connection.createStatement();
    }

    /**
     * For simple queries to the database, the string is fed to this method.
     * @param query Simple string DB queries
     * @return ResultSet from executed query
     * @throws SQLException If the query violates the rules of the database, an error will occur.
     */
    private ResultSet query(String query) throws SQLException {
        return statement.executeQuery(query);
    }

    /**
     * For more complex queries where multiple paramaters may need to be added to teh query.
     * @param query This is the simple string query. "?" will fill the spaces that need to be replaced by the params
     * @param params Some number of objects to build the query, filling in the "?"s
     * @return ResultSet of data returned from the query
     * @throws SQLException If the query violates the rules of the database, an error will occur.
     */
    private ResultSet preparedQuery(String query, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        int i = 1;
        for(Object param : params) {
            preparedStatement.setString(i, param.toString());
            i++;
        }
        return preparedStatement.executeQuery();
    }

    /**
     * Similar to the prepared statement query above, this is a prepared update, calling the execute update method
     * @param sql Simple string sql statement for updating. "?"s need to eb replaced by the params
     * @param params Some number of objects to be stored in the database, filling in the "?"s in the query
     * @throws SQLException If the update violates the rules of the database, an error will occur.
     */
    private void preparedUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for(Object param : params) {
            preparedStatement.setString(i, param.toString());
            i++;
        }
        preparedStatement.executeUpdate();
    }

    /**
     * Closes the connection when the socket is no longer needed.
     * @throws SQLException If the socket is already closed a sql exception will occur.
     */
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * This returns a subset of the items that match the parameters.
     * @param rarity Allows for items only of the chosen rarity to be returned.
     * @return ResultSet containing items with the chosen rarity
     * @throws SQLException If the query violates the rules of the database, an error will occur.
     */
    public ResultSet getWonders(RarityConfig rarity) throws SQLException {
        return query(
                "SELECT name, type, page, rarity, id FROM " +
                        NetworkConstants.JDBC_SCHEMA + ".items WHERE rarity = '" + rarity.getName() + "'");
    }

    /**
     * This returns a subset of the potions that match the parameters.
     * @param rarity Allows for potions only of the chosen rarity to be returned.
     * @return ResultSet containing potions with the chosen rarity
     * @throws SQLException If the query violates the rules of the database, an error will occur.
     */
    public ResultSet getPotions(RarityConfig rarity) throws SQLException {
        return query(
                "SELECT name, type, page, rarity, id FROM " + NetworkConstants.JDBC_SCHEMA +
                        ".potions WHERE rarity = '" + rarity.getName() + "'");
    }

    /**
     * This returns a subset of the spells that match the parameters.
     * @param spellLevel Allows for spells only of the chosen spell level and lower to be returned.
     * @return ResultSet containing spells with the chosen spell level and lower
     * @throws SQLException If the query violates the rules of the database, an error will occur.
     */
    public ResultSet getScrolls(Integer spellLevel) throws SQLException {
        return query("SELECT name, page, id FROM " + NetworkConstants.JDBC_SCHEMA +
                ".spells WHERE level_num <= '" + spellLevel + "' AND level_num > '0'");
    }

    /**
     * This method queries the database to see if the session id already exists.
     * If it does, the result set won't be empty so we return false.
     * @param shopId string of ID to be checked
     * @return boolean, true if the ID does not already exist, false if it does.
     * @throws SQLException If the query violates the rules of the database, an error will occur.
     */
    public Boolean checkShopId(String shopId) throws SQLException {
        ResultSet idCheck = query("SELECT sessionId FROM sessions WHERE sessionId = '" + shopId + "'");
        return !idCheck.next();
    }

    /**
     * This method returns all the data about a given stored shop ID. This allows shops to be retrieved for sharing
     * purposes.
     * @param shopId string ID for the shop to be retrieved.
     * @return ResultSet containing the shop data for the chosen ID
     * @throws SQLException if the ID checked does not exist or the query violates the rules of the database, an error
     *                      will occur.
     */
    public ResultSet getShop(String shopId) throws SQLException {
        return preparedQuery("SELECT adminId, discounts, basePrices, itemId, type, gold, stones, charges, doses," +
                " onSale, spellLevel FROM " + NetworkConstants.JDBC_SCHEMA + ".sessions RIGHT JOIN " +
                NetworkConstants.JDBC_SCHEMA + ".storeditems ON sessions.sessionId = storeditems.sessionId WHERE " +
                "sessions.sessionId = ?", shopId);
    }

    /**
     * Retrieves an item based on the given id.
     * @param itemId Integer ID number for a given item in the item table
     * @return ResultSet with data to build Wonder object from.
     * @throws SQLException If the itemId is not in the table or the query violates the rules of the database an error
     *                      will occur.
     */
    public ResultSet getWonder(Integer itemId) throws SQLException {
        return preparedQuery("SELECT type, name, page, rarity FROM items " +
                "WHERE id = ?", itemId);
    }

    /**
     * Retrieves information about an item based on the given id.
     * @param itemId Integer ID number for a given item in the item table
     * @return ResultSet with data to build a WonderInfo object from.
     * @throws SQLException If the itemId is not in the table or the query violates the rules of the database an error
     *                      will occur.
     */
    public ResultSet getWonderInfo(Integer itemId) throws SQLException {
        return preparedQuery("SELECT description, attunement, limits, name FROM items " +
                "WHERE id = ?", itemId);
    }

    /**
     * Retrieves a potion based on the given id.
     * @param itemId Integer ID number for a given potion in the potion table.
     * @return ResultSet with data to build Potion object from.
     * @throws SQLException If the itemId is not in the table or the query violates the rules of the database an error
     *                      will occur.
     */
    public ResultSet getPotion(Integer itemId) throws SQLException {
        return preparedQuery("SELECT type, name, rarity, page FROM potions " +
                "WHERE id = ?", itemId);
    }

    /**
     * Retrieves information about a potion based on the given id
     * @param itemId Integer ID number for a given potion in the potion table
     * @return ResultSet with data to build PotionInfo object from
     * @throws SQLException If the itemId is not in the table or the query violates the rules of the database an error
     *                      will occur.
     */
    public ResultSet getPotionInfo(Integer itemId) throws SQLException {
        return preparedQuery("SELECT description, name FROM potions WHERE id = ?", itemId);
    }

    /**
     * Retrieves a scroll based on the given id
     * @param itemId Integer ID number for a given spell in the scroll table.
     * @return ResultSet with data to build Scroll object from
     * @throws SQLException If the itemId is not in the table or the query violates the rules of the database an error
     *                      will occur.
     */
    public ResultSet getScroll(Integer itemId) throws SQLException {
        return preparedQuery("SELECT name, page, level, level_num FROM spells WHERE id = ?", itemId);
    }

    /**
     * Retrieves information about a scroll based on the given id
     * @param itemId Integer ID number for a given spell in the scroll table.
     * @return ResultSet with data to build ScrollInfo object from
     * @throws SQLException If the itemId is not in the table or the query violates the rules of the database an error
     *                      will occur.
     */
    public ResultSet getScrollInfo(Integer itemId) throws SQLException {
        return preparedQuery("SELECT school, concentration, spell_range, classes, ritual, casting_time, " +
                "components, duration, name FROM spells WHERE id = ?", itemId);
    }

    /**
     * Adds the current session information into the sessions table for future retrieval
     * @param shop Shop object of the newly created Shop to be stored
     * @throws SQLException If the update violates the rules of the database an error will occur.
     */
    public void updateSessions(Shop shop) throws SQLException {
        Gson gson = new Gson();
        preparedUpdate("INSERT INTO sessions (sessionId, adminId, discounts, basePrices) VALUES (?, ?, ?, ?)",
                shop.getId(), shop.getAdminId(), gson.toJson(shop.getBasePrices()), gson.toJson(shop.getDiscounts()));
    }

    /**
     * Adds the items in the current shop and their attributes into the storeditems table.
     * @param shop Shop object of the newly created Shop to be stored
     * @throws SQLException If the update violates the rules of the database an error will occur.
     */
    public void updateStoredItems(Shop shop) throws SQLException {
        for(var item : shop.getItems()) {
            preparedUpdate("INSERT INTO storeditems (sessionId, itemId, type, gold, stones, charges, doses, onSale) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", shop.getId(), item.getId(), item.getType(), item.getGoldCost(),
                    item.getStones(), item.getCharges(), item.getDoses(), item.getOnSale() ? 1 : 0);
        }
        for(var scroll : shop.getScrolls()) {
            preparedUpdate("UPDATE storeditems SET spellLevel = ? WHERE sessionId = ? AND itemId = ?",
                    scroll.getLevelNum(), shop.getId(), scroll.getId());
        }
    }
}
