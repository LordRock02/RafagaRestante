package presentacion;

import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyRenderer extends GanttRenderer {

    private static final int PASS = 2; // assumes two passes
    private final List<Color> clut = new ArrayList<>();
    private final TaskSeriesCollection model;
    private int row;
    private int col;
    private int index;

    public MyRenderer(TaskSeriesCollection model) {
        this.model = model;
    }

    @Override
    public Paint getItemPaint(int row, int col) {

        if (clut.isEmpty() || this.row != row || this.col != col) {
            initClut(row, col);
            this.row = row;
            this.col = col;
            index = 0;
        }
        int clutIndex = index++ / PASS;
        return clut.get(clutIndex);
    }

    private void initClut(int row, int col) {
        clut.clear();

        TaskSeries series = (TaskSeries) model.getRowKeys().get(row);
        List<Task> tasks = series.getTasks(); // unchecked

        int taskCount = tasks.get(col).getSubtaskCount();
        taskCount = Math.max(1, taskCount);

        //System.out.println("----> " + taskCount);
        String description;

        for (int i = 0; i < taskCount; i++) {
            description = tasks.get(col).getSubtask(i).getDescription();

           // System.out.println(description + ": " + i);
            if (description.equals("ejecutando")) {
                clut.add(Color.green);
                //System.out.println("green");
            }
            if (description.equals("esperando")) {
                clut.add(Color.gray);
                //System.out.println("gray");
            }
        }
    }
}
