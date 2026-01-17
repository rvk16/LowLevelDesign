package oop.inheritance;

public class Dog extends Animal{

    Dog(){
        super();
        System.out.println("Dog Constructor");
    }

    @Override
    void makeSound()
    {
        System.out.println("Bhow Bhow");
    }

}
