package com.cjhammons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that holds the Kmeans implementation as well as all supporting methods
 * and data structures.
 *
 * Author: Curtis Hammons
 */
public class Kmeans {

    private static final int NUM_CLUSTERS = 3;
    private List<Point> allPoints = new ArrayList<>();
    List<Cluster> clusters = new ArrayList<>();

    /**
     * Simple class that holds (x, y) coordinates
     */
    public class Point {
        double x, y;
//        boolean isCentroid;

        /**
         * Constructor
         * @param _x x coordinate of the point
         * @param _y y coordinate of the point
         */
        public Point(double _x, double _y) {
            x = _x;
            y = _y;
//            isCentroid = false;

        }

//        public boolean isCentroid() {
//            return isCentroid;
//        }
//
//        public void setCentroid(boolean centroid) {
//            isCentroid = centroid;
//        }

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
        int cluserId;

        /**
         * Constructor
         * @param _pointList List of points in the cluster
         */
        public Cluster(List<Point> _pointList, int id) {
            pointList = _pointList;
            calculateCentroid();
            cluserId = id;
        }

        /**
         * Calculates the cluster's centroid.
         * Sets the local centroid variable to the result.
         */
        public void calculateCentroid() {
//            centroid.setCentroid(false);
            double cx = 0;
            double cy = 0;
            for (int i = 0; i < pointList.size(); i++) {
                Point p = pointList.get(i);
                cx += p.x;
                cy += p.y;
            }
            centroid = new Point(cx / pointList.size(), cy / pointList.size());

            //had to comment this out because it was overloading intellij's console
//            System.out.println("Cluster " + cluserId + "'s centroid is: " + centroid.toString());
        }

        public int getCluserId() {
            return cluserId;
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
     * Calculates Euclidian distance between two points
     * @param a
     * @param b
     * @return distance
     */
    double getDistance(Point a, Point b) {
        //Distance formula in java form
//        double xDist = Math.pow(a.x -  b.x, 2);
//        double yDist = Math.pow(a.y - b.y, 2);
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
                Cluster clust = new Cluster(new ArrayList<>(), i);
                clust.addPoint(p);
                clust.calculateCentroid();
                clusters.add(clust);
            } else {
                i--;
            }
        }

        int loopCount = 0;
        while (true) {
            System.out.println("Loop: " + loopCount++);
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
                if (curCentroid != clusters.get(i).getCentroid()){
                    convergence = false;
                }
            }
            //If centroid did not change for all clusters, we have achieved convergence
            //and can end the algorithm.
            if (convergence) {
                return;
            }
        }
    }
}
