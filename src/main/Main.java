package main;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        final int MAX_ITER = 50000;

        Simulation simulation = new Simulation(
                32,  32,
                20,0,5, 0,0,
                5,0,0,0,0,
                10,0,1,0,0,
                5,0,0,0,0,
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
                        3.0f, 0.2f, 2.0f,1.0f
                ),
                "C:\\Users\\User\\Desktop\\output.txt"
        );
        System.out.println("Инициализирована!");

        PaintingMap map = new PaintingMap(32,32,Human.config.radiusMan,simulation.getPeople(),simulation.getSneeze());
        map.setVisible(true);

        long time = System.currentTimeMillis();
        for (int iter = 0; iter < MAX_ITER; iter++){
            if (simulation.getIterFinal() == 0){
                simulation.iterate();
                map.repaint();
                Thread.sleep(40);
            }
            else{
                break;
            }
        }
        simulation.printInfoStat();
        System.out.println("========= Время =======");
        long newTime = System.currentTimeMillis();
        System.out.println("Выполнено за "+(float)(newTime-time)/1000+" секунд");
        System.out.println("          за "+(float)(newTime-time)/60000+" минут");
        System.out.println("========= Дебаг =======");
        System.out.println("Количество проверок коллизии: "+simulation.getStChecks());







    }
}