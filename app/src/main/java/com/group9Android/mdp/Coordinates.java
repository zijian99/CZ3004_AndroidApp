package com.group9Android.mdp;

public interface Coordinates {
    int getX();

    int getY();

    void setCoordinates(int x, int y);

    boolean containsCoordinate(int x, int y);
}
