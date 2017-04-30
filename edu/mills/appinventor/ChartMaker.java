package edu.mills.appinventor;

import android.util.Log;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.WebViewer;
import com.google.appinventor.components.runtime.util.YailList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.acra.ACRA.LOG_TAG;


@DesignerComponent(version = ChartMaker.VERSION,
        description = "An extension that provides chart-making capabilities. " +
                "Pie charts, bar graphs, and line charts can be displayed in a " +
                "WebViewer using the visualization API for Google Charts. " +
                "Data can be fed into each draw function in list form, " +
                "with strings for labels and titles.",
        category = ComponentCategory.EXTENSION,
        nonVisible = true,
        iconName = "images/extension.png")
@SimpleObject(external = true)
public class ChartMaker extends AndroidNonvisibleComponent implements Component {

    public static final int VERSION = 2;
    private ComponentContainer container;
    private String filePath;

    public ChartMaker(ComponentContainer container) {
        super(container.$form());
    }

    private static String PreparePieLabels(YailList typeOfInput, YailList inputLabel) {
        StringBuilder pieLabels = new StringBuilder();
        for (int i = 1; i < typeOfInput.size() + 1; i++) {
            pieLabels.append("data.addColumn('" + typeOfInput.get(i).toString() + "', '" +
                    inputLabel.get(i).toString() + "');\n");
        }
        return pieLabels.toString();
    }

    private static String PreparePieValues(YailList itemList, YailList valueList){
        StringBuilder parsedValues = new StringBuilder();
        parsedValues.append("data.addRows([");
        for (int i = 1; i < itemList.size() + 1; i++){
            parsedValues.append("['" + itemList.get(i).toString() + "', "
                    + valueList.get(i) + "]");
            if (i == itemList.size()){
                parsedValues.append("]);");
            }
            else{
                parsedValues.append(",");
            }
        }
        return parsedValues.toString();
    }


    private static String PrepareLineLabels(YailList labelInput){
        StringBuilder parsedLabels = new StringBuilder();
        parsedLabels.append("data.addColumn('number', 'X');");
        for (int i = 1; i < labelInput.size() + 1; i++) {
            parsedLabels.append("\ndata.addColumn('number', '").append(labelInput.get(i).toString() + "');");
        }
        return parsedLabels.toString();
    }

    private static String PrepareLineValues(YailList values){
        int numRows = values.size();
        YailList rowData = (YailList) values.get(1);
        int numDigits = rowData.size();

        StringBuilder parsedLineValues = new StringBuilder();
        parsedLineValues.append("data.addRows([\n");

        for (int i = 1; i < numRows + 1; i++) {
            parsedLineValues.append("[");
            rowData = (YailList) values.get(i);
            for (int j = 1; j < numDigits + 1; j++) {
                parsedLineValues.append(rowData.get(j));
                if (j == numDigits) {
                    parsedLineValues.append("]");
                } else {
                    parsedLineValues.append(", ");
                }
            }
            if (i == numRows) {
                parsedLineValues.append("]);");
            } else {
                parsedLineValues.append(", ");
            }
        }
        return parsedLineValues.toString();
    }

    @SimpleFunction(description = "Creates a pie chart from a title string and input lists of data, " +
            "types, labels, items, and values and displays the chart in the WebViewer passed as the final argument. ")
    public void DrawPieChart(String title, YailList types, YailList labels, YailList items,
                             YailList values, WebViewer webViewer){
        String parsedTypes = PreparePieLabels(types, labels);
        String parsedValues = PreparePieValues(items, values);
        try {
            String htmlCode = "<html>\n" +
                    "  <head>\n" +
                    "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "\n" +
                    "      google.charts.load('current', {'packages':['corechart']});\n" +
                    "\n" +
                    "      google.charts.setOnLoadCallback(drawChart);\n" +
                    "\n" +
                    "      function drawChart() {\n" +
                    "\n" +
                    "        var data = new google.visualization.DataTable();\n" +
                    parsedTypes +
                    parsedValues +
                    "\n" +
                    "        var options = {title: '" + title + "',\n" +
                    "                       legend: 'bottom',\n" +
                    "                       width: " + webViewer.Width() +",\n" +
                    "                       height: " + webViewer.Height() +
                    "                                                       };\n" +
                    "\n" +
                    "        var chart = new google.visualization.PieChart(document.getElementById('pie_chart'));\n" +
                    "        chart.draw(data, options);\n" +
                    "      }\n" +
                    "    </script>\n" +
                    "  </head>\n" +
                    "\n" +
                    "  <body>\n" +
                    "    <div id=\"pie_chart\"></div>\n" +
                    "  </body>\n" +
                    "</html>";
            File file = File.createTempFile("display", ".html");
            filePath = file.getAbsolutePath();
            FileWriter filewriter = new FileWriter(file);
            filewriter.write(htmlCode);
            filewriter.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException", e);
        }
        webViewer.GoToUrl("file://" + filePath);
    }

