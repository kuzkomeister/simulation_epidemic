package main;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {

        /*
            Карантин для областей (по квадратикам)
            Привязывать больницу к области (к квадратику)


         */

        final int MAX_ITER = 10000;

        Simulation simulation = new Simulation(
                64,  64,
                1900,0,0, 0,
                0, 0,0,0,
                50,0, 0,0,
                50,0,0, 0,
                0,0,0,0,
                // Настройки людей
                new ConfigForHuman(
                        90,110,
                        140,160,
                        130,150,
                        50,100,
                        100,200,
                        50,100,
                        400,600,
                        900,1000,
                        0.4f, 0.1f,
                        0.1f,
                        0.5f, 0.25f,
                        2.0f, 0.3f, 1.5f,0.5f
                ), null,
                // Больница
                /*
                new Hospital(
                        10,
                        0.05f,0.95f,
                        200,300,
                        200,300
                ),
                 */
                "C:\\Users\\User\\Desktop\\output.txt"
        );
        System.out.println("Инициализирована!");

        //GUI_Main_Menu menu = new GUI_Main_Menu(64,64,simulation.getPeople(),null);

        
        PaintingMap map = new PaintingMap(64,64,Human.config.radiusMan,simulation.getPeople(),simulation.getSneeze(),simulation.getRoot());
        map.setVisible(true);

        long oldTime = System.currentTimeMillis();
        boolean STOP = true;

        for (int iter = 0; iter < MAX_ITER; iter++){
            simulation.iterate();
            map.repaint();
            if (simulation.getIterFinal() != 0 && STOP){
                STOP = false;
                simulation.printInfoStat();
            }
            Thread.sleep(1);
        }
        simulation.closeFile();

        long newTime = System.currentTimeMillis();
        long delta = (newTime - oldTime)/1000;
        System.out.println("Прошло в секундах: "+delta);
        delta /= 60.0f;
        System.out.println("       в минутах: "+delta);



    }
}