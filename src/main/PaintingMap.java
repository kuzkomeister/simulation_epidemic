package main;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

public class PaintingMap extends Frame {

    private int sizeX, sizeY;
    private int radius;
    public QuadTree root;
    private LinkedList<Human> people;
    private LinkedList<Point> sneeze;
    static final int SIZE = 20;
    private boolean DEBUG = true;

    public PaintingMap(int sizeX, int sizeY, float radius, LinkedList<Human> people, LinkedList<Point> sneeze){
        super("Карта");
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        setLayout(new FlowLayout());

        this.sizeX = sizeX*SIZE;
        this.sizeY = sizeY*SIZE;
        this.radius = (int)(radius*SIZE);
        this.people = people;
        this.sneeze = sneeze;
        this.root = new QuadTree(new Rectangle(0,0,sizeX,sizeY));

        setSize(this.sizeX+55,this.sizeY+75);
    }

    private void paintQuadTree(QuadTree r, Graphics g, int lvl){

        if (r != null) {
            Rectangle2D rect = r.getRegion();

            switch(lvl){
                case 1 -> g.setColor(Color.BLACK);
                case 2 -> g.setColor(Color.DARK_GRAY);
                case 3 -> g.setColor(Color.GRAY);
                case 4 -> g.setColor(Color.LIGHT_GRAY);
                case 5 -> g.setColor(Color.WHITE);
                default -> g.setColor(Color.ORANGE);
            }

            g.fillRect( (int) (rect.getX() * SIZE+7), (int) (rect.getY() * SIZE+31),
                    (int) (rect.getWidth() * SIZE-1), (int) (rect.getHeight() * SIZE-1));

            paintQuadTree(r.getChild(0), g, lvl+1);
            paintQuadTree(r.getChild(1), g, lvl+1);
            paintQuadTree(r.getChild(2), g, lvl+1);
            paintQuadTree(r.getChild(3), g, lvl+1);
        }

    }

    @Override
    public void paint(Graphics g){
        // фон
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0,0,getWidth(),getHeight());

        // Области
        if (root != null && DEBUG) {
            for (Human human : people) {
                root.insert(human);
            }
            paintQuadTree(root, g, 1);

            root.clear();
        }

        if (people != null) {
            // Соц дистанция
            if (DEBUG) {
                for (Human human : people) {
                    if (human.socDist) {
                        g.setColor(Color.CYAN);
                        g.fillOval(
                                (int) (human.getX() * SIZE + 7 - SIZE * 2),
                                (int) (human.getY() * SIZE + 31 - SIZE * 2),
                                2 * (int) (Human.config.radiusSoc * SIZE),
                                2 * (int) (Human.config.radiusSoc * SIZE)
                        );
                    }
                }
            }
            // Чих
            if (sneeze.size() != 0) {
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
            // Человек
            for (Human human : people) {
                /*
                if (human.getInfectHand()){
                    g.setColor(Color.RED);
                }
                else{
                    g.setColor(Color.WHITE);
                }
                g.fillOval((int)(human.getX()*SIZE+7),(int)(human.getY()*SIZE+31),2*radius,2*radius);
                g.setColor(Color.BLACK);
                g.drawOval((int)(human.getX()*SIZE+7),(int)(human.getY()*SIZE+31),2*radius,2*radius);
                 */
                switch (human.getCondition()) {
                    case 0 -> g.setColor(Color.GREEN);
                    case 1 -> g.setColor(Color.MAGENTA);
                    case 2 -> g.setColor(Color.RED);
                    case 3 -> g.setColor(Color.BLUE);
                    case 5 -> g.setColor(Color.YELLOW);
                }
                //g.fillOval((int)(human.getX()*SIZE+7+SIZE/2),(int)(human.getY()*SIZE+31+SIZE/2),2*radius-SIZE,2*radius-SIZE);
                g.fillOval((int) (human.getX() * SIZE + 7), (int) (human.getY() * SIZE + 31), 2 * radius, 2 * radius);

                //g.setColor(Color.BLACK);
                //g.drawOval((int)(human.getX()*SIZE+7+SIZE/2),(int)(human.getY()*SIZE+31+SIZE/2),2*radius-SIZE,2*radius-SIZE);

                g.setColor(Color.BLACK);
                g.drawOval((int) (human.getX() * SIZE + 7), (int) (human.getY() * SIZE + 31), 2 * radius, 2 * radius);
            }
        }

    }

}