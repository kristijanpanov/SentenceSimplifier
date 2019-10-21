package org.group.sensim;

public class App {
    public static void main(String[] args){
        System.out.println("i am starting the app");

        try {
            Thread.sleep(1000);
        }
        catch (Exception e){
           e.printStackTrace();
        }

        System.out.println("\nfinished!");
    }
}
