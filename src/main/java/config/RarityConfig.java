package main.java.config;

public enum RarityConfig {
    COMMON(5, 16, 1, 4, 1, 1, "Common"),
    UNCOMMON(10, 76, 2, 7, 2, 3, "Uncommon"),
    RARE(50, 751, 4, 13, 4, 5, "Rare"),
    VERYRARE(500, 7501, 8, 25, 6, 8, "Very Rare"),
    LEGENDARY(5000, 75001, 16, 49, 9, 9, "Legendary");

    private final int GOLD_MIN;
    private final int GOLD_MAX;
    private final int STONE_MIN;
    private final int STONE_MAX;
    private final int SPELL_LEVEL_MIN;
    private final int SPELL_LEVEL_MAX;
    private final String NAME;

    /**
     * This enum has a number of attributes associated with each mode. The modes are based around possible rarities of
     * items that can be generated for the item table.
     *
     * @author areed
     * @param goldMin       This is the minimum of the range used for determining a random gold value for an item.
     * @param goldMax       This is the maximum of the range used for determining a random gold value for an it
     * @param stoneMin      This is the minimum of the range used for randomly determining the number of
     *                      spellstones needed to charge a magic item.
     * @param stoneMax      This is the maximum of the range used for determining the number of
     *                      spellstones needed to charge a magic item.
     * @param spellLevelMin This is the mimimum of the range used for randomly determining the spell level of a
     *                      scroll.
     * @param spelllevelMax This is the maximum of the range used for randomly determining the spell level of a
     *                      scroll.
     * @param name          This is the string representation of the rarity for plain text use.
     */
    RarityConfig(int goldMin, int goldMax, int stoneMin, int stoneMax,
                         int spellLevelMin, int spelllevelMax, String name) {
        this.GOLD_MIN = goldMin;
        this.GOLD_MAX = goldMax;
        this.STONE_MIN = stoneMin;
        this.STONE_MAX = stoneMax;
        this.SPELL_LEVEL_MIN = spellLevelMin;
        this.SPELL_LEVEL_MAX = spelllevelMax;
        this.NAME = name;
    }

    public int getGoldMin() {
        return GOLD_MIN;
    }

    public int getGoldMax() {
        return GOLD_MAX;
    }

    public int getStoneMin() {
        return STONE_MIN;
    }

    public int getStoneMax() { return STONE_MAX; }

    public int getSpellLevelMin() { return SPELL_LEVEL_MIN; }

    public int getSpellLevelMax() { return SPELL_LEVEL_MAX; }

    public String getName() { return NAME; }
}
