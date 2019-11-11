package org.group.sensim;

public class App {
    private int testingNumber;

    public static void main(String[] args) {
        System.out.println("i am starting the app");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int testingNumber = 0;
        System.out.println("\nfinished!" + testingNumber);
    }
}
