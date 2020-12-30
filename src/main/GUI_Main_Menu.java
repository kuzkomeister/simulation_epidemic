package main;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

public class GUI_Main_Menu extends JFrame {
    private JPanel rootPanel;
    private JPanel statistic;
    private GUI_Region region;

    protected class GUI_Region extends JPanel{
        private LinkedList<Human> people;
        private QuadTree root;
        private double sizeX, sizeY;

        private boolean PAINT_QUADTREE = false;
        private boolean PAINT_SOCDIST = false;
        private boolean PAINT_MEETS = false;



        public GUI_Region(
                double sizeX, double sizeY,
                LinkedList<Human> people,
                QuadTree root
        ){
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.people = people;
            this.root = root;
        }

        private void paintQuadTree(Graphics2D g2d, QuadTree node, int level){
            if (node != null) {
                // Выбор цвета в зависимости от уровня
                switch(level){
                    case 1 -> g2d.setColor(Color.BLACK);
                    case 2 -> g2d.setColor(Color.DARK_GRAY);
                    case 3 -> g2d.setColor(Color.GRAY);
                    case 4 -> g2d.setColor(Color.LIGHT_GRAY);
                    case 5 -> g2d.setColor(Color.WHITE);
                    default -> g2d.setColor(Color.ORANGE);
                }
                // Рисуем область
                Rectangle2D rect = node.getRegion();
                g2d.fill(rect);
                // Вызов потомков дерева
                paintQuadTree(g2d, node.getChild(0), level+1);
                paintQuadTree(g2d, node.getChild(1), level+1);
                paintQuadTree(g2d, node.getChild(2), level+1);
                paintQuadTree(g2d, node.getChild(3), level+1);
            }
        }

        @Override
        public void paintComponent(Graphics g){
                                                        int SIZE = 10;
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // Рисуем фон карты
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fill(new Rectangle2D.Double(0,0,sizeX*SIZE,sizeY*SIZE));
            // Рисуем области
            if (PAINT_QUADTREE && root != null){
                for (Human human:people)
                    root.insert(human);
                paintQuadTree(g2d,root,1);
                root.clear();
            }
            // Рисуем области соц дистанции
            if (PAINT_SOCDIST){
                for (Human human:people){
                    if (human.socDist){
                        g2d.setColor(Color.CYAN);

                        g2d.fillOval(
                                (int) (human.getX() * SIZE + 7 - SIZE * 3),
                                (int) (human.getY() * SIZE + 31 - SIZE * 3),
                                2 * (int) (Human.config.radiusSoc * SIZE),
                                2 * (int) (Human.config.radiusSoc * SIZE)
                        );
                    }
                }
            }
            // Рисуем "встречи"
            /*
            if (PAINT_MEETS){
                for (Point p : sneeze) {
                    g.setColor(Color.PINK);
                    g.fillOval(
                            (int) (p.getX() * SIZE + 7 - SIZE * 2),
                            (int) (p.getY() * SIZE + 31 - SIZE * 2),
                            2 * (int) (Human.config.radiusInf * SIZE),
                            2 * (int) (Human.config.radiusInf * SIZE)
                    );
                }
                sneeze.clear();
            }
             */
            // Рисуем людей
            for (Human human:people){
                // Определение цвета в зависимости от состояния
                switch (human.getCondition()) {
                    case 0 -> g2d.setColor(Color.GREEN);
                    case 1 -> g2d.setColor(Color.MAGENTA);
                    case 2 -> g2d.setColor(Color.RED);
                    case 3 -> g2d.setColor(Color.BLUE);
                    case 5 -> g2d.setColor(Color.YELLOW);
                }
                // Рисуем человека
                g2d.fillOval((int) (human.getX() * SIZE + 7), (int) (human.getY() * SIZE + 31), 2 * (int)Human.config.radiusMan, 2 * (int)Human.config.radiusMan);
                // Контур
                g2d.setColor(Color.BLACK);
                g2d.drawOval((int) (human.getX() * SIZE + 7), (int) (human.getY() * SIZE + 31), 2 * (int)Human.config.radiusMan, 2 * (int)Human.config.radiusMan);
            }


        }
    }

    public GUI_Main_Menu(
            double sizeX, double sizeY,
            LinkedList<Human> people,
            QuadTree root
    ) {
        super("Симуляция эпидемии");
        setSize(1280,720);


        //===== Основная панель
        rootPanel = new JPanel();
        rootPanel.setBackground(Color.GRAY); // Цвет фона
        //=== Панель карты
        region = new GUI_Region(sizeX, sizeY, people, root);
        // Устанавливаем название и границу
        region.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Карта"));
        region.setPreferredSize(new Dimension(100,getHeight()));

        //=== Панель текущей статистики
        statistic = new JPanel();
        // Устанавливаем название и границу
        statistic.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Текущая статистика"));
        statistic.setPreferredSize(new Dimension(280,getHeight()));




        // Собираем все компоненты
        rootPanel.add(region,BorderLayout.WEST);
        rootPanel.add(statistic,BorderLayout.EAST);
        setContentPane(rootPanel);
        //=====
        // Делаем видимым окно
        setVisible(true);
        // Запрещаем менять размер окна
        setResizable(false);
        // Закрытие окна при нажатии крестика
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Делаем на весь экран
        //setExtendedState(MAXIMIZED_BOTH);
    }





}
