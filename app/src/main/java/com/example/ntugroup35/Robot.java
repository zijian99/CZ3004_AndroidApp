package com.example.ntugroup35;

import android.util.Log;

public class Robot implements Coordinates {
    private int x;
    private int y;
    private String status;
    private char direction;

    public Robot(){
        direction = 'N';
        x = -1;
        y = -1;
    }

    @Override
    public int getX() {
        return this.x;
    }

    public void setX(int x){
        this.x = x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    public void setY(int y){
        this.y = y;
    }

    @Override
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean containsCoordinate(int x, int y) {
        Log.d("PC,RP,", "" + this.x + "," + this.y);
        if (this.x <= x && x <= (this.x + 2) && this.y <= y && y <= (this.y + 2)){
            return true;
        }
        return false;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public char getDirection(){
        return direction;
    }

    public void setDirection(char direction){
        this.direction = direction;
    }

    // "W"
    public void moveRobotForward(){
        char robotDir = getDirection();
        if (this.x != -1 && this.y != -1){
            if(robotDir == 'N'){
                int newY = this.y + 1;
                if (newY <= 18){
                    this.setY(newY);
                }
            }else if (robotDir == 'S'){
                int newY = this.y - 1;
                if (newY >= 1 ){
                    this.setY(newY);
                }
            }else if (robotDir == 'E'){
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
    // "S"
    public void moveRobotBackward(){
        char robotDir = getDirection();
        if (this.x != -1 && this.y != -1){
            if(robotDir == 'N'){
                int newY = this.y - 1;
                if (newY >= 1){
                    this.setY(newY);
                }
            }else if (robotDir == 'S'){
                int newY = this.y + 1;
                if (newY <= 18){
                    this.setY(newY);
                }
            }else if (robotDir == 'E'){
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
    // "A"
    public void moveRobotTurnLeft(){
        if (this.x != -1 && this.y != -1){
            char robotDir = this.getDirection();
            if (robotDir == 'N'){
                this.setDirection('W');
            }
            else if (robotDir == 'S'){
                this.setDirection('E');
            }
            else if (robotDir == 'E'){
                this.setDirection('N');
            }
            else{
                //W
                this.setDirection('S');
            }
        }
    }
    // "D"
    public void moveRobotTurnRight(){
        if (this.x != -1 && this.y != -1){
            char robotDir = this.getDirection();
            if (robotDir == 'N'){
                this.setDirection('E');
            }
            else if (robotDir == 'S'){
                this.setDirection('W');
            }
            else if (robotDir == 'E'){
                this.setDirection('S');
            }
            else{
                //W
                this.setDirection('N');
            }
        }
    }
    // "Q"
    public void moveRobotHardLeft(){
        if (this.x != -1 && this.y != -1){
            char robotDir = this.getDirection();
            if (robotDir == 'N'){
                int newX = this.x - 3;
                int newY = this.y + 3;
                if (newX >= 1 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('W');}
            }
            else if (robotDir == 'S'){
                int newX = this.x + 3;
                int newY = this.y - 3;
                if (newX <= 18 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('E');}
            }
            else if (robotDir == 'E'){
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
    // "E"
    public void moveRobotHardRight(){
        if (this.x != -1 && this.y != -1){
            char robotDir = this.getDirection();
            if (robotDir == 'N'){
                int newX = this.x + 3;
                int newY = this.y + 3;
                if (newX <= 18 && newY <= 18){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('E');}
            }
            else if (robotDir == 'S'){
                int newX = this.x - 3;
                int newY = this.y - 3;
                if (newX >= 1 && newY >= 1){
                    this.setX(newX);
                    this.setY(newY);
                    this.setDirection('W');}
            }
            else if (robotDir == 'E'){
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

    public void reset(){
        setCoordinates(-1,-1);
        this.direction = 'N';
    }

}