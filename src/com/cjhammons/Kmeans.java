package com.cjhammons;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that holds the Kmeans implementation as well as
 * all supporting methods and data structures.
 *
 * Author: Curtis Hammons
 */
public class Kmeans extends JFrame{

    private static final int NUM_CLUSTERS = 2;
    private static final double OFFSET_MARGIN = 0.05;

    private List<Point> allPoints = new ArrayList<>();
    List<Cluster> clusters = new ArrayList<>();
    int numCluster = 0;

    /**
     * Simple class that holds (x, y) coordinates
     */
    public class Point {
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

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    /**
     * Class to represent a cluster of points.
     */
    public class Cluster {
        List<Point> pointList;
        Point centroid;
        Point prevCentroid;
        int clusterId;

        /**
         * Constructor
         * @param _pointList List of points in the cluster
         */
        public Cluster(List<Point> _pointList) {
            pointList = _pointList;
            clusterId = numCluster;
            calculateCentroid();
            numCluster++;
        }

        /**
         * Calculates the cluster's centroid.
         * Sets the local centroid variable to the result.
         */
        public void calculateCentroid() {
            prevCentroid = centroid;
            double cx = 0;
            double cy = 0;
            for (int i = 0; i < pointList.size(); i++) {
                Point p = pointList.get(i);
                cx += p.x;
                cy += p.y;
            }
            centroid = new Point(cx / pointList.size(), cy / pointList.size());
            System.out.println("Cluster " + clusterId + "'s centroid is now: " + centroid.toString());
        }

        /**
         * Calculates the distance shifted between previous and current centroids.
         * All it does is call getDistance(), in hindsight it was probably unnecessary.
         */
        public double calculateShift() {
            return getDistance(centroid, prevCentroid);
        }

        public int getClusterId() {
            return clusterId;
        }

        public void addPoint(Point p){
            if (!pointList.contains(p)) {
                pointList.add(p);
            }
        }

        public void removePoint(Point p){
            if (pointList.contains(p)) {
                pointList.remove(p);
            }
        }

        public Point getCentroid() {
            return centroid;
        }

        public void setCentroid(Point centroid) {
            this.centroid = centroid;
        }
    }

    /**
     * Reads points form the provided file and adds them to the master point list.
     */
     void readFile(){
        String fileName = "B.txt";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            //Create points from the data in the file
            while ((line = reader.readLine()) != null) {
                //Split at the space
                String[] lineSplit = line.split("\\s+");
                Point p = new Point(Double.parseDouble(lineSplit[0]), Double.parseDouble(lineSplit[1]));
                allPoints.add(p);
                System.out.println("point " + p.toString() + " added");
            }

        } catch (FileNotFoundException e) {
            System.out.println(fileName + " does not exist");
        } catch (IOException e) {
            System.out.println("Error reading " + fileName);
        }
    }

    /**
     * Calculates Euclidian distance between two points
     * @param a point a
     * @param b point b
     * @return distance
     */
    double getDistance(Point a, Point b) {
        //Distance formula in java form
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    /**
     * Implementation of the kmeans algorithm
     */
    public void kmeans() {
        readFile();

        //Get initial randomized cluster centroids
        List initCentroids = new ArrayList();
        Random rand = new Random();
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Point p = allPoints.get(rand.nextInt(allPoints.size()));

            //Check if point is already in init list. if it is, decrement i so loop
            //will repeat without adding anything
            if (!initCentroids.contains(p)) {
//                p.setCentroid(true);
                initCentroids.add(p);
                System.out.println(p.toString() + " is initial centroid.");
                Cluster clust = new Cluster(new ArrayList<>());
                clust.addPoint(p);
                clust.calculateCentroid();
                clusters.add(clust);
            } else {
                i--;
            }
        }

        //Loop Forever until convergence is achieved
        int loopCount = 0;
        while (true) {
            System.out.println("Loop: " + loopCount++);

            //Group all points into the cluster whose centroid they are closest to
            for (Point point : allPoints) {
                //Get shortest distance to first cluster's centroid
                double shortestDist = getDistance(point, clusters.get(0).getCentroid());

                int clusterIndex = 0;
                //Find distance to remaining clusters
                for (int i = 1; i < NUM_CLUSTERS; i++){

                    double dist = getDistance(point, clusters.get(i).getCentroid());
                    //Check if distance is shorter than current shortest distance
                    if (dist < shortestDist) {
                        shortestDist = dist;
                        if (clusters.get(clusterIndex).pointList.contains(point)) {
                            clusters.get(clusterIndex).removePoint(point);
                        }
                        clusterIndex = i;
                    }
                }
                //Add point to it's closest centroid's cluster
                clusters.get(clusterIndex).addPoint(point);
            }

            //Calculate the new centroids for all the clusters.
            boolean convergence = true;
            for (int i = 0; i < clusters.size(); i++) {
                Point curCentroid = clusters.get(i).getCentroid();
                clusters.get(i).calculateCentroid();
                //If centroid changed, we do not have convergence
                double shift = clusters.get(i).calculateShift();
                if (shift > OFFSET_MARGIN){
                    convergence = false;
                }
            }

            //If centroid did not change for all clusters, we have achieved convergence
            //and can end the algorithm.
            if (convergence) {
                calculateDistortion();
                plot();
                return;
            }
        }
    }

    /**
     * Calculates the distortion of the resulting clusters
     * @return distortion value
     */
    double calculateDistortion() {
        double result = 0;
        for (Cluster clust : clusters) {
            double sum = 0;
            for (Point point : allPoints) {
                sum += Math.pow(getDistance(point, clust.getCentroid()), 2);
            }
            result += sum;
        }
        System.out.println("Distortion is: " + result);
        return result;
    }

    /**
     * Creates a scatter plot of all the points with color coded clusters.
     * This is hardcoded to 3 clusters, since that is what the assignment required.
     *
     * Requires GRAL java graphing library: http://trac.erichseifert.de/gral/
     */
    void plot() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        List<DataTable> dataTables = new ArrayList<>();
        List<DataSeries> dataSeries = new ArrayList<>();
        //init plot with blank datatable
        XYPlot plot = new XYPlot(new DataTable(double.class, double.class));
        DataTable centroids = new DataTable(Double.class, Double.class);
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            Point centroid = clusters.get(i).getCentroid();
            centroids.add(centroid.x, centroid.y);
            DataTable data = new DataTable(Double.class, Double.class);
            for (Point point : clusters.get(i).pointList) {
                data.add(point.x, point.y);
            }
            DataSeries series = new DataSeries("Series" + i, data, 0, 1);
            dataTables.add(data);
            dataSeries.add(series);
            plot.add(series);
        }
        plot.add(centroids);
        //Color the clusters the appropiate color.
        //...I am not proud of this code. May God forgive me.
        for (int i = 0; i < NUM_CLUSTERS; i++) {
            PointRenderer renderer = new DefaultPointRenderer2D();
            Color color;
            switch (i) {
                case 1:
                    color = new Color(1.0f, 0.0f, 0.0f);
                    break;
                case 2:
//                    color = new Color(0.0f, 1.0f, 0.0f);
//                    break;
//                case 3:
                default:
                    color = new Color(0.0f, 0.5f, 1.0f);
            }
            renderer.setColor(color);
            plot.setPointRenderers(dataSeries.get(i), renderer);
        }

        PointRenderer centroidRender = new DefaultPointRenderer2D();
        centroidRender.setColor(new Color(0.0f, 0.0f, 0.0f));
        plot.setPointRenderers(centroids, centroidRender);


        getContentPane().add(new InteractivePanel(plot));
        setVisible(true);

    }
}
