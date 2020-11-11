package main;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        // Осталось рандомное распределение масок и соц дист, соблюдение соц дистанции


        Simulation simulation = new Simulation(
                // Размер карты
                70, 70,
                // Количество людей в состояниях
                900,50,50, 0,0,0,
                // Количество людей: в масках, соблюдающих соц дистанцию, моющих людей
                1000,0,
                // Интервал для времени между чихами
                90,110,

                //===== Все что связано с руками и лицом
                // Интервал для времени между контактом рук с лицом
                140,160,
                // Интервал для времени между мытьем рук
                140,160,
                // Интервал для пожатия рук
                100,200,
                // Интервал для "загрязнения" своих рук
                50,100,
                // Вероятность заразиться от контакта рук с лицом
                0.5f,

                // Интервал для смены направления движения
                50,100,
                // Интервал для выхода из инкубационного и клинического периода
                400,600,
                900,1000,

                // Вероятности стать: бессимптомным больным, умереть
                0.3f, 0.2f,
                // Эффективность защиты маски
                0.5f,0.25f,
                // Радиусы: соц. дистанции, человека, заражения
                2.0f, 0.2f,1.0f,
                // адрес и название файла вывода
                "C:\\Users\\User\\Desktop\\output.txt"
        );

        for (int iter = 0; iter < 5000; iter++){
            simulation.iterate();
        }
        simulation.printInfoStat();
        simulation.closeFile();





    }

}
