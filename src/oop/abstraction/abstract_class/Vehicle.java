package oop.abstraction.abstract_class;

abstract class Vehicle {
    String brand;

    Vehicle(String brand) {
        this.brand = brand;
    }

    abstract void start();

    void displayBrand(){
        System.out.println("Brand: " + brand);
    }

}
