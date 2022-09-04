package com.example.ntugroup35;

public class Obstacle implements Coordinates {
    private int x;
    private int y;
    private int numberObs;
    private int targetID;
    private char side;
    private boolean isExplored;

    public Obstacle(int numberObs){
        this.x = -1;
        this.y = -1;
        this.numberObs = numberObs;
        this.isExplored = false;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    @Override
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean containsCoordinate(int x, int y) {
        if (this.x == x && this.y == y){
            return true;
        }
        return false;
    }

    public int getNumberObs(){
        return this.numberObs;
    }
    public void setNumberObs(int numberObs){
        this.numberObs = numberObs;
    }
    public int getTargetID(){
        return this.targetID;
    }
    public char getSide(){
        return this.side;
    }

    public boolean setSide(char side){
        if (side != 'N' && side != 'S' && side != 'E' && side != 'W'){
            return false;
        } else {
            this.side = side;
            return true;
        }
    }

    public void explore(int targetID){
        this.targetID = targetID;
        isExplored = true;
    }

    public boolean isExplored(){
        return this.isExplored;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Obstacle){
            Obstacle obstacle = (Obstacle) o;
            return x == obstacle.getX() && y == obstacle.getY();
        } else {
            return false;
        }
    }

}
