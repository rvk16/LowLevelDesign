package oop.abstraction.abstract_class;

public class Main {
    public static void main(String[] args) {
        Vehicle myCar = new Car("Toyota");
        myCar.displayBrand();
        myCar.start();

        Vehicle myBike = new Car("Hunter350");
        myBike.displayBrand();
        myBike.start();
    }
}
