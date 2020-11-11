package main;

import java.util.ArrayList;
import java.util.Queue;

public class OctTree {



    //=== Узлы
    // Дочерние узлы
    private OctTree[] childNode = new OctTree[4];
    // Родительский узел
    private OctTree parent;

    //=== Внутренности квадранта
    // Список людей
    private ArrayList<Man> people = new ArrayList<>();

    //=== Координаты ЛВ угла и размер квадрата
    private final int x, y;
    private final int size;




    //=====
    // Минимальный размер нода
    private static final int MIN_SIZE = 4;
    // Радиус пересечение для разделения на ноды
    private static final float RADIUS_CROSS = 2.0f;

////////////////////////////////////////////////////
    public OctTree(int x, int y, int size){
        this.x = x;
        this.y = y;
        this.size = size;
    }

    private OctTree createNode(int x, int y, int size){
        OctTree ret = new OctTree(x,y,size);
        ret.parent = this;
        return ret;
    }

    private void buildTree(){
        // Если дошли до минимального размера
        if (size <= MIN_SIZE){
            return;
        }


    }




}
