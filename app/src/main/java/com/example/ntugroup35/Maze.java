package com.example.ntugroup35;

import java.util.ArrayList;

public class Maze {
    private static Maze mazemap;
    public static Maze getInstance(){
        if (mazemap == null){
            mazemap = new Maze();
        }
        return mazemap;
    }

    private Maze(){}

    private final ArrayList<Obstacle> obstacles = new ArrayList<>();

    public ArrayList<Obstacle> getObstacles(){
        return this.obstacles;
    }

    // add new obstacle to arrayList of obstacles
    public Obstacle addObstacle(){
        int number = this.obstacles.size() + 1;
        Obstacle newObstacle = new Obstacle(number);
        this.obstacles.add(newObstacle);
        return newObstacle;
    }

    // remove obstacle from arrayList of obstacles
    public void removeObstacle(Obstacle obstacle){
        int indexToRemove = obstacle.getNumberObs() - 1;
        this.obstacles.remove(indexToRemove);
        for (int i = indexToRemove; i < this.obstacles.size(); i++){
            this.obstacles.get(i).setNumberObs(i + 1);
        }
    }

    // find obstacle at a particular x and y coordinate
    public Coordinates findObstacle(int x, int y){
        for (Coordinates obstacle: this.obstacles){
            if (obstacle.containsCoordinate(x, y)){
                return obstacle;
            }
        }
        return null;
    }

    // check if coodinates is occupied by anything other than the obstacle
    public boolean isOccupied(int x, int y, Obstacle obstacle){
        for (Obstacle o: this.obstacles){
            if (o.containsCoordinate(x, y) && o != obstacle){
                return true;
            }
        }
        return false;
    }
}
