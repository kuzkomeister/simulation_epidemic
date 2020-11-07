package main;

import java.util.ArrayList;

public class Simulation {

    private ArrayList<Man> people;    // список жалких подопытных людишек
    private long iter = 0;            // номер текущей итерации
    //========== Настройки карты/симуляции
    private float sizeX, sizeY; // размеры карты по х и у
    private int amountMans;     // количество людей на карте при старте симуляции
    private float radiusMan;         // размер человека
    private float radiusManSqr;      // размер человека (в квадрате)
    //========== Настройки человеков
    // интервалы для рандома
    public final int timeRecovery_a, timeRecovery_b;        // для выздоровления
    public final int timeSneeze_a, timeSneeze_b;            // для чихания
    public final int timeChangeDirect_a, timeChangeDirect_b;// для смены направления движения
    //========== Настройки болезни
    private float radiusInf;        // радиус заражения
    private float radiusInfSqr;     // радиус заражения (в квадрате)

    private float radiusSoc;        // радиус социального дистанцирования
    private float radiusSocSqr;     // радиус социального дистанцирования (в квадрате)
    //========== Статистика
    private long stContacts = 0;    // количество контактов за сессию
    private int iterFinal = 0;      // номер итерации, на которой закончились изменения
    // Количество здоровых,инфицированных в инкубац. периоде, инфиц. с симптомами, инфиц. без симптомов
    // выздоровевших, умерших в текущий момент времени
    private int amountZd, amountInfInc, amountInfSymp, amountInfNotSymp, amountVzd, amountDied;
    //=====================


    // Конструктор
    public Simulation(float sizeX, float sizeY, int amountMans, int amountInf,
                      int timeRecovery_a, int timeRecovery_b, int timeChangeDirect_a, int timeChangeDirect_b, int timeSneeze_a, int timeSneeze_b
    ){
        people = new ArrayList<>();
        //===== Настройки карты, симуляции
        this.sizeX = sizeX; this.sizeY = sizeY;
        this.amountMans = amountMans+amountInf;
        amountZd = amountMans;
        amountInfInc = 0;
        amountInfSymp = amountInf;
        amountInfNotSymp = 0;
        amountVzd = 0;
        amountDied = 0;

        radiusMan = 0.2f;
        radiusManSqr = (float) Math.pow(radiusMan,2);
        //===== Настройки болезни
        radiusInf = 1.5f;
        radiusInfSqr = (float) Math.pow(radiusInf,2);
        //===== Настройки человека
        this.timeRecovery_a = timeRecovery_a;
        this.timeRecovery_b = timeRecovery_b;
        this.timeChangeDirect_a = timeChangeDirect_a;
        this.timeChangeDirect_b = timeChangeDirect_b;
        this.timeSneeze_a = timeSneeze_a;
        this.timeSneeze_b = timeSneeze_b;

        //=== заполнение списка людей на карте случайным образом
        float rx,ry;  // Рандомные координаты
        boolean f;  // Флаг поиска свободного места
        // Заполняем инфицированных
        for (int i = 0; i < amountInf; ++i) {
            // Поиск свободного места
            do{
                rx = (float)Math.random() * sizeX;
                ry = (float)Math.random() * sizeY;
                f = this.checkPlace(rx,ry);
            } while (!f);
            // Создание человечека
            people.add(new Man((byte)1,rx,ry));
        }
        // Заполняем здоровых
        for (int i = 0; i < amountMans; ++i) {
            // Поиск свободного места
            do{
                rx = (float) (Math.random() * sizeX);
                ry = (float) (Math.random() * sizeY);
                f = this.checkPlace(rx,ry);
            } while (!f);
            // Создание человечека
            people.add(new Man((byte)0,rx,ry));
        }
    }

    // Проверка на возможность перемещения бойчика по карте
    public boolean checkBarrier(float x, float y){
        if ((x-this.radiusManSqr > 0) && (x+this.radiusManSqr < sizeX)) {
            return (y-this.radiusManSqr > 0) && (y+this.radiusManSqr < sizeY);
        }
        else return false;
    }

    // Проверка на свободность места на карте
    public boolean checkPlace(float x, float y){
        boolean res = true;
        for (Man temp:this.people){
            if ((Math.pow(x-temp.getX(),2)+Math.pow(y-temp.getY(),2)) < 4* radiusManSqr){
                res = false;
                break;
            }
        }
        return res;
    }

    // Произвести контакт между "man" и ближайшими людьми
    // только если "man" инфицирован
    public void makeContact(Man man){
        if (man.getCondition() == 1){
            // Поиск людей находящихся рядом с "man"
            for (Man temp:people) {
                // Проверка на возможность контакта
                if ((Math.pow(man.getX() - temp.getX(), 2) + Math.pow(man.getY() - temp.getY(), 2)) < radiusInfSqr){
                    stContacts++;  // статистика
                    // Если человек здоровый
                    if (temp.getCondition() == 0)
                        // если вероятность на его стороне
                        if (Math.random() < (man.getProbabilityInfection() * temp.getProbabilityGetInfection())) {
                            // то заражает
                            temp.setCondition((byte) 1);
                            // устанавливаем время выздоровления
                            temp.setTimeRecovery((int)(Math.random()*(timeRecovery_b-timeRecovery_a+1)+timeRecovery_a));
                            // статистика
                            setAmountCond(-1,0,1,0,0,0);
                        }
                }
            }

        }
    }

    // Заводим моторчик нашей мапы
    public void iterate() {
        for (Man temp : people) {
            temp.doDela(this);
        }
        iter++;
    }

    //=================================================
    // для дебага
    public void printInfoStat(){
        System.out.println("=======================================================");
        System.out.println("Здоровых: "+amountZd);
        System.out.println("Инфицированных в инкубационном периоде: "+amountInfInc);
        System.out.println("Инфицированных в клиническом периоде: "+amountInfSymp);
        System.out.println("Инфицированных без проявления симптомов: "+amountInfNotSymp);
        System.out.println("Выздоровевших: "+amountVzd);
        System.out.println("Смертей: "+amountDied);
    }
    //======== Статистика
    // изменение статистики количества людей в различных состояниях
    public void setAmountCond(int diffZd, int diffInfInc, int diffInfSymp, int diffInfNotSymp, int diffVzd, int diffDied){
        if ((diffZd + diffInfInc + diffInfSymp + diffInfNotSymp + diffVzd + diffDied) == 0){
            amountZd+=diffZd;
            amountInfInc+=diffInfInc;
            amountInfSymp+=diffInfSymp;
            amountInfNotSymp+=diffInfNotSymp;
            amountVzd+=diffVzd;
            amountDied+=diffDied;
        }
    }

    public ArrayList<Man> getPeople(){
        return people;
    }
    //======== Размеры карты
    public void setSizeX(float sizeX){
        this.sizeX = sizeX;
    }

    public float getSizeX(){
        return this.sizeX;
    }

    public void setSizeY(float sizeY){
        this.sizeY = sizeY;
    }

    public float getSizeY(){
        return this.sizeY;
    }
}
