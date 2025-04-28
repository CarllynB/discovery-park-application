package com.example.discoveryparkmap;

import java.util.ArrayList;
import java.util.List;

public class RouteManager {
    private List<Landmark> landmarks;

    public RouteManager() {
        landmarks = new ArrayList<>();
        loadLandmarks();
    }

    private void loadLandmarks() {
        // Sample landmark coordinates (estimates for now)
        landmarks.add(new Landmark("K Wing Entrance", 1, 100, 300));
        landmarks.add(new Landmark("G Wing Grill Door", 1, 240, 220));
        landmarks.add(new Landmark("D Wing Corner", 1, 470, 170));
        landmarks.add(new Landmark("B Wing Entrance", 1, 430, 400));
        landmarks.add(new Landmark("A Wing Side Door", 1, 260, 530));
        landmarks.add(new Landmark("South F Wing Lobby", 1, 230, 610));
    }

    public List<Landmark> getLandmarks() {
        return landmarks;
    }
}
