package main;

public class Main {

    public static void main(String[] args) {


        Simulation simulation = new Simulation(
                10,10,
                17,3,
                800,1000,
                50,100,
                50,100);

        simulation.iterate();

        simulation.printInfoStat();
    }

}
