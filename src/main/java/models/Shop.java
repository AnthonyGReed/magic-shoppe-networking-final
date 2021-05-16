package main.java.models;

import com.google.gson.Gson;
import main.java.config.RarityConfig;
import org.apache.commons.lang3.RandomStringUtils;
import main.java.webserver.JDBCSocket;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This is the main container for an instance of the shop. This object holds the item objects and generates some
 * pricing information shared by all items in the shop. Additionally, this object generates a code to be retrieved in
 * the future.
 * @author areed
 */
public class Shop {
    public String id;
    public String adminId;
    public ArrayList<Item> items;
    public ArrayList<Scroll> scrolls;
    public HashMap<RarityConfig, Integer> basePrices;
    public HashMap<Types, Integer> discounts;

    /**
     * This constructor is for creating a new shop. It generates everything new including the shop id. The last step
     * of the constructor is to save the object in the database for future retrieval.
     * @throws SQLException if the query violates the rules of the table an error will occur.
     */
    public Shop() throws SQLException {
        basePrices = new HashMap<>();
        discounts = new HashMap<>();
        items = new ArrayList<>();
        scrolls = new ArrayList<>();
        generateBasePrices();
        generateDiscounts();
        this.id = generateShopId();
        this.adminId = generateAdminId();
        for(int i = 0; i < 15; i++) {
            Wonder wonder = new Wonder(basePrices, discounts);
            items.add(wonder);
        }
        for(int i = 0; i < 3; i ++) {
            Potion potion = new Potion(basePrices, discounts);
            items.add(potion);
        }
        int numOfScrolls = randomNum(3, 5);
        for(int i = 0; i < numOfScrolls; i++) {
            Scroll scroll = new Scroll(basePrices, discounts);
            items.add(scroll);
            scrolls.add(scroll);
        }
        JDBCSocket jdbcSocket = new JDBCSocket();
        jdbcSocket.updateSessions(this);
        jdbcSocket.updateStoredItems(this);
        jdbcSocket.close();
    }

    /**
     * This constructor is for a shop that already exists. Passing the id retrieves the data from the table and
     * populates the items that were supposed to be in it. This is the only place input from the user touches our
     * back end.
     * @param shopId String provided by user frontend (in a query parameter) to load from database
     * @throws SQLException if the ID isn't in the database or the query breaks the rules of the table an error will
     *                      occur.
     */
    public Shop(String shopId) throws SQLException {
        this.id = shopId;
        JDBCSocket jdbcSocket = new JDBCSocket();
        this.items = new ArrayList<>();
        if(shopId.length() != 6 || !StringUtils.isAlphanumeric(shopId)) {
            shopId = "null";
        }
        ResultSet shopInfo = jdbcSocket.getShop(shopId);
        shopInfo.next();
        Gson gson = new Gson();
        this.adminId = shopInfo.getString(1);
        this.discounts = gson.fromJson(shopInfo.getString(2), HashMap.class);
        this.basePrices = gson.fromJson(shopInfo.getString(3), HashMap.class);
        shopInfo.beforeFirst();
        while(shopInfo.next()) {
            if(shopInfo.getString(5).equals("Potion")) {
                items.add(new Potion(
                        shopInfo.getInt(4),
                        shopInfo.getInt(6),
                        shopInfo.getInt(9),
                        shopInfo.getBoolean(10)
                ));
            } else if(shopInfo.getString(5).equals("Scroll")) {
                items.add(new Scroll(
                        shopInfo.getInt(4),
                        shopInfo.getInt(6),
                        shopInfo.getBoolean(10),
                        shopInfo.getInt(11)
                ));
            } else {
                items.add(new Wonder(
                        shopInfo.getInt(4),
                        shopInfo.getInt(6),
                        shopInfo.getInt(7),
                        shopInfo.getInt(8),
                        shopInfo.getBoolean(10)
                ));
            }
        }
        jdbcSocket.close();
    }

    /**
     * This function populates the base prices hash map by iterating through each rarity and generating a random base
     * price from the rarities parameters.
     */
    private void generateBasePrices() {
        for(RarityConfig rarity : RarityConfig.values()) {
           basePrices.put(rarity, randomNum(rarity.getGoldMin(), rarity.getGoldMax()));
        }
    }

    /**
     * This function populates the discounts hash map by iterating through the types and assigning a flag to each. That
     * flag is used to determine the type's discount value.
     */
    private void generateDiscounts() {
        for(Types type: Types.values()) {
            discounts.put(type, randomNum(0,3));
        }
    }

    /**
     * This function takes a min and a max and generates a random number based on the range input.
     * @param min Integer the minimum value of the random range.
     * @param max Integer the maximum value of the random range.
     * @return Integer a random number within the range.
     */
    private Integer randomNum(Integer min, Integer max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    /**
     * This function uses the Apache Commons to generate a random string of six characters to be the shop ID. Before it
     * is assigned, the id is checked by the socket to ensure it does not already exist in the database. We check to
     * see if the id is unique and if it is not, we call the function recursively to find one that is.
     * @return A new string of six characters that does not already occupy space in the database.
     * @throws SQLException The SQLException for an empty result set is our expectation here. Any other rules that
     *                      violate the table will cause an error to occur.
     */
    private String generateShopId() throws SQLException {
        String checkString = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        JDBCSocket jdbcSocket = new JDBCSocket();
        if(!jdbcSocket.checkShopId(checkString)) {
            checkString = generateShopId();
        }
        jdbcSocket.close();
        return checkString;
    }

    /**
     * This function is currently not implemented but it generates an admin id for each shop that could be used to
     * make modifications to the items in the future.
     * @return String of six random characters.
     */
    private String generateAdminId() {
        return RandomStringUtils.randomAlphanumeric(6);
    }

    public String getId() {
        return id;
    }

    public String getAdminId() {
        return adminId;
    }

    public HashMap<RarityConfig, Integer> getBasePrices() {
        return basePrices;
    }

    public HashMap<Types, Integer> getDiscounts() {
        return discounts;
    }

    public ArrayList<Scroll> getScrolls() {
        return scrolls;
    }

    public ArrayList<Item> getItems() {
        return items;
    }
}
