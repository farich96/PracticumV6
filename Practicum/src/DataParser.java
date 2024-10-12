import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class DataParser {
    public List<ParsedSaleInfo> getParsedDataList(List<String[]> list){
        List<ParsedSaleInfo> result = new ArrayList<>();
        return list.stream().map(v -> getParsedData(v)).toList();
    }

    public ParsedSaleInfo getParsedData(String[] dataRow){
        Region region = new Region(dataRow[0], dataRow[1]);
        ItemType itemType = new ItemType(dataRow[2]);
        BigDecimal totalProfit = new BigDecimal(dataRow[7]);
        Integer unitsSold = Integer.parseInt(dataRow[6]);
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("[M/dd/yyyy]" + "[MM.dd.yyyy]"));
        DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
        LocalDate date = LocalDate.parse(dataRow[5], dateTimeFormatter);
        ParsedSaleInfo parsedSaleInfo = new ParsedSaleInfo(
                region, itemType, dataRow[3], dataRow[4],
                date, unitsSold, totalProfit
        );
        return parsedSaleInfo;
    }
}
