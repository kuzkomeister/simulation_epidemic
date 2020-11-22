package main;

import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.LinkedList;

public class QuadTree {
    //===== Узлы
    // Родительский узел
    private QuadTree parent;
    // Дочерние узлы
    private QuadTree[] childs;
    /* Индексы дочерних квадрантов
       +---+---+
       | 2 | 1 |
       +---+---+
       | 3 | 4 |
       +---+---+  */

    //===== Внутренности узла
    private LinkedList<Human> people;

    //===== Область
    private final Rectangle region;

    //===== Константы
    // Минимально допущенный размер
    static final int MIN_SIZE = 2;
    // Радиус для проверки пересечения с центральными осями
    static float RADIUS;
    //
    static final int MAX_OBJECTS = 1;

/////////////////////
    // Конструктор
    public QuadTree(Rectangle region){
        this.region = region;
        people = new LinkedList<>();
        childs = new QuadTree[4];
    }

    // Создание дочернего узла
    private QuadTree createNode(Rectangle region, LinkedList<Human> peopleList){
        QuadTree node = new QuadTree(region);
        node.people = peopleList;
        node.parent = this;
        return node;
    }

    // Очистка дерева
    public void clear(){
        people.clear();
        for (int i = 0; i < childs.length; ++i){
            if (childs[i] != null){
                childs[i].clear();
                childs[i]=null;
            }
        }
    }

    // Разделение узла на 4 подузла
    private void split(){
        int subWidth = (int)(region.getWidth() / 2);
        int subHeight = (int)(region.getHeight() / 2);
        int x = (int)region.getX();
        int y = (int)region.getY();

        childs[0] = new QuadTree(new Rectangle(x+subWidth,y,subWidth,subHeight));
        childs[0].parent = this;
        childs[1] = new QuadTree(new Rectangle(x,y,subWidth,subHeight));
        childs[1].parent = this;
        childs[2] = new QuadTree(new Rectangle(x,y+subHeight,subWidth,subHeight));
        childs[2].parent = this;
        childs[3] = new QuadTree(new Rectangle(x+subWidth,y+subHeight,subWidth,subHeight));
        childs[3].parent = this;
    }

    // Определение объекта места в квадродереве
    public int getIndex(Human human){
        int index = -1;
        double verticalMidpoint = region.getX() + (region.getWidth() / 2);
        double horizontalMidpoint = region.getY() + (region.getHeight() / 2);

        if (((human.getX() - RADIUS < verticalMidpoint && verticalMidpoint < human.getX() + RADIUS) ||
            (human.getY() - RADIUS < horizontalMidpoint && horizontalMidpoint < human.getY() + RADIUS))){
            return index;
        }
        else {

            boolean X = human.getX() > verticalMidpoint;
            boolean Y = human.getY() > horizontalMidpoint;

            if (X){
                if (Y){
                    index = 3;
                }
                else{
                    index = 0;
                }
            }
            else{
                if (Y){
                    index = 2;
                }
                else{
                    index = 1;
                }
            }
        }

        return index;
    }

    // Вставка объекта в дерево
    public void insert(Human human) {
        // Если данная область не поделена
        if (childs[0] != null) {
            // Вычисление куда входит объект
            int index = getIndex(human);
            // Если объект входит в дочерний узел, то добавляем в него
            if (index != -1) {
                childs[index].insert(human);
                return;
            }
        }
        // Добавление в список объект
        people.add(human);
        // Если много объектов и размеры позваляют, то распределяем объекты по дочерним узлам
        if (people.size() > MAX_OBJECTS && region.getWidth() > MIN_SIZE) {
            // Делим область на 4 части, если еще не поделено
            if (childs[0] == null) {
                split();
            }
            // Распределяем объекты по дочерним узлам
            int i = 0;
            while (i < people.size()) {
                int index = getIndex(people.get(i));
                if (index != -1) {
                    childs[index].insert(people.remove(i));
                }
                else {
                    i++;
                }
            }
        }
    }

    // Вывод списка объектов находящихся рядом с
    public LinkedList<Human> retrieve(LinkedList<Human> returnObjects, Human human) {
        int index = getIndex(human);
        if (index != -1 && childs[0] != null) {
            childs[index].retrieve(returnObjects, human);
        }

        returnObjects.addAll(people);

        return returnObjects;
    }

    public Rectangle getRegion(){
        return region;
    }

    public QuadTree getChild(int i){
        return childs[i];
    }

    public void printUz(int lvl){

        if (people.size() != 0) {
            System.out.println("== " + lvl + " ==");
            System.out.println(region);

            for (Human human : people) {
                System.out.println(human.getX() + " " + human.getY());
            }

            if (childs[0] != null) {
                System.out.println("Есть дочки {");
                childs[0].printUz(lvl + 1);
                childs[1].printUz(lvl + 1);
                childs[2].printUz(lvl + 1);
                childs[3].printUz(lvl + 1);
                for (int j = 0; j < lvl; ++j)
                    System.out.print(" ");
                System.out.println("}");
            } else {
                System.out.println("NULL!");
            }
        }
        else{
            if (childs[0] != null){
                childs[0].printUz(lvl + 1);
                childs[1].printUz(lvl + 1);
                childs[2].printUz(lvl + 1);
                childs[3].printUz(lvl + 1);
            }
        }


    }

}
