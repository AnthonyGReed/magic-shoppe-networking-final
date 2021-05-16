package main.java.dbSetup;


import main.java.config.NetworkConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * This will connect to the database and create the schema as outlined in the NetworkConstants config file.
 * Then it will create the required tables and populate the data tables from the TSV files in the data folder.
 * The TSV files contain lists of items to draw from randomly when the api is called.
 *
 * @author areed
 */
public class Installer {
    /**
     * @throws SQLException If the account provided does not have access or the data does not match the tables created.
     */
    public static void main(String[] args) throws SQLException {
        System.out.println("==============================================");
        System.out.println("||      Magic Item Shop Database Setup      ||");
        System.out.println("==============================================");
        System.out.println("Connecting to " + NetworkConstants.JDBC_CONNECTION_STRING + " with user " +
                NetworkConstants.JDBC_USERNAME);
        Connection connection = DriverManager.getConnection(NetworkConstants.JDBC_CONNECTION_STRING,
                NetworkConstants.JDBC_USERNAME,
                NetworkConstants.JDBC_PASSWORD);
        Statement stmt = connection.createStatement();
        System.out.println("Connection established...");
        System.out.println("Creating Schema " + NetworkConstants.JDBC_SCHEMA);
        stmt.execute("CREATE SCHEMA " + NetworkConstants.JDBC_SCHEMA);
        System.out.println("Schema Created");
        connection.setCatalog(NetworkConstants.JDBC_SCHEMA);
        Statement statement = connection.createStatement();
        System.out.println("Creating Magic Item Table");
        statement.execute("CREATE TABLE `items` (" +
                "  `id` int NOT NULL AUTO_INCREMENT," +
                "  `name` varchar(255) DEFAULT NULL," +
                "  `rarity` varchar(45) DEFAULT NULL," +
                "  `page` varchar(45) DEFAULT NULL," +
                "  `description` text," +
                "  `attunement` tinyint DEFAULT NULL," +
                "  `limits` varchar(255) DEFAULT NULL," +
                "  `type` varchar(45) DEFAULT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE KEY `id_UNIQUE` (`id`)" +
                ")");
        System.out.println("Creating Potions Table");
        statement.execute("CREATE TABLE `potions` (" +
                "  `id` int NOT NULL AUTO_INCREMENT," +
                "  `name` varchar(255) DEFAULT NULL," +
                "  `type` varchar(45) DEFAULT NULL," +
                "  `rarity` varchar(45) DEFAULT NULL," +
                "  `page` varchar(45) DEFAULT NULL," +
                "  `description` text," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE KEY `id_UNIQUE` (`id`)" +
                ")");
        System.out.println("Creating Spell Table");
        statement.execute("CREATE TABLE `spells` (" +
                "  `id` int NOT NULL AUTO_INCREMENT," +
                "  `name` varchar(45) DEFAULT NULL," +
                "  `type` varchar(45) DEFAULT NULL," +
                "  `school` varchar(45) DEFAULT NULL," +
                "  `spell_range` varchar(45) DEFAULT NULL," +
                "  `duration` varchar(45) DEFAULT NULL," +
                "  `components` varchar(45) DEFAULT NULL," +
                "  `level` varchar(45) DEFAULT NULL," +
                "  `level_num` varchar(1) DEFAULT NULL," +
                "  `concentration` tinyint DEFAULT NULL," +
                "  `ritual` tinyint DEFAULT NULL," +
                "  `classes` varchar(255) DEFAULT NULL," +
                "  `casting_time` varchar(45) DEFAULT NULL," +
                "  `page` varchar(45) DEFAULT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE KEY `id_UNIQUE` (`id`)" +
                ")");
        System.out.println("Creating stored sessions table");
        statement.execute("CREATE TABLE `sessions` (" +
                "  `sessionId` varchar(20) NOT NULL," +
                "  `adminId` varchar(20) NOT NULL," +
                "  `discounts` json DEFAULT NULL," +
                "  `basePrices` json DEFAULT NULL," +
                "  PRIMARY KEY (`sessionId`)," +
                "  UNIQUE KEY `sessionId_UNIQUE` (`sessionId`)" +
                ")");
        System.out.println("Creating stored items table");
        statement.execute("CREATE TABLE `storeditems` (" +
                "  `id` int NOT NULL AUTO_INCREMENT," +
                "  `sessionId` varchar(20) NOT NULL," +
                "  `itemId` int NOT NULL," +
                "  `type` varchar(20) NOT NULL," +
                "  `gold` int DEFAULT NULL," +
                "  `stones` int DEFAULT NULL," +
                "  `charges` int DEFAULT NULL," +
                "  `doses` int DEFAULT NULL," +
                "  `onSale` tinyint DEFAULT NULL," +
                "  `spellLevel` int DEFAULT NULL," +
                "  PRIMARY KEY (`id`)" +
                ")");
        System.out.println("Tables created");
        String itemSql = "INSERT INTO items (name, rarity, page, description, attunement, limits, type) VALUES" +
                " (?, ?, ?, ?, ?, ?, ?)";
        try {
            System.out.println("Populating items table...");
            PreparedStatement itemPreparedStatement = connection.prepareStatement(itemSql);
            BufferedReader itemLineReader = new BufferedReader(new FileReader(
                    "src/main/java/dbSetup/data/MagicItems.tsv"));
            String itemLineText;

            int itemCount = 0;
            int itemBatchSize = 20;

            itemLineReader.readLine();

            while ((itemLineText = itemLineReader.readLine()) != null) {
                String[] data = itemLineText.split("\\t");
                String name = data[0];
                String type = data[1];
                String rarity = data[2];
                String page = data[3];
                String description = data[4];
                int attunement = Integer.parseInt(data[5]);
                String limits = "";
                if (data.length > 6) {
                    limits = data[6];
                }

                itemPreparedStatement.setString(1, name);
                itemPreparedStatement.setString(2, rarity);
                itemPreparedStatement.setString(3, page);
                itemPreparedStatement.setString(4, description);
                itemPreparedStatement.setInt(5, attunement);
                itemPreparedStatement.setString(6, limits);
                itemPreparedStatement.setString(7, type);

                itemPreparedStatement.addBatch();

                if (itemCount % itemBatchSize == 0) {
                    itemPreparedStatement.executeBatch();
                }
                itemCount++;
            }
            itemPreparedStatement.executeBatch();
            System.out.println("Complete");
        } catch (IOException | SQLException ex) {
            System.err.println(ex);
        }
        String potionSql = "INSERT INTO potions (name, type, rarity, page, description) VALUES" +
                " (?, ?, ?, ?, ?)";
        try {
            System.out.println("Populating potions table...");
            PreparedStatement potionPreparedStatement = connection.prepareStatement(potionSql);
            BufferedReader potionLineReader = new BufferedReader(new FileReader(
                    "src/main/java/dbSetup/data/Potions.tsv"));
            String potionLineText;

            int potionCount = 0;
            int potionBatchSize = 20;

            potionLineReader.readLine();

            while ((potionLineText = potionLineReader.readLine()) != null) {
                String[] data = potionLineText.split("\\t");
                String name = data[0];
                String type = data[1];
                String rarity = data[2];
                String page = data[3];
                String description = data[4];

                potionPreparedStatement.setString(1, name);
                potionPreparedStatement.setString(2, type);
                potionPreparedStatement.setString(3, rarity);
                potionPreparedStatement.setString(4, page);
                potionPreparedStatement.setString(5, description);

                potionPreparedStatement.addBatch();

                if (potionCount % potionBatchSize == 0) {
                    potionPreparedStatement.executeBatch();
                }
                potionCount++;
            }
            potionPreparedStatement.executeBatch();
            System.out.println("Complete");
        } catch (IOException | SQLException ex) {
            System.err.println(ex);
        }
        String scrollSql = "INSERT INTO spells (name, type, school, spell_range, duration, components, " +
                "level, level_num, concentration, ritual, classes, casting_time, page) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            System.out.println("Populating spells table...");
            PreparedStatement scrollPreparedStatement = connection.prepareStatement(scrollSql);
            BufferedReader scrollLineReader = new BufferedReader(new FileReader(
                    "src/main/java/dbSetup/data/Spells.tsv"));
            String scrollLineText;

            int scrollCount = 0;
            int scrollBatchSize = 20;

            scrollLineReader.readLine();

            while ((scrollLineText = scrollLineReader.readLine()) != null) {
                String[] data = scrollLineText.split("\\t");
                String name = data[0];
                String type = data[1];
                String school = data[2];
                String range = data[3];
                String duration = data[4];
                String components = data[5];
                String level = data[6];
                String level_num = data[6].charAt(0) == 'C' ? "0" : data[6].substring(0, 1);
                int concentration = Integer.parseInt(data[7]);
                int ritual = Integer.parseInt(data[8]);
                String classes = data[9];
                String castingTime = data[10];
                String page = data[11];

                scrollPreparedStatement.setString(1, name);
                scrollPreparedStatement.setString(2, type);
                scrollPreparedStatement.setString(3, school);
                scrollPreparedStatement.setString(4, range);
                scrollPreparedStatement.setString(5, duration);
                scrollPreparedStatement.setString(6, components);
                scrollPreparedStatement.setString(7, level);
                scrollPreparedStatement.setString(8, level_num);
                scrollPreparedStatement.setInt(9, concentration);
                scrollPreparedStatement.setInt(10, ritual);
                scrollPreparedStatement.setString(11, classes);
                scrollPreparedStatement.setString(12, castingTime);
                scrollPreparedStatement.setString(13, page);
                scrollPreparedStatement.addBatch();

                if (scrollCount % scrollBatchSize == 0) {
                    scrollPreparedStatement.executeBatch();
                }
                scrollCount++;
            }
            scrollPreparedStatement.executeBatch();
            System.out.println("Complete");
        } catch (IOException | SQLException ex) {
            System.err.println(ex);
        }

        System.out.println("All tables complete with no errors. Closing...");
        System.out.println("==============================================");
        connection.close();
        System.exit(1);
    }
}
