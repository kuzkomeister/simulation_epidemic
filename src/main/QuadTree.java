package main;

import javax.swing.plaf.synth.Region;
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
    // Список людей
    private LinkedList<Human> people;
    // Количество людей в этой области
    private int count;

    //===== Область
    private final Rectangle2D region;

    //===== Статус
    private boolean updateStatus;

    //===== Константы
    // Минимально допущенный размер
    static final int MIN_SIZE = 2;
    // Радиус для проверки пересечения с центральными осями
    static float RADIUS;
    // Максимальное количество объектов в области до деления
    static final int MAX_OBJECTS = 5;

/////////////////////
    // Конструктор
    public QuadTree(Rectangle2D region){
        this.region = region;
        people = new LinkedList<>();
        childs = new QuadTree[4];
        count = 0;
        updateStatus = false;
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
        count++;
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
        people.addLast(human);
        // Если много объектов и размеры позволяют, то распределяем объекты по дочерним узлам
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

    // Объединение узлов
    public void join(){
        // Если есть потомки
        if (childs[0] != null){
            // Объединяем в дочерних узлах
            for (int i = 0; i < childs.length; ++i){
                childs[i].join();
            }
            // Если можно всех людей в области внести в один узел
            if (count <= MAX_OBJECTS){
                // Вносим людей из дочерних в текущий узел
                for (int i = 0; i < childs.length; ++i){
                    people.addAll(childs[i].people);
                }
                // Удаляем дочерние узлы
                for (int i = 0; i < childs.length; ++i){
                    childs[i].clear();
                    childs[i] = null;
                }
            }
        }
    }

    // Обновление содержимого узла
    public void update(){
        // Если есть потомки
        if (childs[0] != null){
            // Обновляем потомков
            for (int i = 0; i < childs.length; ++i){
                childs[i].update();
            }
        }
        // Перемещаем людей текущего узла
        int n = people.size();
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                relocate(people.removeFirst());
            }
        }
    }

    // Перемещает человека по дереву
    private void relocate(Human human){
        count--;
        // Если человек в области текущего узла
        if (region.getX() < human.getX() - RADIUS && human.getX() + RADIUS < region.getWidth() &&
            region.getY() < human.getY() - RADIUS && human.getY() + RADIUS < region.getHeight()){
            // то добавляем в эту вершину/ветку
            insert(human);
        }
        // Если нет, то идем на узел выше/больше
        else{
            if (parent != null){
                parent.relocate(human);
            }
            else{
                insert(human);
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
