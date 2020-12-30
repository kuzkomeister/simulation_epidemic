package main;

import java.awt.geom.Rectangle2D;
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
    private final Rectangle2D region;

    //===== Константы
    // Минимально допущенный размер
    static final int MIN_SIZE = 2;
    // Радиус для проверки пересечения с центральными осями
    static float RADIUS;
    // Максимальное количество объектов в области до деления
    static final int MAX_OBJECTS = 3;

/////////////////////
    // Конструктор
    public QuadTree(Rectangle2D region){
        this.region = region;
        people = new LinkedList<>();
        childs = new QuadTree[4];
    }

    // Очистка дерева
    public void clear(){
        people.clear();
        parent = null;
        for (int i = 0; i < childs.length; ++i){
            if (childs[i] != null){
                childs[i].clear();
                childs[i]=null;
            }
        }
    }

    // Разделение узла на 4 подузла
    private void split(){
        double subWidth =  region.getWidth() / 2;
        double subHeight = region.getHeight() / 2;
        double x = region.getX();
        double y = region.getY();

        childs[0] = new QuadTree(new Rectangle2D.Double(x+subWidth,y,subWidth,subHeight));
        childs[0].parent = this;
        childs[1] = new QuadTree(new Rectangle2D.Double(x,y,subWidth,subHeight));
        childs[1].parent = this;
        childs[2] = new QuadTree(new Rectangle2D.Double(x,y+subHeight,subWidth,subHeight));
        childs[2].parent = this;
        childs[3] = new QuadTree(new Rectangle2D.Double(x+subWidth,y+subHeight,subWidth,subHeight));
        childs[3].parent = this;
    }

    // Определение объекта места в квадродереве
    private int getIndex(Human human){
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
        if (people.size() > MAX_OBJECTS && (region.getWidth() > MIN_SIZE && region.getHeight() > MIN_SIZE)) {
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

    //=========================

    // Найти узел в котором находится human
    public QuadTree findNode(Human human){
        int index = getIndex(human);
        if (index != -1 && childs[0] != null) {
            if (!people.contains(human)) {
                return childs[index].findNode(human);
            }
            else return this;
        }
        else{
            if (people.contains(human)){
                return this;
            }
            else{
                return null;
            }
        }
    }

    public void updateHuman(Human human) {
        people.remove(human);
        if (human.getCondition() == 4) {
            // Переопределяем объект в дереве
            if (parent != null) {
                if (parent.parent != null) {
                    if (parent.parent.parent != null) {
                        parent.parent.parent.insert(human);
                    } else {
                        parent.parent.insert(human);
                    }
                } else {
                    parent.insert(human);
                }
            } else {
                insert(human);
            }
            // Уничтожаем пустой нод
            boolean clear7 = true;
            for (QuadTree node : parent.childs) {
                if (!(node.people.size() == 0 && node.childs[0] == null)) {
                    clear7 = false;
                    break;
                }
            }
            if (clear7) {
                for (QuadTree node : parent.childs) {
                    node.clear();
                }
            }
        }
    }

    //=========================

    public Rectangle2D getRegion(){
        return region;
    }

    public QuadTree getChild(int i){
        return childs[i];
    }

}
