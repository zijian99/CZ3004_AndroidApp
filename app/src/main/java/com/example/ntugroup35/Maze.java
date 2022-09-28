package com.example.ntugroup35;

import java.util.ArrayList;

public class Maze {
    private static Maze maze;

    /**
     * Get the maze object
     *
     * @return maze
     */
    public static Maze getInstance(){
        if (maze == null){
            maze = new Maze();
        }
        return maze;
    }

    /**
     * Constructor
     */
    private Maze(){}

    /**
     * Array list of obstacles
     */
    private final ArrayList<Obstacle> obstacles = new ArrayList<>();

    /**
     * Get obstacle array list
     *
     * @return
     */
    public ArrayList<Obstacle> getObstacles(){
        return this.obstacles;
    }

    /**
     * Add new obstacle to array list
     * @return
     */
    public Obstacle addObstacle(){
        int number = this.obstacles.size() + 1;
        Obstacle newObstacle = new Obstacle(number);
        this.obstacles.add(newObstacle);
        return newObstacle;
    }

    /**
     * Remove obstaclefrom array list
     * @param obstacle
     */
    public void removeObstacle(Obstacle obstacle){
        int indexToRemove = obstacle.getNumberObs() - 1;
        this.obstacles.remove(indexToRemove);
        for (int i = indexToRemove; i < this.obstacles.size(); i++){
            this.obstacles.get(i).setNumberObs(i + 1);
        }
    }
    public void clearMap(){
        System.out.println("clearing");
        this.obstacles.clear();

    }
    /**
     * Find obstacle using coordinates
     * @param x
     * @param y
     * @return
     */
    public Coordinates findObstacle(int x, int y){
        for (Coordinates obstacle: this.obstacles){
            if (obstacle.containsCoordinate(x, y)){
                return obstacle;
            }
        }
        return null;
    }
    public int findObstacleNumber(int x, int y){
        for (Obstacle obstacle: this.obstacles){
            if (obstacle.containsCoordinate(x, y)){
                return obstacle.getNumberObs();
            }
        }
        return -1;
    }
    /**
     * Check position is occupied by the obstacle
     * @param x
     * @param y
     * @param obstacle
     * @return
     */
    public boolean isOccupied(int x, int y, Obstacle obstacle){
        for (Obstacle o: this.obstacles){
            if (o.containsCoordinate(x, y) && o != obstacle){
                return true;
            }
        }
        return false;
    }
}
