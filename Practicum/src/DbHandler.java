import org.javatuples.Pair;
import org.sqlite.JDBC;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DbHandler {
    private String connectionString;

    private final Connection connection;

    public DbHandler(String dbPath) throws SQLException {
        this.connectionString = "jdbc:sqlite:" + dbPath;

        DriverManager.registerDriver(new JDBC());

        this.connection = DriverManager.getConnection(this.connectionString);

        this.initDatabase();
    }

    private void initDatabase(){
        var deleteRegionTable = "DROP TABLE IF EXISTS Regions";
        var deleteItemTypeTable = "DROP TABLE IF EXISTS ItemTypes";
        var deleteSalesInfoTable = "DROP TABLE IF EXISTS SalesInfo";

        var regionTable = "CREATE TABLE IF NOT EXISTS Regions ("
                + "	id INTEGER PRIMARY KEY,"
                + "	region text NOT NULL,"
                + "	country text NOT NULL"
                + ");";

        var itemTypeTable = "CREATE TABLE IF NOT EXISTS ItemTypes ("
                + "	id INTEGER PRIMARY KEY,"
                + "	typeName text NOT NULL"
                + ");";

        var saleInfoTable = "CREATE TABLE IF NOT EXISTS SalesInfo ("
                + "	id INTEGER PRIMARY KEY,"
                + "	regionId INTEGER NOT NULL,"
                + "	itemTypeId INTEGER NOT NULL,"
                + "	saleChannel text NOT NULL,"
                + "	orderPriority text NOT NULL,"
                + "	orderDate text NOT NULL,"
                + "	unitsSold INTEGER NOT NULL,"
                + "	totalProfit REAL NOT NULL,"
                + "	FOREIGN KEY (regionId)  REFERENCES Regions(id) ON DELETE CASCADE,"
                + "	FOREIGN KEY (itemTypeId)  REFERENCES ItemTypes(id) ON DELETE CASCADE"
                + ");";

        try{
            var stmt = this.connection.createStatement();
            stmt.execute(deleteSalesInfoTable);
            stmt.execute(deleteItemTypeTable);
            stmt.execute(deleteRegionTable);

            stmt.execute(regionTable);
            stmt.execute(itemTypeTable);
            stmt.execute(saleInfoTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Region> getAllRegions() {
        try (Statement statement = this.connection.createStatement()) {
            List<Region> regions = new ArrayList<Region>();
            ResultSet resultSet = statement.executeQuery("SELECT id, region, country FROM Regions");
            while (resultSet.next()) {
                var region = new Region(
                        resultSet.getString("region"),
                        resultSet.getString("country")
                );
                region.Id = resultSet.getInt("id");
                regions.add(region);
            }
            return regions;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addRegion(Region region) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO Regions(`id`, `region`, `country`) " +
                        "VALUES(?, ?, ?)")) {
            statement.setObject(1, region.Id);
            statement.setObject(2, region.Region);
            statement.setObject(3, region.Country);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRegion(int id) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM Regions WHERE id = ?")) {
            statement.setObject(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<ItemType> getAllItemTypes() {
        try (Statement statement = this.connection.createStatement()) {
            List<ItemType> itemTypes = new ArrayList<ItemType>();
            ResultSet resultSet = statement.executeQuery("SELECT id, typeName FROM ItemTypes");
            while (resultSet.next()) {
                var itemType = new ItemType(
                        resultSet.getString("typeName")
                );
                itemType.Id = resultSet.getInt("id");
                itemTypes.add(itemType);
            }
            return itemTypes;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addItemType(ItemType itemType) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO ItemTypes(`id`, `typeName`) " +
                        "VALUES(?, ?)")) {
            statement.setObject(1, itemType.Id);
            statement.setObject(2, itemType.TypeName);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteItemType(int id) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM ItemTypes WHERE id = ?")) {
            statement.setObject(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<SaleInfo> getAllSaleInfos() {
        try (Statement statement = this.connection.createStatement()) {
            List<SaleInfo> saleInfos = new ArrayList<SaleInfo>();
            ResultSet resultSet = statement.executeQuery("SELECT id, regionId, itemTypeId, saleChannel, orderPriority," +
                    "orderDate, unitsSold, totalProfit FROM SalesInfo");

            while (resultSet.next()) {
                var salesInfo = new SaleInfo();
                salesInfo.Id = resultSet.getInt("id");
                salesInfo.SalesChannel = resultSet.getString("saleChannel");
                salesInfo.ItemTypeId = resultSet.getInt("itemTypeId");
                salesInfo.RegionId = resultSet.getInt("regionId");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                salesInfo.OrderDate = LocalDate.parse(resultSet.getString("orderDate"), formatter);
                salesInfo.OrderPriority = resultSet.getString("orderPriority");
                salesInfo.UnitsSold = resultSet.getInt("unitsSold");
                salesInfo.TotalProfit = resultSet.getBigDecimal("totalProfit");
                saleInfos.add(salesInfo);
            }
            return saleInfos;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void addSaleInfo(SaleInfo saleInfo) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "INSERT INTO SalesInfo(`id`, `regionId`, `itemTypeId`, `saleChannel`, `orderPriority`," +
                        "`orderDate`, `unitsSold`, `totalProfit`) VALUES(?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setObject(1, saleInfo.Id);
            statement.setObject(2, saleInfo.RegionId);
            statement.setObject(3, saleInfo.ItemTypeId);
            statement.setObject(4, saleInfo.SalesChannel);
            statement.setObject(5, saleInfo.OrderPriority);
            statement.setObject(6, saleInfo.OrderDate);
            statement.setObject(7, saleInfo.UnitsSold);
            statement.setObject(8, saleInfo.TotalProfit);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSaleInfo(int id) {
        try (PreparedStatement statement = this.connection.prepareStatement(
                "DELETE FROM SalesInfo WHERE id = ?")) {
            statement.setObject(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getCountryWithLargestIncome(){
        String query = "SELECT *\n" +
                "FROM \n" +
                "(\n" +
                "\tSELECT r.region, r.country, SUM(s.totalProfit) as total_profit\n" +
                "\tFROM SalesInfo s JOIN Regions r ON r.Id = s.regionId\n" +
                "\tWHERE r.region = 'Asia' OR r.region = 'Europe'\n" +
                "\tGROUP BY r.region, r.country\n" +
                ") as c\n" +
                "WHERE\n" +
                "NOT EXISTS (\n" +
                "\tSELECT 1\n" +
                "\tFROM (\n" +
                "\t\tSELECT r.region, r.country, SUM(s.totalProfit) as total_profit\n" +
                "\t\tFROM SalesInfo s JOIN Regions r ON r.Id = s.regionId\n" +
                "\t\tWHERE r.region = 'Asia' OR r.region = 'Europe'\n" +
                "\t\tGROUP BY r.region, r.country\n" +
                "\t) as cc\n" +
                "\tWHERE \n" +
                "\tcc.total_profit > c.total_profit\n" +
                ")";

        try (Statement statement = this.connection.createStatement()) {

            List<String[]> countries = new ArrayList<String[]>();

            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                String[] result = new String[3];
                result[0] = resultSet.getString("region");
                result[1] = resultSet.getString("country");
                result[2] = Double.toString(resultSet.getDouble("total_profit"));
                countries.add(result);
            }

            return countries;

        } catch (SQLException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }
    }

    public List<String[]> getCountryWithSpecificIncome(){
        String query = "SELECT *\n" +
                "FROM \n" +
                "(\n" +
                "\tSELECT r.region, r.country, SUM(s.totalProfit) as total_profit\n" +
                "\tFROM SalesInfo s JOIN Regions r ON r.Id = s.regionId\n" +
                "\tWHERE r.region = 'Middle East and North Africa' OR r.region = 'Sub-Saharan Africa'\n" +
                "\tGROUP BY r.region, r.country\n" +
                "\tHAVING SUM(s.totalProfit) > 419000 AND SUM(s.totalProfit) < 441000\n" +
                ") as c\n" +
                "WHERE\n" +
                "NOT EXISTS (\n" +
                "\tSELECT 1\n" +
                "\tFROM (\n" +
                "\t\tSELECT r.region, r.country, SUM(s.totalProfit) as total_profit\n" +
                "\t\tFROM SalesInfo s JOIN Regions r ON r.Id = s.regionId\n" +
                "\t\tWHERE r.region = 'Middle East and North Africa' OR r.region = 'Sub-Saharan Africa'\n" +
                "\t\tGROUP BY r.region, r.country\n" +
                "\t\tHAVING SUM(s.totalProfit) > 419000 AND SUM(s.totalProfit) < 441000\n" +
                "\t) as cc\n" +
                "\tWHERE \n" +
                "\tcc.total_profit > c.total_profit\n" +
                ")";

        try (Statement statement = this.connection.createStatement()) {
            List<String[]> countries = new ArrayList<String[]>();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String[] result = new String[3];
                result[0] = resultSet.getString("region");
                result[1] = resultSet.getString("country");
                result[2] = Double.toString(resultSet.getDouble("total_profit"));
                countries.add(result);
            }
            return countries;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Pair<String, Integer>> getSoldUnitsPerRegion(){
        String query = "SELECT *\n" +
                "FROM \n" +
                "(\n" +
                "\tSELECT r.region, SUM(s.unitsSold) as total_sold_units\n" +
                "\tFROM SalesInfo s JOIN Regions r ON r.Id = s.regionId\n" +
                "\tGROUP BY r.region\n" +
                ") as c";

        try (Statement statement = this.connection.createStatement()) {
            List<Pair<String, Integer>> regions = new ArrayList<Pair<String, Integer>>();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Pair<String, Integer> result = Pair.with(
                        resultSet.getString("region"),
                        resultSet.getInt("total_sold_units")
                );
                regions.add(result);
            }
            return regions;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}