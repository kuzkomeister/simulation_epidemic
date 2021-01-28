package main;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Simulation {

    private LinkedList<Point> sneeze; // список чиханий (для анимации)

    private LinkedList<Human> people; // список жалких подопытных людишек
    private int iter = 0;            // номер текущей итерации
    private QuadTree root;           // Корень квадродерева
    private FileWriter file;         // файл в который будет записываться статистика
    private Hospital hospital;       // Больница

    //========== Настройки карты/симуляции
    public final double sizeX, sizeY; // размеры области по х и у
    public final int amountMans;     // количество людей на карте при старте симуляции
    public final int amountMask;     // Количество людей с масками
    public final int amountSocDist;  // Количество людей соблюдающих соц. дистанцию

    //========== Статистика
    private int stContacts = 0;    // количество контактов за сессию
    private int stContactInf = 0;  // количество заразившихся от чиха с инфицированным
    private int stHandshakes = 0;  // количество рукопожатий
    private int stHandshakeInf = 0;// количество заразившихся от контакта рук с лицом
    private int stChecks = 0;      // Количество проверок коллизии
    private int iterFinal = 0;     // номер итерации, на которой закончились изменения
    // Количество здоровых,инфицированных в инкубац. периоде, инфиц. с симптомами, инфиц. без симптомов
    // выздоровевших, умерших в текущий момент времени
    private int amountZd, amountInfInc, amountInfSymp,
                amountInfNotSymp, amountVzd, amountDied;

    //=====================
    // Конструктор
    public Simulation(
            // Размер области
            double sizeX, double sizeY,
            // Настройки здоровых:
            // кол-во (здоровых, носящих маски, соблюдающих соц дистанцию, носящих и соблюдающих)
            int amountZd, int amountMaskZd, int amountSocDistZd, int amountGoodHumanZd,
            // Настройки инфицированных в инкубационном периоде:
            // кол-во (здоровых, носящих маски, соблюдающих соц дистанцию, носящих и соблюдающих)
            int amountInfInc, int amountMaskInfInc, int amountSocDistInfInc, int amountGoodHumanInfInc,
            // Настройки инфицированных в клиническом периоде:
            // кол-во (здоровых, носящих маски, соблюдающих соц дистанцию, носящих и соблюдающих)
            int amountInfSymp, int amountMaskInfSymp, int amountSocDistInfSymp, int amountGoodHumanInfSymp,
            // Настройки инфицированных бессимптомных:
            // кол-во (здоровых, носящих маски, соблюдающих соц дистанцию, носящих и соблюдающих)
            int amountInfNotSymp, int amountMaskInfNotSymp, int amountSocDistInfNotSymp, int amountGoodHumanInfNotSymp,
            // Настройки выздоровевших:
            // кол-во (здоровых, носящих маски, соблюдающих соц дистанцию, носящих и соблюдающих)
            int amountVzd, int amountMaskVzd, int amountSocDistVzd, int amountGoodHumanVzd,
            // Настройки поведения людей
            ConfigForHuman config,
            // Больничка
            Hospital hospital,
            // Название файла вывода или расположение файла вывода
            String fileName

    ) throws IOException {
sneeze = new LinkedList<>();
        people = new LinkedList<>();
        Human.config = config;
        this.hospital = hospital;
        QuadTree.RADIUS = Math.max(Math.max(config.radiusMan,config.radiusSoc),Math.max(config.radiusHandshake,config.radiusInf));
        root = new QuadTree(new Rectangle(0, 0, (int)sizeX, (int)sizeY));
        file = new FileWriter(fileName,false);
        //===== Настройки карты, симуляции
        this.sizeX = sizeX; this.sizeY = sizeY;
        this.amountMans = amountZd+amountInfInc+amountInfSymp+amountInfNotSymp+amountVzd;
        //===== Количество людей в состояниях...
        this.amountZd = amountZd;
        this.amountInfInc = amountInfInc;
        this.amountInfSymp = amountInfSymp;
        this.amountInfNotSymp = amountInfNotSymp;
        this.amountVzd = amountVzd;
        this.amountDied = 0;
        this.amountMask = amountMaskZd+amountMaskInfInc+amountMaskInfSymp+amountMaskInfNotSymp+amountMaskVzd+
            amountGoodHumanZd+amountGoodHumanInfInc+amountGoodHumanInfSymp+amountGoodHumanInfNotSymp+amountGoodHumanVzd;
        this.amountSocDist = amountSocDistZd+amountSocDistInfInc+amountSocDistInfSymp+amountSocDistInfNotSymp+amountSocDistVzd+
            amountGoodHumanZd+amountGoodHumanInfInc+amountGoodHumanInfSymp+amountGoodHumanInfNotSymp+amountGoodHumanVzd;

        //=== заполнение списка людей на карте
        int masks, socDists, goodHumans;
        //-- Здоровые
        masks = amountMaskZd;
        socDists = amountSocDistZd;
        goodHumans = amountGoodHumanZd;
        for (int zd = 0; zd < amountZd; ++zd){
            // Распределение масок и соблюдение соц дистанции
            boolean mask, socDist;
            if (goodHumans != 0){
                mask = true;
                socDist = true;
                goodHumans--;
            }
            else{
                if (masks != 0){
                    mask = true;
                    socDist = false;
                    masks--;
                }
                else{
                    if (socDists != 0){
                        mask = false;
                        socDist = true;
                        socDists--;
                    }
                    else{
                        mask = false;
                        socDist = false;
                    }
                }
            }

            Human human = new Human((byte)0,mask,socDist,0,0);
            double rx,ry;  // Рандомные координаты
            boolean f;  // Флаг поиска свободного места
            // Распределение по области
            do{
                rx = Math.random() * sizeX;
                ry = Math.random() * sizeY;
                human.setX(rx);
                human.setY(ry);
                float distance = getNearDistance(human);
                if (distance != -1)
                    f = distance > 2 * Human.config.radiusMan;
                else f = true;
            } while (!f);
            // Создаем человечека
            people.add(human);
            root.insert(human);
        }
        //-- Инфицированные в инкубационном периоде
        masks = amountMaskInfInc;
        socDists = amountSocDistInfInc;
        goodHumans = amountGoodHumanInfInc;
        for (int infInc = 0; infInc < amountInfInc; ++infInc){
            // Распределение масок и соблюдение соц дистанции
            boolean mask, socDist;
            if (goodHumans != 0){
                mask = true;
                socDist = true;
                goodHumans--;
            }
            else{
                if (masks != 0){
                    mask = true;
                    socDist = false;
                    masks--;
                }
                else{
                    if (socDists != 0){
                        mask = false;
                        socDist = true;
                        socDists--;
                    }
                    else{
                        mask = false;
                        socDist = false;
                    }
                }
            }

            Human human = new Human((byte)1,mask,socDist,0,0);
            double rx,ry;  // Рандомные координаты
            boolean f;  // Флаг поиска свободного места
            // Распределение по области
            do{
                rx = Math.random() * sizeX;
                ry = Math.random() * sizeY;
                human.setX(rx);
                human.setY(ry);
                float distance = getNearDistance(human);
                if (distance != -1)
                    f = distance > 2 * Human.config.radiusMan;
                else f = true;
            } while (!f);
            // Создаем человечека
            people.add(human);
            root.insert(human);
        }
        //-- Инфицированные в клиническом периоде с симптомами
        masks = amountMaskInfSymp;
        socDists = amountSocDistInfSymp;
        goodHumans = amountGoodHumanInfSymp;
        for (int infSymp = 0; infSymp < amountInfSymp; ++infSymp){
            // Распределение масок и соблюдение соц дистанции
            boolean mask, socDist;
            if (goodHumans != 0){
                mask = true;
                socDist = true;
                goodHumans--;
            }
            else{
                if (masks != 0){
                    mask = true;
                    socDist = false;
                    masks--;
                }
                else{
                    if (socDists != 0){
                        mask = false;
                        socDist = true;
                        socDists--;
                    }
                    else{
                        mask = false;
                        socDist = false;
                    }
                }
            }

            Human human = new Human((byte)2,mask,socDist,0,0);
            double rx,ry;  // Рандомные координаты
            boolean f;  // Флаг поиска свободного места
            // Распределение по области
            do{
                rx = Math.random() * sizeX;
                ry = Math.random() * sizeY;
                human.setX(rx);
                human.setY(ry);
                float distance = getNearDistance(human);
                if (distance != -1)
                    f = distance > 2 * Human.config.radiusMan;
                else f = true;
            } while (!f);
            // Создаем человечека
            people.add(human);
            root.insert(human);
        }
        //-- Инфицированные в клиническом периоде без симптомами
        masks = amountMaskInfNotSymp;
        socDists = amountSocDistInfNotSymp;
        goodHumans = amountGoodHumanInfNotSymp;
        for (int infNotSymp = 0; infNotSymp < amountInfNotSymp; ++infNotSymp){
            // Распределение масок и соблюдение соц дистанции
            boolean mask, socDist;
            if (goodHumans != 0){
                mask = true;
                socDist = true;
                goodHumans--;
            }
            else{
                if (masks != 0){
                    mask = true;
                    socDist = false;
                    masks--;
                }
                else{
                    if (socDists != 0){
                        mask = false;
                        socDist = true;
                        socDists--;
                    }
                    else{
                        mask = false;
                        socDist = false;
                    }
                }
            }

            Human human = new Human((byte)5,mask,socDist,0,0);
            double rx,ry;  // Рандомные координаты
            boolean f;  // Флаг поиска свободного места
            // Распределение по области
            do{
                rx = Math.random() * sizeX;
                ry = Math.random() * sizeY;
                human.setX(rx);
                human.setY(ry);
                float distance = getNearDistance(human);
                if (distance != -1)
                    f = distance > 2 * Human.config.radiusMan;
                else f = true;
            } while (!f);
            // Создаем человечека
            people.add(human);
            root.insert(human);
        }
        //-- Здоровые
        masks = amountMaskVzd;
        socDists = amountSocDistVzd;
        goodHumans = amountGoodHumanVzd;
        for (int vzd = 0; vzd < amountVzd; ++vzd){
            // Распределение масок и соблюдение соц дистанции
            boolean mask, socDist;
            if (goodHumans != 0){
                mask = true;
                socDist = true;
                goodHumans--;
            }
            else{
                if (masks != 0){
                    mask = true;
                    socDist = false;
                    masks--;
                }
                else{
                    if (socDists != 0){
                        mask = false;
                        socDist = true;
                        socDists--;
                    }
                    else{
                        mask = false;
                        socDist = false;
                    }
                }
            }

            Human human = new Human((byte)3,mask,socDist,0,0);
            double rx,ry;  // Рандомные координаты
            boolean f;  // Флаг поиска свободного места
            // Распределение по области
            do{
                rx = Math.random() * sizeX;
                ry = Math.random() * sizeY;
                human.setX(rx);
                human.setY(ry);
                float distance = getNearDistance(human);
                if (distance != -1)
                    f = distance > 2 * Human.config.radiusMan;
                else f = true;
            } while (!f);
            // Создаем человечека
            people.add(human);
            root.insert(human);
        }
        stChecks = 0;
    }

    // Основной метод симуляции
    public void iterate() throws IOException {

        // обход по лЮдям для делания дел
        for (Human human : people) {
            byte oldCond = human.getCondition();
            human.doDela(this);
            // Если изменилось состояние человека
            if (oldCond != human.getCondition()){
                // Статистика состояний
                setAmountCond(oldCond,human.getCondition());
                // Запоминание финальной итерации
                if (amountInfNotSymp == 0 && amountInfSymp == 0 && amountInfInc == 0)
                    iterFinal = iter;
            }
            //root.update();
        }

        root.update();
        root.join();

        people.removeIf(human -> human.getCondition() == 4);


        if (hospital != null) {
            hospital.iterate(this);
        }

        writeFile(2);

        iter++;
    }

    //=== Методы для людишичек
    // Встретиться
    public void makeMeet(Human human){
        LinkedList<Human> tempList = getRegionPeople(human);
        // Поиск людей находящихся рядом с "human"
        for (Human temp:tempList) {
            // Проверка на возможность контакта
            if ((Math.pow(human.getX() - temp.getX(), 2) + Math.pow(human.getY() - temp.getY(), 2)) < Human.config.radiusInfSqr){
                // Увеличиваем статистику по количеству произошедших встреч
                incStContacts();
                // Если текущий выбранный человек заражен,а ближайший нет
                if ((human.getCondition() == 2 || human.getCondition() == 5) && temp.getCondition() == 0) {
                    // если вероятность на его стороне
                    if (Math.random() < (human.probabilityInfection * temp.probabilityGetInfection)) {
                        // то заражает
                        temp.setCondition((byte) 1);
                        // Статистика состояний
                        setAmountCond(0, 1);
                        // Увеличиваем статистику по количеству встреч с заражением
                        incStContactInf();
                    }
                }
            }
        }
        if (human.getCondition() == 2 || human.getCondition() == 5)
            sneeze.add(new Point((int)human.getX(),(int)human.getY()));
    }

    // Пожать руки
    public void makeHandshake(Human human){
        LinkedList<Human> tempList = getRegionPeople(human);
        // Поиск людей находящихся рядом с "human"
        for (Human temp:tempList) {
            // Проверка на возможность контакта
            if ((Math.pow(human.getX() - temp.getX(), 2) + Math.pow(human.getY() - temp.getY(), 2)) < Human.config.radiusHandshakeSqr){
                incStHandshake();
                if (human.getInfectHand())
                    temp.setInfectHand(true);
            }
        }
    }

    // Возвращает расстояние до ближайшего человека (-1 если никого нет)
    public float getNearDistance(Human human){
        LinkedList<Human> tempList = getRegionPeople(human);
        float resDistance = -1;
        if (tempList.size() != 0){
            // Вычисление первого результата для дальнейшего сравнения
            resDistance = (float)(Math.pow((human.getX()-tempList.getFirst().getX()),2) + Math.pow((human.getY()-tempList.getFirst().getY()),2));
            tempList.remove(0);
            ++stChecks;
            // Обход по списку людей для вычисления минимальной дистанции
            for (Human tempHuman:tempList){
                float distance = (float)(Math.pow((human.getX()-tempHuman.getX()),2) + Math.pow((human.getY()-tempHuman.getY()),2));
                if (distance < resDistance){
                    resDistance = distance;
                }
                ++stChecks;
            }
        }
        return resDistance;
    }

    // Проверяет выход за рамки области
    public boolean checkBarrier(double x, double y){
        if ((x - Human.config.radiusMan > 0) && (x + Human.config.radiusMan < sizeX)) {
            return (y - Human.config.radiusMan > 0) && (y + Human.config.radiusMan < sizeY);
        }
        else return false;
    }
    //======
    // Получить список людей в области
    private LinkedList<Human> getRegionPeople(Human human){
        LinkedList<Human> tempList = new LinkedList<>();
        tempList = root.retrieve(tempList,human);
        tempList.remove(human);
        return tempList;
    }

    // Запись в файл
    private void writeFile(int mode) throws IOException {
        switch (mode) {
            case 1 -> file.write(iter + " " + amountZd + " " + amountInfInc + " " + amountInfSymp + " " + amountInfNotSymp + " " +
                    amountVzd + " " + amountDied + "\n");
            case 2 -> {
                int amountInf = amountInfInc + amountInfSymp + amountInfNotSymp;
                file.write(iter + " " + amountZd + " " + amountInf + " " + amountVzd + " " + amountDied + "\n");
            }
        }
    }

    public void closeFile() throws IOException {
        file.close();
    }

    //=================================================
    // для дебага
    public void printInfoStat(){
        System.out.println("=======================================================");
        if (iterFinal != 0) System.out.println("Изменения закончились на "+iterFinal+" итерации из "+iter);
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
        if (hospital != null){
            System.out.println("===== Статистика больницы =====");
            System.out.println("Всего прошло пациентов: "+hospital.getStAmountPatients());
            System.out.println("Всего умерло в больнице: "+hospital.getStAmountDied());
            System.out.println("Всего сделано тестов: "+hospital.getStAmountTest());
            System.out.println("Количество ложно-отрицательных тестов: "+hospital.getStAmountFalseTest());
        }
        System.out.println("========= Дебаг =======");
        System.out.println("Количество проверок коллизии: "+stChecks);
    }

    //======== Статистика
    // изменение статистики количества людей в различных состояниях
    public void setAmountCond(int oldCond, int newCond){
        switch (oldCond) {
            case 0 -> amountZd--;
            case 1 -> amountInfInc--;
            case 2 -> amountInfSymp--;
            case 3 -> amountVzd--;
            case 5 -> amountInfNotSymp--;
        }
        switch (newCond) {
            case 0 -> amountZd++;
            case 1 -> amountInfInc++;
            case 2 -> amountInfSymp++;
            case 3 -> amountVzd++;
            case 4 -> amountDied++;
            case 5 -> amountInfNotSymp++;
        }
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

    public int getStChecks(){
        return stChecks;
    }

    public int getIterFinal(){
        return iterFinal;
    }

    //======== Размеры карты

    public double getSizeX(){
        return this.sizeX;
    }

    public double getSizeY(){
        return this.sizeY;
    }

    //======== Ссылка на список людей

    public LinkedList<Human> getPeople(){
        return people;
    }

    public LinkedList<Point> getSneeze(){
        return sneeze;
    }

    public QuadTree getRoot(){
        return root;
    }

}
