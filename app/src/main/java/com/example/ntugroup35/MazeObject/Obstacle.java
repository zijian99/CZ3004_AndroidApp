package com.example.ntugroup35.MazeObject;

/**
 * Obstacle object
 */
public class Obstacle implements Coordinates {
    /**
     * X coordinate
     */
    private int x;
    /**
     * Y coordinate
     */
    private int y;
    /**
     * Number of Obstacles
     */
    private int numberObs;
    /**
     * Found id of the obstacle
     */
    private int targetID;
    /**
     * Side of obstacle facing at
     */
    private char side;
    /**
     * Boolean of whether obstacle is explored
     */
    private boolean isExplored;

    /**
     * Constructor class
     * @param numberObs number of obstacle
     */
    public Obstacle(int numberObs){
        this.x = -1;
        this.y = -1;
        this.numberObs = numberObs;
        this.isExplored = false;
    }

    /**
     * Get x coordinate of obstacle
     *
     * @return x coordinate
     */
    public int getX(){
        return this.x;
    }

    /**
     * Get Y coordinate of obstacle
     *
     * @return y coordinate
     */
    public int getY(){
        return this.y;
    }

    /**
     * Set coordinate of obstacle
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    @Override
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Check whether coordinates contains anything
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return boolean
     */
    @Override
    public boolean containsCoordinate(int x, int y) {
        return this.x == x && this.y == y;
    }

    /**
     * Get number of obstacle
     *
     * @return number of obstacle
     */
    public int getNumberObs(){
        return this.numberObs;
    }

    /**
     * Set the number of obstacle
     *
     * @param numberObs number of obstacle
     */
    public void setNumberObs(int numberObs){
        this.numberObs = numberObs;
    }

    /**
     * Get the target id
     * @return target id
     */
    public int getTargetID(){
        return this.targetID;
    }

    /**
     * Get the side of the obstacle facing
     * @return side
     */
    public char getSide(){
        return this.side;
    }

    /**
     * Set the side of the obstacle facing
     *
     * @param side character side of obstacle facing
     * @return boolean
     */
    public boolean setSide(char side){
        if (side != 'N' && side != 'S' && side != 'E' && side != 'W'){
            return false;
        } else {
            this.side = side;
            return true;
        }
    }

    /**
     * Explore current obstacle
     * @param targetID target id to update
     */
    public void explore(int targetID){
        this.targetID = targetID;
        isExplored = true;
    }

    /**
     * Check whether current obstacle is explored
     *
     * @return boolean
     */
    public boolean isExplored(){
        return this.isExplored;
    }

    /**
     * Check whether the object is same as current obstacle
     * @param o Object
     * @return boolean
     */
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
