package main.java.models;

import main.java.config.RarityConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the main "scroll" object for spells. We have two constructors, one for creating a new object and one for a
 * a specific instance of an object that has been stored in the database. Additionally, there are some methods that
 * are explicitly needed by the Scroll group.
 * @author areed
 */
public class Scroll extends Item{
    String level;
    Integer levelNum;

    /**
     * This takes the information from the shop and constructs a new Scroll item object.
     * @param basePrices HashMap containing the Rarity Config enum and the assigned price value for each of the options.
     * @param discounts HashMap containing the types and an integer displaying what kind of discount they should get.
     * @throws SQLException if the query violates the rules of the table an error will occur.
     */
    public Scroll(HashMap<RarityConfig, Integer> basePrices, HashMap<Types, Integer> discounts) throws SQLException {
        super();
        Integer spellLevel = randomNum(rarity.getSpellLevelMin(), rarity.getSpellLevelMax());
        ResultSet possibleSpells = jdbcSocket.getScrolls(spellLevel);
        ArrayList<HashMap<String, Object>> spellList = new ArrayList<>();
        while(possibleSpells.next()) {
            HashMap<String, Object> spellProps = new HashMap<>();
            spellProps.put("name", possibleSpells.getString(1));
            spellProps.put("page", possibleSpells.getString(2));
            spellProps.put("id", possibleSpells.getInt(3));
            spellList.add(spellProps);
        }
        Integer selectedNumber = randomNum(0, spellList.size() - 1);
        HashMap<String, Object> selectedSpell = spellList.get(selectedNumber - 1);
        this.id = (Integer)selectedSpell.get("id");
        this.type = "Scroll";
        this.name = (String)selectedSpell.get("name");
        this.page = convertBookNames((String)selectedSpell.get("page"));
        this.level = levelFromLevelNum(spellLevel);
        this.levelNum = spellLevel;
        this.onSale = checkSale();
        this.goldCost = generateGoldCost(basePrices, discounts);
        jdbcSocket.close();
    }

    /**
     * This creates a Scroll item object by taking in data from teh storeditems table.
     * @param itemId Integer id for the scroll
     * @param gold Integer gold cost for the scroll
     * @param onSale Boolean if the item is on sale or not.
     * @throws SQLException if the itemId does not exist in the items table or the query violates the rules of the
     *                      table an error will occur.
     */
    public Scroll(Integer itemId, Integer gold, Boolean onSale, Integer spellLevel) throws SQLException {
        super(onSale);
        this.id = itemId;
        this.goldCost = gold;
        ResultSet itemInfo = jdbcSocket.getScroll(itemId);
        itemInfo.next();
        this.type = "Scroll";
        this.name = itemInfo.getString(1);
        this.page = convertBookNames(itemInfo.getString(2));
        this.level = levelFromLevelNum(spellLevel);
        this.levelNum = spellLevel;
        this.rarity = rarityFromSpellLevel(spellLevel);
        jdbcSocket.close();
    }

    /**
     * This price is a function of the base price, discounts, and whether or not the item is on sale. This number is
     * rounded and multiplied by 10.
     * @param basePrices The list of rarity prices associated with the shop
     * @param discounts The discounts applicable in the shop (based on type)
     * @return Integer total price of the Wonder
     */
    private Integer generateGoldCost(HashMap<RarityConfig, Integer> basePrices, HashMap<Types, Integer> discounts) {
        return (int)Math.round(calculateBasePrice(basePrices, discounts)) * 10;
    }

    /**
     * The level of the spell is dynamically generated, but when we display it in the Javascript we want to display a
     * String version of the level using the number's suffix. Since our number system is silly and has different
     * suffixes for the first three numbers, we use this switch to assign the correct stringified level to the object.
     * @param levelNum Integer the level of the spell that will be on the scroll
     * @return Stringified version of the level of the spell.
     */
    private String levelFromLevelNum(Integer levelNum) {
        String result = "";
        switch(levelNum) {
            case 1:
                result += "1st";
                break;
            case 2:
                result += "2nd";
                break;
            case 3:
                result += "3rd";
                break;
            default:
                result += levelNum + "th";
        }
        return result;
    }

    /**
     * The level of the spell is stored in the database, but not the rarity. When we pull the spell from the database
     * we need to reconstruct what the rarity was by checking the level number against this switch.
     * @param levelNum Integer the level fof the spell that was stored in the database.
     * @return RarityConfig of the rarity of the object
     */
    private RarityConfig rarityFromSpellLevel(Integer levelNum) {
        RarityConfig value;
        switch(levelNum) {
            case 1:
                value = RarityConfig.COMMON;
                break;
            case 2:
            case 3:
                value = RarityConfig.UNCOMMON;
                break;
            case 4:
            case 5:
                value = RarityConfig.RARE;
                break;
            case 6:
            case 7:
            case 8:
                value = RarityConfig.VERYRARE;
                break;
            case 9:
                value = RarityConfig.LEGENDARY;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + levelNum);
        }
        return value;
    }

    public String getLevel() {
        return level;
    }

    public Integer getLevelNum() {
        return levelNum;
    }
}
