package main;

public class Man {
    //=====
    // 0 - здоровый, 1 - инфицированный в инкубационном периоде,
    // 2 - инфицированный в клиническом периоде, 3 - выздоровевший, 4 - мертвый
    // 5 - инфицированный без проявления симптомов
    private byte condition;         // Текущее состояние человека
    private float x,y;              // Координаты человека на карте
    private float[] vectorDirection;// Вектор направления человека

    private final boolean mask;       // Надета маска
    private final boolean socDist;    // Соблюдает социальное дистанцирование
    private boolean infectHand; // Инфицированы ли руки
    //===== Текущее время
    private int timeRecovery;       // время необходимое для выздоровления
    private int timeInfInc;         // время инкубационного периода
    private int timeSneeze;         // время между чихами
    private int timeHandToFaceContact;// время между контактом рук с лицом
    private int timeWash;           // время между мытьем/дезинфекцией рук
    private int timeChangeDirect = 1;   // время между сменой направлений
    private int timeHandshake = 1;      // время между пожатием рук
    private int timeInfHand = 0;            // время между "загрязнением" своих рук

    //===== Вероятности
    private final float probabilityInfection;     // Текущая вероятность удачной попытки заразить
    private final float probabilityGetInfection;  // Текущая вероятность удачной попытки заразиться
    //=================

    // Конструктор
    public Man(byte condition, boolean mask, boolean socDist,
               float maskProtectionFor, float maskProtectionFrom,
               float x, float y){
        //
        this.condition = condition;
        this.x = x; this.y = y;
        this.mask = mask; this.socDist = socDist;
        infectHand = false;

        vectorDirection = new float[2];
        vectorDirection[0] = (float)Math.random()*2-1;
        vectorDirection[1] = (float)Math.random()*2-1;
        //===== Вероятности
        if (mask){
            probabilityInfection = 1.0f-maskProtectionFor;
            probabilityGetInfection = 1.0f-maskProtectionFrom;
        }
        else{
            probabilityInfection = 1.0f;
            probabilityGetInfection = 1.0f;
        }
    }

    // Поменять направление движения
    private void setVectorDirection(float x, float y){
        vectorDirection[0] = x;
        vectorDirection[1] = y;
    }

    // Переместится на карте "sim" по направлению
    public boolean move(Simulation sim){
        boolean res = true;
        if (sim.checkBarrier(x+ vectorDirection[0],y+ vectorDirection[1])
                && sim.checkPlace(x+ vectorDirection[0], y+ vectorDirection[1])){
            x += vectorDirection[0];
            y += vectorDirection[1];
        }
        else res = false;
        return res;
    }

    // Произвести контакт
    public void contact(Simulation simulation, boolean contactOrhand){
        if (contactOrhand){
            simulation.makeContact(this, contactOrhand);
        }
        else{
            if (infectHand)
                simulation.makeContact(this, contactOrhand);
        }
    }

    // Проверка таймеров
    private void checkTimers(Simulation sim){
        //===== Таймеры для смены состояний
        switch (condition){
            case 0: // Здоров
                // Таймер для "трогания" лица
                if (timeHandToFaceContact == 0){
                    // Грязные ли руки
                    if (infectHand){
                        if (Math.random() <= sim.probabilityInfHand){
                            condition = 1;
                            sim.incStHandshakeInf();
                            timeInfInc = (int)(Math.random()*(sim.timeInfInc_b-sim.timeInfInc_a+1)+sim.timeInfInc_a);
                            sim.setAmountCond(-1,1,0,0,0,0);
                        }
                    }
                }
                else{
                    timeHandToFaceContact--;
                }
                break;

            case 1: // Инфицирован. В инкубационном периоде
                // Таймер для выхода из инкубационного периода
                if (timeInfInc == 0){
                    // Шанс на то, чтобы стать бессимптомным больным
                    if (Math.random() <= sim.probabilityNotSymp){
                        // стал бессимптомным
                        condition = 5;
                        sim.setAmountCond(0,-1,0,1,0,0);
                    }
                    else{
                        // стал симптомным
                        condition = 2;
                        sim.setAmountCond(0,-1,1,0,0,0);
                    }
                    // время для выздоровления
                    timeRecovery = (int)(Math.random()*(sim.timeRecovery_b-sim.timeRecovery_a+1)+sim.timeRecovery_a);
                }
                else{
                    timeInfInc--;
                }
                break;

            case 2, 5: // Инфицирован. В клиническом периоде или без проявления симптомов
                // Таймер чихания
                if (timeSneeze == 0){
                    contact(sim,true);
                    timeSneeze = (int)(Math.random()*(sim.timeSneeze_b-sim.timeSneeze_a+1)+sim.timeSneeze_a);
                }
                else{
                    timeSneeze--;
                }

                // Таймер для "загрязнения" своих рук
                if (timeInfHand == 0){
                    infectHand = true;
                    timeInfHand = (int)(Math.random()*(sim.timeInfHand_b-sim.timeInfHand_a+1)+sim.timeInfHand_a);
                }
                else{
                    timeInfHand--;
                }

                // Таймер выздоровления
                if (timeRecovery == 0){

                    // шанс умереть
                    if (condition != 5 && Math.random() <= sim.probabilityDied){
                        // умир
                        condition = 4;
                        sim.setAmountCond(0,0,-1,0,0,1);
                    }
                    else{
                        // выздоровел
                        // проверка для правильности ведения статистики
                        if (condition == 2){
                            sim.setAmountCond(0,0,-1,0,1,0);
                        }
                        else {
                            sim.setAmountCond(0,0,0,-1,1,0);
                        }
                        condition = 3;
                    }

                }
                else{
                    timeRecovery--;
                }
                break;
        }

        // Таймер для мытья/дезинфекции рук
        if (timeWash == 0){
            infectHand = false;
            timeWash = (int)(Math.random()*(sim.timeWash_b-sim.timeWash_a+1)+sim.timeWash_a);
        }
        else{
            timeWash--;
        }

        //==== Пожать руки с кем-то
        if (timeHandshake == 0){
            contact(sim,false);
            timeHandshake = (int)(Math.random()*(sim.timeHandshake_b -sim.timeHandshake_a +1)+sim.timeHandshake_a);
        }
        else{
            timeHandshake--;
        }

        //===== Время для смены направления движения
        if (timeChangeDirect == 0){
            timeChangeDirect = (int)(Math.random()*(sim.timeChangeDirect_b-sim.timeChangeDirect_a+1)+sim.timeChangeDirect_a);
        }
        else{
            setVectorDirection((float)Math.random()*2-1,(float)Math.random()*2-1);
            timeChangeDirect--;
        }

    }

    // Заставляем человека делать свои делишки
    // Основной метод
    public void doDela(Simulation sim){
        if (condition != 4){
            // Проверяем таймеры
            checkTimers(sim);
            // Переместится по карте
            move(sim);
        }
    }

    //============= Время
    public void setTimeInfInc(int timeInfInc){
        this.timeInfInc=timeInfInc;
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

    public void setInfectHand(boolean conditionHand){
        infectHand = conditionHand;
    }

    public boolean getInfectHand(){
        return infectHand;
    }
    //============ Координаты
    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

}
