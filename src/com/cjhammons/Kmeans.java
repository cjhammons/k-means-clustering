package com.cjhammons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds the Kmeans implementation as well as all supporting methods
 * and data structures.
 *
 * Author: Curtis Hammons
 */
public class Kmeans {

    static final int NUM_CLUSTERS = 3;
    List<Point> allPoints = new ArrayList<>();

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

    /**
     * Reads points form the provided file and adds them to the master point list.
     */
     void readFile(){
        String fileName = "A.txt";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            //Create points from the data in the file
            while ((line = reader.readLine()) != null) {
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
     * Implementation of the kmeans algorithm
     */
    public void kmeans() {
        readFile();
    }
}
