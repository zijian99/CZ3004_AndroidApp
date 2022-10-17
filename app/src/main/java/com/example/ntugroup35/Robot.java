package com.example.ntugroup35;

import android.util.Log;

public class Robot implements Coordinates {
    /**
     * X coordinate
     */
    private int x;
    /**
     * Y coordinate
     */
    private int y;
    /**
     * Status description of the robot
     */
    private String status;
    /**
     * Direction of the robot facing (N,S,W,E)
     */
    private char direction;

    /**
     * Constructor of the robot
     */
    public Robot(){
        direction = 'N';
        x = -1;
        y = -1;
    }

    /**
     * Inherit class from the interface {@link Coordinates}
     *
     * @return x coordinate
     */
    @Override
    public int getX() {
        return this.x;
    }
    /**
     * Set x coordinates
     *
     * @param x x coordinate
     */
    public void setX(int x){
        this.x = x;
    }
    /**
     * Get center coordinate of robot
     *
     * @return x coordinate
     */
    public int getMiddleX(){return this.x+1;}
    /**
     * Inherit class from the interface {@link Coordinates}
     *
     * @return y coordinate
     */
    @Override
    public int getY() {
        return this.y;
    }
    /**
     * Get center coordinate of robot
     *
     * @return y coordinate
     */
    public int getMiddleY(){return this.y+1;}
    /**
     * Set y coordinates
     *
     * @param y y coordinate
     */
    public void setY(int y){
        this.y = y;
    }

    /**
     * Set coordinates of the robot
     * @param x x coordinate
     * @param y y coordinate
     */
    @Override
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Check whether current coordinate contains anything
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return boolean
     */
    @Override
    public boolean containsCoordinate(int x, int y) {
        Log.d("PC,RP,", "" + this.x + "," + this.y);
        if (this.x <= x && x <= (this.x + 2) && this.y <= y && y <= (this.y + 2)){
            return true;
        }
        return false;
    }

    /**
     * Get status of robot
     *
     * @return string of status of robot
     */
    public String getStatus(){
        return status;
    }

    /**
     * Set status of robot
     *
     * @param status string of status of robot
     */
    public void setStatus(String status){
        this.status = status;
    }

    /**
     * Get the direction of robot
     *
     * @return direction character of the robot
     */
    public char getDirection(){
        return direction;
    }

    /**
     * Set current direction of the robot
     *
     * @param direction direction character of the robot
     */
    public void setDirection(char direction){
        this.direction = direction;
    }

