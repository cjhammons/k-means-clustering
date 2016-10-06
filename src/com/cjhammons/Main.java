package com.cjhammons;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import javax.swing.JFrame;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

/**
 * Author: Curtis Hammons
 */
public class Main {

    static final int NUM_CLUSTERS = 3;
    List<Point> allPoints;

    /**
     * Simple class that holds (x, y) coordinates
     */
    class Point {
        double x, y;

        /**
         * Constructor
         * @param _x x coordinate of the point
         * @param _y y coordinate of the point
         */
        public Point(double _x, double _y) {
            x = _x;
            y = _y;
        }
    }

    /**
     * Class to represent a cluster of points.
     */
    class Cluster {
        List<Point> pointList;
        Point centroid;

        /**
         * Constructor
         * @param _pointList List of points in the cluster
         */
        public Cluster(List<Point> _pointList) {
            pointList = _pointList;
        }

        public Point getCentroid() {
            return centroid;
        }

        public void setCentroid(Point centroid) {
            this.centroid = centroid;
        }
    }

    void readFile(){
        String fileName = "A.txt";
        try {
            FileReader fileReader = new FileReader(fileName);


        } catch (FileNotFoundException e) {
            System.out.println(fileName + " does not exist");
        }
    }

    public static void main(String[] args) {

    }


}