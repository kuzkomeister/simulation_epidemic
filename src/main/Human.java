package main;

public class Human {
    //=====
    static ConfigForHuman config;   // Настройки для людей
    // 0 - здоровый, 1 - инфицированный в инкубационном периоде,
    // 2 - инфицированный в клиническом периоде, 3 - выздоровевший, 4 - мертвый
    // 5 - инфицированный без проявления симптомов
    private byte condition;         // Текущее состояние человека
    private float x,y;              // Координаты человека на карте
    private float[] vectorDirection;// Вектор направления человека

    public final boolean mask;       // Надета маска
    public final boolean socDist;    // Соблюдает социальное дистанцирование
    private boolean infectHand;      // Инфицированы ли руки

    //===== Текущее время
    private int timeRecovery;       // время необходимое для выздоровления
    private int timeInfInc;         // время инкубационного периода
    private int timeMeet;         // время между встречами
    private int timeHandToFaceContact;// время между контактом рук с лицом
    private int timeWash;           // время между мытьем/дезинфекцией рук
    private int timeChangeDirect;   // время между сменой направлений
    private int timeHandshake;      // время между пожатием рук
    private int timeInfHand;        // время между "загрязнением" своих рук

    //===== Вероятности
    public final float probabilityInfection;     // Текущая вероятность удачной попытки заразить
    public final float probabilityGetInfection;  // Текущая вероятность удачной попытки заразиться

    //=================
    // Конструктор
    public Human(byte condition, boolean mask, boolean socDist,
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
            probabilityInfection = 1.0f-config.maskProtectionFor;
            probabilityGetInfection = 1.0f-config.maskProtectionFrom;
        }
        else{
            probabilityInfection = 1.0f;
            probabilityGetInfection = 1.0f;
        }
        //===== Время
        timeRecovery = config.getTimeRecovery();
        timeInfInc = config.getTimeInfInc();
        timeMeet = config.getTimeMeet();
        timeHandToFaceContact = config.getTimeHandToFaceContact();
        timeWash = config.getTimeWash();
        timeChangeDirect = config.getTimeChangeDirect();
        timeHandshake = config.getTimeHandshake();
        timeInfHand = config.getTimeInfHand();
    }

    // Заставляем человека делать свои делишки
    // Основной метод
    public void doDela(Simulation simulation){
        // Если живой
        if (condition != 4){
            // Если инкубационный период
            if (condition == 1){
                timerIncubPeriod();
            }
            // Если болеет
            if (condition == 2 || condition == 5){
                getHandsDirty();
                timerHealthy();
            }
            // Помыть руки
            washHands();
            // Поздороваться/пожать руки
            handshake(simulation);
            // Побеседовать/встретиться с ближайшими в округе
            meet(simulation);
            // Полапать свое лицо
            if (condition == 0)
                touchTheFace(simulation);
            // повернуться
            rotate();
            // переместиться
            move(simulation);

        }
    }

    // Переместится
    private void move(Simulation simulation){
        float oldDist = simulation.getNearDistance(this);
        boolean res = false;
        for (int i = 0; i < 100 && !res; ++i) {
            res = true;
            if (simulation.checkBarrier(x + vectorDirection[0], y + vectorDirection[1])) {
                float oldx = x, oldy = y;
                x += vectorDirection[0];
                y += vectorDirection[1];

                float distance = simulation.getNearDistance(this);
                if (distance != -1) {
                    if (socDist) {
                        if (distance < oldDist) {
                            x = oldx;
                            y = oldy;
                            res = false;
                        }
                    } else {
                        if (distance < 4 * config.radiusManSqr) {
                            x = oldx;
                            y = oldy;
                            res = false;
                        }
                    }
                }
            } else {
                res = false;
            }

            if (!res){
                setVectorDirection((float)Math.random()-0.5f,(float)Math.random()-0.5f);
                timeChangeDirect = config.getTimeChangeDirect();
            }
        }
    }

    // Пообщаться с ближайшими людьми
    private void meet(Simulation simulation){
        if (timeMeet == 0){
            simulation.makeMeet(this);
            timeMeet = config.getTimeMeet();
        }
        else{
            timeMeet--;
        }
    }

    // Поздороваться/пожать руки с ближайшими людьми
    private void handshake(Simulation simulation){
        if (timeHandshake == 0){
            simulation.makeHandshake(this);
            timeHandshake = config.getTimeHandshake();
        }
        else{
            timeHandshake--;
        }
    }

    // Потрогать лицо руками
    private void touchTheFace(Simulation simulation){
        if (timeHandToFaceContact == 0){
            // Грязные ли руки
            if (infectHand){
                if (Math.random() <= config.probabilityInfHand){
                    condition = 1;
                    simulation.incStHandshakeInf();
                }
            }
        }
        else{
            timeHandToFaceContact--;
        }
    }

    // Повернуться/сменить направление движения
    private void rotate(){
        if (timeChangeDirect == 0){
            setVectorDirection((float)Math.random()-0.5f,(float)Math.random()-0.5f);
            timeChangeDirect = config.getTimeChangeDirect();
        }
        else{
            timeChangeDirect--;
        }
    }

    // Помыть/продезинфецировать руки
    private void washHands(){
        if (timeWash == 0){
            infectHand = false;
            timeWash = config.getTimeWash();
        }
        else{
            timeWash--;
        }
    }

    // Испачкать свои руки
    private void getHandsDirty(){
        if (timeInfHand == 0){
            infectHand = true;
            timeInfHand = config.getTimeInfHand();
        }
        else{
            timeInfHand--;
        }
    }

    // Таймер инкубационного периода
    private void timerIncubPeriod(){
        if (timeInfInc == 0){
            // Шанс на то, чтобы стать бессимптомным больным
            if (Math.random() <= config.probabilityNotSymp){
                // стал бессимптомным
                condition = 5;
            }
            else{
                // стал симптомным
                condition = 2;
            }
        }
        else{
            timeInfInc--;
        }
    }

    // Таймер выздоровления
    private void timerHealthy(){
        if (timeRecovery == 0){
            // шанс умереть
            if (condition != 5 && Math.random() <= config.probabilityDied){
                // умир
                condition = 4;
            }
            else{
                condition = 3;
            }

        }
        else{
            timeRecovery--;
        }
    }

    // Поменять направление движения
    private void setVectorDirection(float x, float y){
        vectorDirection[0] = x;
        vectorDirection[1] = y;
    }

    //============= Состояние
    public void setCondition(byte condition){
        this.condition = condition;
    }

    public byte getCondition(){
        return this.condition;
    }

    //============= Ручки
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

    public void setX(float x) {
        this.x = x;
    }

    public float getY(){
        return this.y;
    }

    public void setY(float y){
        this.y = y;
    }
}
