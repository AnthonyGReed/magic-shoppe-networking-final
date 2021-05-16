package main.java.models;

import main.java.config.RarityConfig;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the main "wonder" object for everything that isn't a scroll or potion. We have two constructors, one for
 * generating a new object and one for a specific instance of an object that has been stored in the database.
 * Additionally, there are some methods that are explicitly needed by the Wonder group.
 * @author areed
 */
public class Wonder extends Item {
    Integer charges;
    Integer stones;

    /**
     * This takes the information from the shop and constructs a new Wonder item object.
     * @param basePrices HashMap containing the RarityConfig enum and the assigned price value for each of options.
     * @param discounts HashMap containing the types and an integer displaying what kind of discount they should get.
     * @throws SQLException if the query violates the rules of the table an error will occur.
     */
    public Wonder(HashMap<RarityConfig, Integer> basePrices, HashMap<Types, Integer> discounts) throws SQLException {
        super();
        ResultSet possibleItems = jdbcSocket.getWonders(rarity);
        ArrayList<HashMap<String, Object>> itemList = new ArrayList<>();
        while (possibleItems.next()) {
            HashMap<String, Object> itemProps = new HashMap<>();
            itemProps.put("name", possibleItems.getString(1));
            itemProps.put("type", possibleItems.getString(2));
            itemProps.put("page", possibleItems.getString(3));
            itemProps.put("rarity", RarityConfig.valueOf(possibleItems
                    .getString(4)
                    .replace(" ", "")
                    .toUpperCase()));
            itemProps.put("id", possibleItems.getInt(5));
            itemList.add(itemProps);
        }
        Integer selectedNumber = randomNum(0, itemList.size() - 1);
        HashMap<String, Object> selectedItem = itemList.get(selectedNumber);
        this.id = (Integer) selectedItem.get("id");
        this.type = (String) selectedItem.get("type");
        this.name = (String) selectedItem.get("name");
        this.page = convertBookNames((String) selectedItem.get("page"));
        this.charges = generateCharges();
        this.stones = generateStones(rarity);
        this.onSale = checkSale();
        this.goldCost = generateGoldCost(basePrices, discounts);
        jdbcSocket.close();
    }

    /**
     * This creates a Wonder item object by taking in data from the storeditems table.
     * @param itemId Integer id for the wonder
     * @param gold Integer gold cost for the wonder
     * @param stones Integer stone cost per charge for the wonder
     * @param charges Integer number of charges for the wonder
     * @param onSale Boolean if the item is on sale or not
     * @throws SQLException if the itemId does not exist in the items table or the query violates the rules of the
     *                      table an error will occur.
     */
    public Wonder(Integer itemId, Integer gold, Integer stones, Integer charges, Boolean onSale) throws SQLException {
        super(onSale);
        this.id = itemId;
        this.goldCost = gold;
        this.stones = stones;
        this.charges = charges;
        ResultSet itemInfo = jdbcSocket.getWonder(itemId);
        itemInfo.next();
        this.type = itemInfo.getString(1);
        this.name = itemInfo.getString(2);
        this.page = convertBookNames(itemInfo.getString(3));
        this.rarity = RarityConfig.valueOf(itemInfo.getString(4).replace(" ", "").toUpperCase());
        jdbcSocket.close();
    }

    /**
     * Generates the number of charges an item has before it must be recharged.
     * @return Integer a random number between 1 and 5
     */
    private Integer generateCharges() {
        return randomNum(1, 5);
    }

    /**
     * Generates the number of spell stones it will cost to recharge one charge of an item
     * @param rarity RarityConfig enum carries the min and max of stones for each rarity.
     * @return Integer the stone cost of a charge for the item.
     */
    private Integer generateStones(RarityConfig rarity) {
        return randomNum(rarity.getStoneMin(), rarity.getStoneMax());
    }

    /**
     * This price is a function of the base price, discounts, whether or not the item is on sale, the number of charges,
     * and the stone cost to recharge it. This number is rounded and multiplied by 10.
     * @param basePrices The list of rarity prices associated with the shop
     * @param discounts The discounts applicable in the shop (based on type)
     * @return Integer total price of the Wonder
     */
    private Integer generateGoldCost(HashMap<RarityConfig, Integer> basePrices, HashMap<Types, Integer> discounts) {
        Double price = calculateBasePrice(basePrices, discounts);
        price *= (1 + (charges * .2));
        price *= (1 - (
                (Double.valueOf(stones) - (double) rarity.getStoneMin()) /
                        ((double) rarity.getStoneMax() - (double) rarity.getStoneMin()) * .4));
        return (int) Math.round(price) * 10;
    }

    @Override
    public Integer getCharges() {
        return charges;
    }

    @Override
    public Integer getStones() {
        return stones;
    }
}