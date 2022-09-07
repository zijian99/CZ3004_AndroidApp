package com.example.ntugroup35;

/**
 * Interface for coordinates extended class {@link Obstacle} n
 */
public interface Coordinates {
    int getX();

    int getY();

    void setCoordinates(int x, int y);

    boolean containsCoordinate(int x, int y);
}
