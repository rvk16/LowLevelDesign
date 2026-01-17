package oop.abstraction.abstract_class;

class Bike extends Vehicle {

    Bike(String brand) {
        super(brand);
    }

    @Override
    void start() {
        System.out.println("Bike is starting");
    }
}