    /**
     * Move robot forward by 1 unit of coordinate
     */
    // "W"
    public void moveRobotForward(){
        char robotDirection = getDirection();
        if (this.x != -1 && this.y != -1){
            if(robotDirection == 'N'){
                int newY = this.y + 1;
                if (newY <= 18){
                    this.setY(newY);
                }
            }else if (robotDirection == 'S'){
                int newY = this.y - 1;
                if (newY >= 1 ){
                    this.setY(newY);
                }
            }else if (robotDirection == 'E'){
                int newX = this.x + 1;
                if (newX <= 18){
                    this.setX(newX);
                }
            }else{
                // West or W
                int newX = this.x - 1;
                if (newX >= 1){
                    this.setX(newX);
                }
            }
        }
    }
    /**
     * Move robot backward by 1 unit of coordinate
     */
    // "S"
    public void moveRobotBackward(){
        char robotDirection = getDirection();
        if (this.x != -1 && this.y != -1){
            if(robotDirection == 'N'){
                int newY = this.y - 1;
                if (newY >= 1){
                    this.setY(newY);
                }
            }else if (robotDirection == 'S'){
                int newY = this.y + 1;
                if (newY <= 18){
                    this.setY(newY);
                }
            }else if (robotDirection == 'E'){
                int newX = this.x - 1;
                if (newX >= 1){
                    this.setX(newX);
                }
            }else{
                // W
                int newX = this.x + 1;
                if (newX <= 18){
                    this.setX(newX);
                }
            }
        }
    }
    /**
     * Turn robot facing direction to left
     */
    // "A"
    public void moveRobotTurnLeft(){
        if (this.x != -1 && this.y != -1){
            char robotDirection = this.getDirection();
            if (robotDirection == 'N'){
                this.setDirection('W');
            }
            else if (robotDirection == 'S'){
                this.setDirection('E');
            }
            else if (robotDirection == 'E'){
                this.setDirection('N');
            }
            else{
                //W
                this.setDirection('S');
            }
        }
    }
    /**
     * Turn robot facing direction to right
     */
    // "D"
    public void moveRobotTurnRight(){
        if (this.x != -1 && this.y != -1){
            char robotDirection = this.getDirection();
            if (robotDirection == 'N'){
                this.setDirection('E');
            }
            else if (robotDirection == 'S'){
                this.setDirection('W');
            }
            else if (robotDirection == 'E'){
                this.setDirection('S');
            }
            else{
                //W
                this.setDirection('N');
            }
        }
    }
    /**
     * Make robot turn a hard left
     */
    // "Q"
    public void moveRobotHardLeft(){
        if (this.x != -1 && this.y != -1){
            char robotDirection = this.getDirection();
            if (robotDirection == 'N'){
                int newX = this.x - 3;
                int newY = this.y + 3;
                if (newX >= 1 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('W');}
            }
            else if (robotDirection == 'S'){
                int newX = this.x + 3;
                int newY = this.y - 3;
                if (newX <= 18 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('E');}
            }
            else if (robotDirection == 'E'){
                int newX = this.x + 3;
                int newY = this.y + 3;
                if (newX <= 18 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('N');}
            }
            else{
                //W
                int newX = this.x - 3;
                int newY = this.y - 3;
                if (newX >= 1 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('S');}
            }
        }
    }
    /**
     * Make robot turn a reverse hard left
     */
    // "Q"
    public void moveRobotHardLeftReverse(){
        if (this.x != -1 && this.y != -1){
            char robotDirection = this.getDirection();
            if (robotDirection == 'N'){
                int newX = this.x - 3;
                int newY = this.y - 3;
                if (newX >= 1 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('E');}
            }
            else if (robotDirection == 'S'){
                int newX = this.x + 3;
                int newY = this.y + 3;
                if (newX <= 18 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('W');}
            }
            else if (robotDirection == 'E'){
                int newX = this.x - 3;
                int newY = this.y + 3;
                if (newX >= 1 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('S');}
            }
            else{
                //W
                int newX = this.x + 3;
                int newY = this.y - 3;
                if (newX <= 18 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('N');}
            }
        }
    }
    /**
     * Make robot turn a hard right
     */
    // "E"
    public void moveRobotHardRight(){
        if (this.x != -1 && this.y != -1){
            char robotDirection = this.getDirection();
            if (robotDirection == 'N'){
                int newX = this.x + 3;
                int newY = this.y + 3;
                if (newX <= 18 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('E');}
            }
            else if (robotDirection == 'S'){
                int newX = this.x - 3;
                int newY = this.y - 3;
                if (newX >= 1 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('W');}
            }
            else if (robotDirection == 'E'){
                int newX = this.x + 3;
                int newY = this.y - 3;
                if (newX <= 18 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('S');}
            }
            else{
                //W
                int newX = this.x - 3;
                int newY = this.y + 3;
                if (newX >= 1 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('N');}
            }
        }
    }
    /**
     * Make robot turn a reverse hard right
     */
    public void moveRobotHardRightReverse(){
        if (this.x != -1 && this.y != -1){
            char robotDirection = this.getDirection();
            if (robotDirection == 'N'){
                int newX = this.x + 3;
                int newY = this.y - 3;
                if (newX <= 18 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('W');}
            }
            else if (robotDirection == 'S'){
                int newX = this.x - 3;
                int newY = this.y + 3;
                if (newX >= 1 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('E');}
            }
            else if (robotDirection == 'E'){
                int newX = this.x - 3;
                int newY = this.y - 3;
                if (newX >= 1 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('N');}
            }
            else{
                //W
                int newX = this.x + 3;
                int newY = this.y + 3;
                if (newX <= 18 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('S');}
            }
        }
    }
    /**
     * Reset the robot to default
     */
    public void reset(){
        setCoordinates(-1,-1);
        this.direction = 'N';
    }

}