package ejemplo_uso_jfreechart;

import java.awt.Color;
import java.awt.Paint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import presentacion.MyRenderer;

public class GanttDemo2 extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    public static final TaskSeriesCollection model = new TaskSeriesCollection();

    public GanttDemo2(final String title) {
        super(title);
        final IntervalCategoryDataset dataset = createSampleDataset();
        // create the chart...
        final JFreeChart chart = ChartFactory.createGanttChart(
                "Diagramme de Gantt", // chart title
                "Processus", // domain axis label
                "temps(ms)", // range axis label
                dataset, // data
                false, // include legend
                true, // tooltips
                false // urls
        );
        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        DateAxis range = (DateAxis) plot.getRangeAxis();
        range.setDateFormatOverride(new SimpleDateFormat("SSS"));
        range.setMaximumDate(new Date(100));

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

        //GanttRenderer personnalisÃ©..
        MyRenderer renderer = new MyRenderer(model);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);
    }



    private IntervalCategoryDataset createSampleDataset() {
        final TaskSeries s1 = new TaskSeries("");
        final Task t0 = new Task("esperando", new SimpleTimePeriod(30, 50));
        final Task st0 = new Task("ejecutando", new SimpleTimePeriod(10, 20));
        // Task st01 = new Task( "ES",new SimpleTimePeriod(30,60));
        t0.addSubtask(st0);
        //   t.addSubtask(st01);
        s1.add(t0);

        final Task t1 = new Task("P1", new SimpleTimePeriod(5, 10));
        final Task st11 = new Task("esperando", new SimpleTimePeriod(10, 20));
        Task st12 = new Task("ejecutando", new SimpleTimePeriod(30, 60));
        t1.addSubtask(st11);
        t1.addSubtask(st12);
        s1.add(t1);

        final Task t2 = new Task("P2", new SimpleTimePeriod(0, 40));
        final Task st31 = new Task("esperando", new SimpleTimePeriod(5, 30));
        final Task st32 = new Task("ejecutando", new SimpleTimePeriod(50, 60));
        t2.addSubtask(st31);
        t2.addSubtask(st32);
        s1.add(t2);

        final Task t3 = new Task("P3", new SimpleTimePeriod(0, 80));
        final Task st41 = new Task("esperando", new SimpleTimePeriod(0, 30));
        final Task st42 = new Task("ejecutando", new SimpleTimePeriod(30, 80));
        t3.addSubtask(st41);
        t3.addSubtask(st42);
        s1.add(t3);

        model.add(s1);
        return model;
    }

    public static void main(final String[] args) {
        final GanttDemo2 demo = new GanttDemo2("Gantt Chart Demo 2");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }
}