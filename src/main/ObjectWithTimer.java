package main;

public class ObjectWithTimer {
    private int timer;      // Таймер
    public final Object object;

    public ObjectWithTimer(Object object, int timer){
        this.object = object;
        this.timer = timer;
    }

    public Object timeHasCome(){
        if (timer == 0){
            return object;
        }
        else{
            timer--;
            return null;
        }
    }

    public int getTimer(){
        return timer;
    }


}
