import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;

public class BarChart_AWT extends ApplicationFrame {
    public BarChart_AWT(CategoryDataset dataset) {
        super( "Число проданных товаров по регионам" );
        JFreeChart chart = ChartFactory.createBarChart(
                "Число проданных товаров по регионам",
                "Регионы",
                "Число проданных товаров",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,
                false,
                false);

        chart.setBackgroundPaint(Color.white);

        CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint    (new Color(212, 212, 248));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint (Color.white);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );
        setContentPane( chartPanel );
    }

    public static void showChart(CategoryDataset dataset) {
        BarChart_AWT chart = new BarChart_AWT(dataset);
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }
}