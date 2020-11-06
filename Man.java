package gui_for_epidemic;

public class Man {
    //=====
    // 0 - здоровый, 1 - инфицированный, 2 - выздоревевший
    private byte condition;         // Текущее состояние человека
    private float x,y;              // Координаты человека на карте
    private float[] vectorDirection;// Вектор направления человека
    //===== Время
    private int timeRecovery;       // время необходимое для выздоровления
    private int timeSneeze;         // время между чихами
    private int timeChangeDirect;   // время между сменой направлений
    //===== Вероятности
    private float probabilityInfection;     // Текущая вероятность удачной попытки заразить
    private float probabilityGetInfection;  // Текущая вероятность удачной попытки заразиться
    //=================

    // Конструктор
    public Man(byte condition, float x, float y){
        //
        this.condition = condition;
        this.x = x; this.y = y;
        vectorDirection = new float[2];
        vectorDirection[0] = (float)Math.random()*2-1;
        vectorDirection[1] = (float)Math.random()*2-1;
        //===== Время
        timeRecovery = 1000;
        timeSneeze = 100;
        timeChangeDirect = 50;
        //===== Вероятности
        probabilityInfection = 1.0f;
        probabilityGetInfection = 1.0f;
    }

    // Поменять направление
    public void setVectorDirection(float x, float y){
        vectorDirection[0] = x;
        vectorDirection[1] = y;
    }

    // Переместится на карте "map" по направлению
    public boolean move(Simulation sim){
        boolean res = true;
        if (sim.checkBarrier(x+ vectorDirection[0],y+ vectorDirection[1])
                && sim.checkPlace(x+ vectorDirection[0], y+ vectorDirection[1])) {
            x += vectorDirection[0];
            y += vectorDirection[1];
        }
        else res = false;
        return res;
    }

    // Произвести контакт
    public void contact(Simulation simulation){
        simulation.makeContact(this);
    }

    // Заставляем человека делать свои делишки
    // Основной метод
    public void doDela(Simulation map){
        //==== Время для выздоровления и чихания
        if (condition == 1){
            // Время для чихания
            if (timeSneeze == 0){
                contact(map);
                timeSneeze = 100;
            }
            else {
                timeSneeze--;
            }
            // Время для выздоровления
            if (timeRecovery == 0) {
                condition = 2;
            }
            else {
                timeRecovery--;
            }
        }
        //===== Время для смены направления
        if (timeChangeDirect != 0){
            setVectorDirection((float)Math.random()*2-1,(float)Math.random()*2-1);
            timeChangeDirect--;
        }
        else{
            timeChangeDirect = 50;
        }
        //===== Переместится по карте
        move(map);
    }

    //============= Вероятности
    public float getProbabilityInfection(){
        return this.probabilityInfection;
    }

    public float getProbabilityGetInfection(){
        return this.probabilityGetInfection;
    }
    //============= Состояние
    public void setCondition(byte condition){
        this.condition = condition;
    }

    public byte getCondition(){
        return this.condition;
    }
    //============ Координаты
    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

}