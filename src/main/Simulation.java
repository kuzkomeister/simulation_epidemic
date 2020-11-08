package main;

import java.util.ArrayList;

public class Simulation {

    private ArrayList<Man> people;    // список жалких подопытных людишек
    private long iter = 0;            // номер текущей итерации
    //========== Настройки карты/симуляции
    private float sizeX, sizeY; // размеры карты по х и у
    public final int amountMans;     // количество людей на карте при старте симуляции
    public final float radiusMan;    // размер человека // для того, чтобы не накладывались друг на друга
    public final float radiusManSqr; // размер человека (в квадрате)

    public final int amountMask;     // Количество людей с масками
    public final int amountSocDist;  // Количество людей соблюдающих соц. дистанцию
    public final int amountWashHands;    // Количество людей моющих/дезинфицирующих руки
    public final float radiusSoc;        // радиус социального дистанцирования
    public final float radiusSocSqr;     // радиус социального дистанцирования (в квадрате)

    //========== Настройки болезни
    public final float maskProtectionFor;    // Эффективность защиты маски в сторону заражения кого-то
    public final float maskProtectionFrom;   // Эффективность защиты маски в сторону заразиться от кого-то
    public final float radiusInf;        // радиус заражения
    public final float radiusInfSqr;     // радиус заражения (в квадрате)

    // Время
    public final int timeSneeze_a, timeSneeze_b;            // Между чиханиями
    public final int timeHandToFaceContact_a, timeHandToFaceContact_b; // Между контактом рук с лицом
    public final int timeWash_a, timeWash_b;                // Между мытьем/дезинфекцией рук
    public final int timeChangeDirect_a, timeChangeDirect_b;// Между сменой направления движения

    public final int timeInfInc_a, timeInfInc_b;        // Длительность инкубационного периода
    public final int timeRecovery_a, timeRecovery_b;        // для выздоровления

    // Вероятности
    public final float probabilityNotSymp;          // Вероятность стать бессимптомным больным
    public final float probabilityDied;             // Вероятность умереть из-за болезни
    public final float probabilityInfHand;          // Вероятность заразиться от контакта рук с лицом

    //========== Статистика
    private long stContacts = 0;    // количество контактов за сессию
    private int iterFinal = 0;      // номер итерации, на которой закончились изменения
    // Количество здоровых,инфицированных в инкубац. периоде, инфиц. с симптомами, инфиц. без симптомов
    // выздоровевших, умерших в текущий момент времени
    private int amountZd, amountInfInc, amountInfSymp,
                amountInfNotSymp, amountVzd, amountDied;
    //=====================


    // Конструктор
    public Simulation(float sizeX, float sizeY,
                      // Количество людей в состояниях
                      int amountZd, int amountInfInc, int amountInfSymp,
                      int amountInfNotSymp, int amountVzd, int amountDied,

                      int amountMask, int amountSocDist, int amountWashHands,
                      // Время
                      int timeSneeze_a, int timeSneeze_b,
                      int timeHandToFaceContact_a, int timeHandToFaceContact_b,
                      int timeWash_a, int timeWash_b,
                      int timeChangeDirect_a, int timeChangeDirect_b,
                      int timeInfInc_a, int timeInfInc_b,
                      int timeRecovery_a, int timeRecovery_b,
                      // Вероятность
                      float probabilityNotSymp, float probabilityDied,
                      float probabilityInfHand,
                      float maskProtectionFor, float maskProtectionFrom,

                      float radiusSoc, float radiusMan, float radiusInf

    ){

        people = new ArrayList<>();
        //===== Настройки карты, симуляции
        this.sizeX = sizeX; this.sizeY = sizeY;
        this.amountMans = amountZd+amountInfInc+amountInfSymp+amountInfNotSymp+amountVzd+amountDied;
        //===== Количество людей в состояниях...
        this.amountZd = amountZd;
        this.amountInfInc = amountInfInc;
        this.amountInfSymp = amountInfSymp;
        this.amountInfNotSymp = amountInfNotSymp;
        this.amountVzd = amountVzd;
        this.amountDied = amountDied;

        this.amountMask = amountMask;
        this.amountWashHands = amountWashHands;
        this.amountSocDist = amountSocDist;
        this.radiusMan = radiusMan;
        radiusManSqr = (float) Math.pow(radiusMan,2);
        //===== Настройки болезни
        this.radiusInf = radiusInf;
        radiusInfSqr = (float) Math.pow(radiusInf,2);

        this.radiusSoc = radiusSoc;
        radiusSocSqr = (float) Math.pow(radiusSoc,2);
        // Настройки времени
        this.timeSneeze_a = timeSneeze_a;
        this.timeSneeze_b = timeSneeze_b;
        this.timeHandToFaceContact_a = timeHandToFaceContact_a;
        this.timeHandToFaceContact_b = timeHandToFaceContact_b;
        this.timeWash_a = timeWash_a;
        this.timeWash_b = timeWash_b;
        this.timeInfInc_a = timeInfInc_a;
        this.timeInfInc_b = timeInfInc_b;
        this.timeChangeDirect_a = timeChangeDirect_a;
        this.timeChangeDirect_b = timeChangeDirect_b;
        this.timeRecovery_a = timeRecovery_a;
        this.timeRecovery_b = timeRecovery_b;

        // Вероятности
        this.probabilityDied = probabilityDied;
        this.probabilityNotSymp = probabilityNotSymp;
        this.probabilityInfHand = probabilityInfHand;
        this.maskProtectionFor = maskProtectionFor;
        this.maskProtectionFrom = maskProtectionFrom;

        //=== заполнение списка людей на карте случайным образом
        float rx,ry;  // Рандомные координаты
        boolean f;  // Флаг поиска свободного места
        // Заполняем список людей
        int zd = amountZd, infInc = amountInfInc, infSymp = amountInfSymp,
                infNotSymp = amountInfNotSymp, vzd = amountVzd, died = amountDied;

        for (int i = 0; i < amountMans; ++i) {
            // Поиск свободного места
            do{
                rx = (float)Math.random() * sizeX;
                ry = (float)Math.random() * sizeY;
                f = this.checkPlace(rx,ry);
            } while (!f);

            // выбор состояния
            byte cond = 0;
            if (zd != 0){
                zd--;
            }
            else{
                if (infInc != 0){
                    cond = 1;
                    infInc--;
                }
                else{
                    if (infSymp != 0){
                        cond = 2;
                        infSymp--;
                    }
                    else{
                        if (vzd != 0){
                            cond = 3;
                            vzd--;
                        }
                        else{
                            if (died != 0){
                                cond = 4;
                                died--;
                            }
                            else {
                                if (infNotSymp != 0){
                                    cond = 5;
                                    infNotSymp--;
                                }
                            }
                        }
                    }
                }
            }

            // Создание человечека
            people.add(new Man(cond, false,false,maskProtectionFor,maskProtectionFrom,probabilityInfHand,rx,ry));
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
                        temp.setCondition((byte) 2);
                        // устанавливаем время выздоровления
                        temp.setTimeInfInc((int)(Math.random()*(timeInfInc_b-timeInfInc_a+1)+timeInfInc_a));
                        // статистика
                        setAmountCond(-1,0,1,0,0,0);
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

    public void printInfoMans(){
        for (Man man:people){
            System.out.println(man.getCondition()+" | "+man.getX()+" "+man.getY());
        }
    }
    //======== Статистика
    // изменение статистики количества людей в различных состояниях
    public void setAmountCond(int diffZd, int diffInfInc, int diffInfSymp,
                              int diffInfNotSymp, int diffVzd, int diffDied){
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
