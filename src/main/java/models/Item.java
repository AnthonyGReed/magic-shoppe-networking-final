package main.java.models;

import main.java.config.RarityConfig;
import main.java.webserver.JDBCSocket;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

/**
 * This is the base object that our other object extends. The attributes and methods listed here are the ones we
 * expect to need in wonders, scrolls, and potions as well.
 * @author areed
 */
public class Item {
    //We have made the jdbcSocket transient so that it will not be included when an object is turned into JSON
    transient JDBCSocket jdbcSocket;

    Integer id;
    String type;
    String name;
    String description;
    String page;
    RarityConfig rarity;
    Boolean onSale;
    Integer goldCost;

    /**
     * This is the constructor for a newly generated item. This will generate the data come to all items.
     * @throws SQLException This will throw an exception if the creation of a socket fails.
     */
    public Item() throws SQLException {
        jdbcSocket = new JDBCSocket();
        this.rarity = generateRarity();
        this.onSale = checkSale();
    }

    /**
     * This is the constructor for items retrieved from the database. The onSale value is part of the shop data and
     * will be passed to this constructor before the rest of the items data is retrieved.
     * @param onSale Boolean as to whether the item is on sale or not.
     * @throws SQLException This will trow an exception if the creation of a socket fails.
     */
    public Item(Boolean onSale) throws SQLException {
        jdbcSocket = new JDBCSocket();
        this.onSale = onSale;
    }

    /**
     * This method selects at random, a rarity for the item it is generating.
     * @return RarityConfig random enum with a higher chance of Common and a smaller chance for each subsequent rarity
     */
    RarityConfig generateRarity() {
        int rand = randomNum(0, 100);
        if (rand < 45) {
            return RarityConfig.COMMON;
        } else if(rand < 75) {
            return RarityConfig.UNCOMMON;
        } else if(rand < 90) {
            return RarityConfig.RARE;
        } else if(rand < 97) {
            return RarityConfig.VERYRARE;
        } else {
            return RarityConfig.LEGENDARY;
        }
    }

    /**
     * Marks 1 in 15 items as "on sale" giving them a gold cost discount.
     * @return Boolean true or false if the item will be considered "on sale" for gold calculation.
     */
    Boolean checkSale() {
        return (randomNum(0, 15) == 15);
    }

    /**
     * A frequently called method for generating a random number without doing the formula every time.
     * @param min The minimum inclusive value
     * @param max The maximum inclusive value
     * @return A value in the selected range
     */
    Integer randomNum(Integer min, Integer max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Takes rarity, discounts, and sale into account to generate a base gold cost of the object
     * @param basePrices The list of rarity prices associated with the shop
     * @param discounts The discounts applicable in the shop (based on type)
     * @return Double gold price of a given item.
     */
    Double calculateBasePrice(HashMap<RarityConfig, Integer> basePrices, HashMap<Types,Integer> discounts) {
        Double price;
        if(!rarity.equals(RarityConfig.LEGENDARY)) {
            price = Double.valueOf(basePrices.get(rarity));
        } else {
            price = Double.valueOf(randomNum(RarityConfig.LEGENDARY.getGoldMin(), RarityConfig.LEGENDARY.getGoldMax()));
        }
        switch(discounts.get(Types.valueOf(type.replace(" ", "").toUpperCase()))) {
            case 0:
                price *= 1.10;
                break;
            case 2:
                price *= 0.90;
                break;
            default:
                break;
        }
        if(onSale) { price *= 0.70; }
        return price;
    }

    /**
     * The data stores the book names as abbreviations. This method converts them to their full names.
     * @param page String of the location in the source books the information is.
     * @return String of the location with abbreviations expanded.
     */
    String convertBookNames(String page) {
        page = page
                .replace("phb", "Player's Handbook")
                .replace("dmg", "Dungeon Master's Guide")
                .replace("mm", "Monster's Manual")
                .replace("xge", "Xanathar's Guide to Everything")
                .replace("tce", "Tasha's Cauldron of Everything")
                .replace("ee", "Elemental Evil")
                .replace("csm", "Custom")
                .replace("pat", "Patreon");
        return page;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getPage() {
        return page;
    }

    public RarityConfig getRarity() {
        return rarity;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public Integer getGoldCost() {
        return goldCost;
    }

    public Integer getStones() {
        return 0;
    }

    public Integer getCharges() {
        return 0;
    }

    public Integer getDoses() {
        return 0;
    }

}