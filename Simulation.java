package gui_for_epidemic;

import java.util.ArrayList;

public class Simulation {

    private ArrayList<Man> people;    // список жалких подопытных людишек
    //========== Настройки карты/симуляции
    private float sizeX, sizeY; // размеры карты по х и у
    private int amountMans;     // количество людей на карте при старте симуляции
    private float radiusMan;      // размер человека (радиус кружка на мапе) (в квадрате)
    //========== Настройки болезни
    private float radiusInf;     // радиус заражения (в квадрате)
    //========== Статистика
    private long stContacts = 0;    // количество контактов за сессию
    //=====================


    // Конструктор
    public Simulation(float sizeX, float sizeY, int amountMans, int amountInf){
        people = new ArrayList<>();
        //===== Настройки карты, симуляции
        this.sizeX = sizeX; this.sizeY = sizeY;
        this.amountMans = amountMans+amountInf;
        radiusMan = 0.2f;
        //===== Настройки болезни
        radiusInf = (float) Math.pow(2.0f,2);
        //==================


        
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
        if ((x-this.radiusMan > 0) && (x+this.radiusMan < sizeX)) {
            return (y-this.radiusMan > 0) && (y+this.radiusMan < sizeY);
        }
        else return false;
    }

    // Проверка на свободность места на карте
    public boolean checkPlace(float x, float y){
        boolean res = true;
        for (Man temp:this.people){
            if ((Math.pow(x-temp.getX(),2)+Math.pow(y-temp.getY(),2)) < 4* radiusMan){
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
            for (Man temp:this.people) {
                // Проверка на возможность контакта
                if ((Math.pow(man.getX() - temp.getX(), 2) + Math.pow(man.getY() - temp.getY(), 2)) < this.radiusInf){
                    this.stContacts++;  // статистика
                    // Если человек здоровый
                    if (temp.getCondition() == 0)
                        // если вероятность на его стороне
                        if (Math.random() < (man.getProbabilityInfection() * temp.getProbabilityGetInfection())) {
                            // то заражает
                            temp.setCondition((byte) 1);
                        }
                }
            }

        }
    }

    // Заводим моторчик нашей мапы
    public void start(int amountIter) {
        for (int iter = 0; iter < amountIter; iter++) {
            for (Man temp : this.people) {
                temp.doDela(this);
            }
        }

    }

    //=================================================
    // для дебага
    public void printInfoMap(){
        int zd = 0, inf = 0, vzd = 0;
        for (Man temp:this.people){
            if (temp.getCondition() == 0) zd++;
            else {  if (temp.getCondition() == 1) inf++;
            else vzd++; }
        }
        System.out.print(zd);
        System.out.print("  ");
        System.out.print(inf);
        System.out.print("  ");
        System.out.println(vzd);
    }

    public void printInfoMans(){
        Man temp;
        int zd = 0, inf = 0, vzd = 0;
        for (int i = 0; i < this.amountMans; ++i) {
            temp = this.people.get(i);
            System.out.print(temp.getCondition());
            System.out.print("  ");
            System.out.print(temp.getX());
            System.out.print("  ");
            System.out.println(temp.getY());
        }
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
    
    public ArrayList<Man> getPeople()
    { return people;  
    }
    
    public int GetRadius()
    { return 2;
        
    }
}
