import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.javatuples.Tuple;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        String path = System.getProperty("user.dir");
        String fileName = "SoldProducts.csv";
        String pathToCsv = String.format("%s\\src\\%s", path, fileName);
        System.out.println(pathToCsv);
        CustomCSVReader csvReader = new CustomCSVReader();
        List<String[]> records = csvReader.readCSVFile(pathToCsv);
        List<String[]> recordsWithoutHeaders = records.stream().skip(1).toList();
        DataParser dataParser = new DataParser();
        var dataRows = dataParser.getParsedDataList(recordsWithoutHeaders);

        HashSet<Region> regionSet = new HashSet<Region>();
        for (var data : dataRows) {
            regionSet.add(data.region);
        }

        HashSet<ItemType> itemTypeSet = new HashSet<>();
        for (var data : dataRows) {
            itemTypeSet.add(data.itemType);
        }

        String dbFileName = "regionData.db";
        String pathToDb = String.format("%s\\src\\%s", path, dbFileName);
        DbHandler dbHandler = new DbHandler(pathToDb);

        Integer id = 1;
        for (var region : regionSet) {
            region.Id = id;
            id++;
            dbHandler.addRegion(region);
        }

        id = 1;
        for (var itemType : itemTypeSet) {
            itemType.Id = id;
            id++;
            dbHandler.addItemType(itemType);
        }

        id = 1;
        for (var row : dataRows) {
            SaleInfo saleInfo = new SaleInfo();
            saleInfo.SalesChannel = row.salesChannel;
            saleInfo.UnitsSold = row.unitsSold;
            saleInfo.TotalProfit = row.totalProfit;
            saleInfo.OrderDate = row.orderDate;
            saleInfo.OrderPriority = row.orderPriority;
            saleInfo.Id = id;

            var foundRegion = regionSet.stream().
                    filter(val -> val.Region.equals(row.region.Region) && val.Country.equals(row.region.Country)).
                    findFirst().get();

            var foundItemType = itemTypeSet.stream().
                    filter(val -> val.TypeName.equals(row.itemType.TypeName)).
                    findFirst().get();

            saleInfo.RegionId = foundRegion.Id;
            saleInfo.ItemTypeId = foundItemType.Id;
            dbHandler.addSaleInfo(saleInfo);
            id++;
        }
        
        var regionWithSoldUnitsPair = dbHandler.getSoldUnitsPerRegion();
        var firstTaskStr = String.join("; ", regionWithSoldUnitsPair.stream().map(Tuple::toString).toList());

        var regionsWithLargestIncome = dbHandler.getCountryWithLargestIncome();
        var regionsWithLargestIncomeStr = String.join("; ",
                regionsWithLargestIncome.stream().map(val -> String.format("(Регион: %s, Страна: %s, Доход: %s)", val[0], val[1], val[2])).
                        toList());
        var secondTaskStr = String.format("Страны с самым высоким доходом среди регионов Европы и Азии: %s", regionsWithLargestIncomeStr);

        var regionsWithSpecificIncome = dbHandler.getCountryWithSpecificIncome();
        var regionsWithSpecificIncomeStr = String.join("; ",
                regionsWithSpecificIncome.stream().map(val -> String.format("(Регион: %s, Страна: %s, Доход: %s)", val[0], val[1], val[2])).
                        toList());
        var thirdTaskStr = String.format("Страны с доходом от 420 до 440 тыс Ближнего Востока и Северной Африки, а также Субсахарской Африки: %s", regionsWithSpecificIncomeStr);

        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        for (var regionUnitPair : regionWithSoldUnitsPair) {
            categoryDataset.addValue(regionUnitPair.getValue1(), "", regionUnitPair.getValue0());
        }

        BarChart_AWT.showChart(categoryDataset);

        System.out.println(firstTaskStr);

        System.out.println(secondTaskStr);

        System.out.println(thirdTaskStr);
    }
}