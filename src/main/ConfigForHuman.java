package main;

public class ConfigForHuman {
    // Время
    public final int timeMeet_a, timeMeet_b;            // Между чиханиями
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
    // Эффективность маски
    public final float maskProtectionFor;    // Эффективность защиты маски в сторону заражения кого-то
    public final float maskProtectionFrom;   // Эффективность защиты маски в сторону заразиться от кого-то
    // Радиусы
    public final float radiusInf;       // радиус заражения
    public final float radiusInfSqr;    // радиус заражения (в квадрате)
    public final float radiusMan;       // размер человека // для того, чтобы не накладывались друг на друга
    public final float radiusManSqr;    // размер человека (в квадрате)
    public final float radiusSoc;       // радиус социального дистанцирования
    public final float radiusSocSqr;    // радиус социального дистанцирования (в квадрате)
    public final float radiusHandshake; // радиус рукопожатия
    public final float radiusHandshakeSqr;// радиус рукопожатия (в квадрате)

    public ConfigForHuman(
            // Интервалы времени
            int timeMeet_a, int timeMeet_b,
            int timeHandToFaceContact_a, int timeHandToFaceContact_b,
            int timeWash_a, int timeWash_b,
            int timeChangeDirect_a, int timeChangeDirect_b,
            int timeHandshake_a, int timeHandshake_b,
            int timeInfHand_a, int timeInfHand_b,
            int timeInfInc_a, int timeInfInc_b,
            int timeRecovery_a, int timeRecovery_b,
            // Вероятность
            float probabilityNotSymp, float probabilityDied,
            float probabilityInfHand,
            // Эффективность маски
            float maskProtectionFor, float maskProtectionFrom,
            // Радиусы
            float radiusSoc, float radiusMan, float radiusInf, float radiusHandshake

    ){
        // Настройки времени
        this.timeMeet_a = timeMeet_a;
        this.timeMeet_b = timeMeet_b;
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
        // Радиусы
        this.radiusMan = radiusMan;
        radiusManSqr = (float)Math.pow(this.radiusMan,2);
        this.radiusSoc = radiusSoc+radiusMan;
        radiusSocSqr = (float)Math.pow(this.radiusSoc,2);
        this.radiusInf = radiusInf+radiusMan;
        radiusInfSqr = (float)Math.pow(this.radiusInf,2);
        this.radiusHandshake = radiusHandshake+radiusMan;
        radiusHandshakeSqr = (float)Math.pow(this.radiusHandshake,2);
    }

    // Получить рандомное int число в интервале [a,b]
    private int randomTime(int a, int b){
        return (int)(Math.random()*(b-a+1)+a);
    }

    //=== Получить случайное время для таймера
    public int getTimeMeet(){
        return randomTime(timeMeet_a, timeMeet_b);
    }

    public int getTimeHandToFaceContact(){
        return randomTime(timeHandToFaceContact_a,timeHandToFaceContact_b);
    }

    public int getTimeWash(){
        return randomTime(timeWash_a,timeWash_b);
    }

    public int getTimeChangeDirect(){
        return randomTime(timeChangeDirect_a,timeChangeDirect_b);
    }

    public int getTimeHandshake(){
        return randomTime(timeHandshake_a,timeHandshake_b);
    }

    public int getTimeInfHand(){
        return randomTime(timeInfHand_a,timeInfHand_b);
    }

    public int getTimeInfInc(){
        return randomTime(timeInfInc_a,timeInfInc_b);
    }

    public int getTimeRecovery(){
        return randomTime(timeRecovery_a,timeRecovery_b);
    }
}
