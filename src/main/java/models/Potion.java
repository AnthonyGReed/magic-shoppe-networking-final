package main.java.models;

import main.java.config.RarityConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the main "Potion" object. We have two constructors, one for generating a new object and one fora specific
 * instance of an object that has been stored in the database. Additionally, there are some methods that are
 * explicitly needed by the Potion group.
 * @author areed
 */
public class Potion extends Item {
    Integer doses;

    /**
     * This takes information from teh shop and constructs a new Potion item object.
     * @param basePrices HashMap containing the RarityConfig enum and the assigned price value for each of options.
     * @param discounts HashMap containing the types and an integer displaying what kind of discount they should get.
     * @throws SQLException if the query violates the rules of the table an error will occur.
     */
    public Potion(HashMap<RarityConfig, Integer> basePrices, HashMap<Types, Integer> discounts ) throws SQLException {
        super();
        ResultSet possiblePotions = jdbcSocket.getPotions(rarity);
        ArrayList<HashMap<String, Object>> potionList = new ArrayList<>();
        while(possiblePotions.next()) {
            HashMap<String, Object> potionProps = new HashMap<>();
            potionProps.put("name", possiblePotions.getString(1));
            potionProps.put("type", possiblePotions.getString(2));
            potionProps.put("page", possiblePotions.getString(3));
            potionProps.put("rarity", RarityConfig.valueOf(possiblePotions.getString(4).replace(" ", "").toUpperCase()));
            potionProps.put("id", possiblePotions.getInt(5));
            potionList.add(potionProps);
        }
        Integer selectedNumber = randomNum(0, potionList.size() - 1);
        HashMap<String, Object> selectedPotion = potionList.get(selectedNumber);
        this.id = (Integer)selectedPotion.get("id");
        this.type = (String)selectedPotion.get("type");
        this.name = (String)selectedPotion.get("name");
        this.page = convertBookNames((String)selectedPotion.get("page"));
        this.doses = generateDoses();
        this.onSale = checkSale();
        this.goldCost = generateGoldCost(basePrices, discounts);
        jdbcSocket.close();
    }

    /**
     * This creates a Potion item object by taking in data from the storeditems table.
     * @param itemId Integer id for the potion
     * @param gold Integer gold cost for the potion
     * @param doses Integer number of doses for the potion
     * @param onSale Boolean if the item is on sale or not
     * @throws SQLException if the itemId does not exist in the items table or the query violates the rules of the
     *                      table an error will occur.
     */
    public Potion(Integer itemId, Integer gold, Integer doses, Boolean onSale) throws SQLException {
        super(onSale);
        this.id = itemId;
        this.goldCost = gold;
        this.doses = doses;
        ResultSet itemInfo = jdbcSocket.getPotion(itemId);
        itemInfo.next();
        this.type = itemInfo.getString(1);
        this.name = itemInfo.getString(2);
        this.rarity = RarityConfig.valueOf(itemInfo.getString(3).replace(" ", "").toUpperCase());
        this.page = convertBookNames(itemInfo.getString(4));
        jdbcSocket.close();
    }

    /**
     * Generates the number of doses a potion has before it must be discarded
     * @return Integer a random number between 1 and 5
     */
    private Integer generateDoses() { return randomNum(1, 5); }

    /**
     * This price is a function of the base price, discounts, whether or not the item is on sale, and the number of
     * doses. This number is rounded and multiplied by 10.
     * @param basePrices The list of rarity prices associated with the shop
     * @param discounts The discounts applicable in the shop based on type
     * @return Integer total price of the potion
     */
    private Integer generateGoldCost(HashMap<RarityConfig, Integer> basePrices, HashMap<Types, Integer> discounts) {
        Double price = calculateBasePrice(basePrices, discounts);
        price *= (1 + (doses *.2));
        return (int)Math.round(price) * 10;
    }

    @Override
    public Integer getDoses() {
        return doses;
    }
}
