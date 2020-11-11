package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Simulation {

    private ArrayList<Man> people;    // список жалких подопытных людишек
    private long iter = 0;            // номер текущей итерации
    private FileWriter file;          // файл в который будет записываться статистика
    //========== Настройки карты/симуляции
    private float sizeX, sizeY; // размеры карты по х и у
    public final int amountMans;     // количество людей на карте при старте симуляции
    public final float radiusMan;    // размер человека // для того, чтобы не накладывались друг на друга
    public final float radiusManSqr; // размер человека (в квадрате)

    public final int amountMask;     // Количество людей с масками
    public final int amountSocDist;  // Количество людей соблюдающих соц. дистанцию
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
    public final int timeHandshake_a, timeHandshake_b;      // Между пожатием рук
    public final int timeInfHand_a, timeInfHand_b;      // Между "загрязнением" своих рук
    public final int timeInfInc_a, timeInfInc_b;        // Длительность инкубационного периода
    public final int timeRecovery_a, timeRecovery_b;    // для выздоровления

    // Вероятности
    public final float probabilityNotSymp;          // Вероятность стать бессимптомным больным
    public final float probabilityDied;             // Вероятность умереть из-за болезни
    public final float probabilityInfHand;          // Вероятность заразиться от контакта рук с лицом

    //========== Статистика
    private long stContacts = 0;    // количество контактов за сессию
    private int stContactInf = 0;   // количество заразившихся от чиха с инфицированным

    private long stHandshakes = 0;   // количество рукопожатий
    private int stHandshakeInf = 0;      // количество заразившихся от контакта рук с лицом

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

                      int amountMask, int amountSocDist,
                      // Время
                      int timeSneeze_a, int timeSneeze_b,

                      //===== Все что связано с руками и лицами
                      int timeHandToFaceContact_a, int timeHandToFaceContact_b,
                      int timeWash_a, int timeWash_b,
                      int timeHandshake_a, int timeHandshake_b,
                      int timeInfHand_a, int timeInfHand_b,
                      float probabilityInfHand,

                      int timeChangeDirect_a, int timeChangeDirect_b,
                      int timeInfInc_a, int timeInfInc_b,
                      int timeRecovery_a, int timeRecovery_b,
                      // Вероятность
                      float probabilityNotSymp, float probabilityDied,
                      float maskProtectionFor, float maskProtectionFrom,

                      float radiusSoc, float radiusMan, float radiusInf,

                      String fileName

    ) throws IOException {

        people = new ArrayList<>();
        file = new FileWriter(fileName,true);
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
        this.timeHandshake_a = timeHandshake_a;
        this.timeHandshake_b = timeHandshake_b;
        this.timeInfHand_a = timeInfHand_a;
        this.timeInfHand_b = timeInfHand_b;

        // Вероятности
        this.probabilityDied = probabilityDied;
        this.probabilityNotSymp = probabilityNotSymp;
        this.probabilityInfHand = probabilityInfHand;
        this.maskProtectionFor = maskProtectionFor;
        this.maskProtectionFrom = maskProtectionFrom;

        //=== заполнение списка людей на карте случайным образом
        float rx,ry;  // Рандомные координаты
        boolean f;  // Флаг поиска свободного места

        // Состояния для заполнения списка людей
        int zd = amountZd, infInc = amountInfInc, infSymp = amountInfSymp,
                infNotSymp = amountInfNotSymp, vzd = amountVzd, died = amountDied;
        // Количество масочников и соц. дистанционщиков для заполнения списка людей
        int masks = amountMask, socdists = amountSocDist;

        // Заполняем список людей
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

            boolean mask, socdist;
            if (masks != 0){
                mask = true;
                masks--;
            }
            else{
                mask = false;
            }
            if (socdists != 0){
                socdist = true;
                socdists--;
            }
            else {
                socdist = false;
            }
            // Создание человечека
            people.add(new Man(cond, mask,socdist,maskProtectionFor,maskProtectionFrom,rx,ry));
        }

    }

    // Запись в файл
    private void writeFile(int mode) throws IOException {
        switch (mode){
            case 1:
            file.write(iter+" "+amountZd+" "+amountInfInc+" "+amountInfSymp+" "+amountInfNotSymp+" "+
                    amountVzd+" "+amountDied+"\n");
            break;

            case 2:
                int amountInf = amountInfInc+amountInfSymp+amountInfNotSymp;
                file.write(iter+" "+amountZd+" "+amountInf+" "+amountVzd+" "+amountDied+"\n");
                break;
        }
    }

    public void closeFile() throws IOException {
        file.flush();
    }

    // Проверка на возможность перемещения бойчика по карте
    public boolean checkBarrier(float x, float y){
        if ((x-this.radiusMan > 0) && (x+this.radiusMan < sizeX)) {
            return (y-this.radiusMan > 0) && (y+this.radiusMan < sizeY);
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
    public void makeContact(Man man, boolean contactOrhand){
        // Поиск людей находящихся рядом с "man"
        for (Man temp:people) {
            // Проверка на возможность контакта
            if ((Math.pow(man.getX() - temp.getX(), 2) + Math.pow(man.getY() - temp.getY(), 2)) < radiusInfSqr){
                if (contactOrhand){ // true - чих, false - пожали руки
                    incStContacts();
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
                            incStContactInf();
                    }

                }
                else{
                    incStHandshake();
                    if (man.getInfectHand())
                        temp.setInfectHand(true);
                }
            }
        }


    }

    // Заводим моторчик нашей мапы
    public void iterate() throws IOException {
        for (Man temp : people) {
            if (temp.getCondition() != 4) {
                temp.doDela(this);
            }
            else{
                temp = null;
            }
        }
        writeFile(2);
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
        System.out.println("--------------------");
        System.out.println("Всего встреч: "+stContacts);
        System.out.println("Заражений от встреч: "+stContactInf);
        System.out.println("--------------------");
        System.out.println("Всего рукопожатий: "+ stHandshakes);
        System.out.println("Заражений от рукопожатий: "+ stHandshakeInf);
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

    public void incStContacts(){
        stContacts++;
    }

    public void incStContactInf(){
        stContactInf++;
    }

    public void incStHandshake(){
        stHandshakes++;
    }

    public void incStHandshakeInf(){
        stHandshakeInf++;
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
