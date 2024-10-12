import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class ParsedSaleInfo {
    public Region region;
    public ItemType itemType;
    public String salesChannel;
    public String orderPriority;
    public LocalDate orderDate;
    public Integer unitsSold;
    public BigDecimal totalProfit;
    public ParsedSaleInfo(Region region, ItemType itemType, String salesChannel,
                          String orderPriority, LocalDate date, Integer unitsSold,
                          BigDecimal totalProfit){
        this.itemType = itemType;
        this.region = region;
        this.salesChannel = salesChannel;
        this.orderPriority = orderPriority;
        this.orderDate = date;
        this.unitsSold = unitsSold;
        this.totalProfit = totalProfit;
    }
}
