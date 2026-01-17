package oop.inheritance;

public class Cat extends Animal {
    Cat() {
        System.out.println("Cat  Constructor");
    }

    @Override
    void makeSound() {
        System.out.println("Meaw Meaw");
    }
}