    @SimpleFunction(description = "Creates a bar chart from a title string and input lists of data " +
            "types, labels, items, and values and displays the chart in the WebViewer passed as the final argument. ")
    public void DrawBarGraph(String title, YailList types, YailList labels, YailList items,
                             YailList values, WebViewer webViewer){
        String parsedTypes = PreparePieLabels(types, labels);
        String parsedValues = PreparePieValues(items, values);
        try {
            String htmlCode = "<html>\n" +
                    "  <head>\n" +
                    "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "\n" +
                    "      google.charts.load('current', {'packages':['corechart']});\n" +
                    "\n" +
                    "      google.charts.setOnLoadCallback(drawChart);\n" +
                    "\n" +
                    "      function drawChart() {\n" +
                    "\n" +
                    "        var data = new google.visualization.DataTable();\n" +
                    parsedTypes +
                    parsedValues +
                    "\n" +
                    "        var options = {title: '" + title + "',\n" +
                    "                       legend: { position: 'bottom' },\n" +
                    "                       width:" + webViewer.Width() + ",\n" +
                    "                       height: " + webViewer.Height() + "};\n" +
                    "\n" +
                    "        var chart = new google.visualization.BarChart(document.getElementById('bar_chart'));\n" +
                    "        chart.draw(data, options);\n" +
                    "      }\n" +
                    "    </script>\n" +
                    "  </head>\n" +
                    "\n" +
                    "  <body>\n" +
                    "    <div id=\"bar_chart\"></div>\n" +
                    "  </body>\n" +
                    "</html>";
            File file = File.createTempFile("display", ".html");
            filePath = file.getAbsolutePath();
            FileWriter filewriter = new FileWriter(file);
            filewriter.write(htmlCode);
            filewriter.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException", e);
        }
        webViewer.GoToUrl("file://" + filePath);
    }

    @SimpleFunction(description = "Creates a line graph from a main title string, x- and y-axis " +
            "title strings, and input lists of lists of labels and values, and displays the chart " +
            "in the WebViewer passed as the final argument. ")
    public void DrawLineGraph(String chartTitle, String hAxisTitle, String vAxisTitle, YailList labels, YailList values, WebViewer webViewer){
        String parsedLabels = PrepareLineLabels(labels);
        String parsedValues = PrepareLineValues(values);
        try {
            String htmlCode = "<html>\n" +
                    "  <head>\n" +
                    "    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "\n" +
                    "      google.charts.load('current', {'packages':['corechart']});\n" +
                    "\n" +
                    "      google.charts.setOnLoadCallback(drawChart);\n" +
                    "\n" +
                    "      function drawChart() {\n" +
                    "\n" +
                    "        var data = new google.visualization.DataTable();\n" +
                    parsedLabels +
                    parsedValues +
                    "\n" +
                    "        var options = {" +
                    "title: '" + chartTitle + "',\n" +
                    "legend: { position: 'bottom' },\n" +
                    "width: " + webViewer.Width() + ",\n" +
                    "height: " + webViewer.Height() + ",\n" +
                    "hAxis: {" +
                    "   title: '" + hAxisTitle + "'\n" +
                    "                       },\n" +
                    "vAxis: {" +
                    "   title: '" + vAxisTitle + "'\n" +
                    "                       }" +
                    "};\n" +
                    "\n" +
                    "        var chart = new google.visualization.LineChart(document.getElementById('line_graph'));\n" +
                    "        chart.draw(data, options);\n" +

                    "      }\n" +
                    "    </script>\n" +
                    "  </head>\n" +
                    "\n" +
                    "  <body>\n" +
                    "    <div id=\"line_graph\"></div>\n" +
                    "  </body>\n" +
                    "</html>";
            File file = File.createTempFile("display", ".html");
            filePath = file.getAbsolutePath();
            FileWriter filewriter = new FileWriter(file);
            filewriter.write(htmlCode);
            filewriter.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException", e);
        }
        webViewer.GoToUrl("file://" + filePath);
    }

}