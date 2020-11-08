package main;

public class Main {

    public static void main(String[] args) {

        Simulation simulation = new Simulation(15, 15,
                90,5,5, 0,0,0,
                50,0,30,
                90,110,
                140,160,
                140,160,
                50,100,
                400,600,
                900,1100,
                0.3f, 0.1f, 0.5f,
                0.5f,0.25f,
                2.0f, 0.2f,1.0f
                );

        simulation.printInfoStat();
        for (int iter = 0; iter < 5000; iter++){
            simulation.iterate();
        }
        simulation.printInfoStat();

    }

}
