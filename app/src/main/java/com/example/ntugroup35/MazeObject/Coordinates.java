package com.example.ntugroup35.MazeObject;

/**
 * Interface for coordinates extended class {@link Obstacle} n {@link Robot}
 */
public interface Coordinates {
    int getX();

    int getY();

    void setCoordinates(int x, int y);

    boolean containsCoordinate(int x, int y);
}
