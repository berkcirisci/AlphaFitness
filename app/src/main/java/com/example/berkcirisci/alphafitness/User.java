package com.example.berkcirisci.alphafitness;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by berkcirisci on 5.8.2016.
 */
public class User {

    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField
    int weight;

    @DatabaseField
    int height;

    @DatabaseField
    String name;

    public User() {
        // needed by ormlite
    }

    public User(int weight, int height, String name) {
        this.height = height;
        this.weight = weight;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setName(String name) {
        this.name = name;
    }

    final static float walkingFactor = 0.57F;

    public float distanceToCalorie(float distance) {
        float caloriesBurnedPerMile = walkingFactor * (weight * 2.2F);
        distance /=  1609.344F;
        return distance * caloriesBurnedPerMile;
    }
}