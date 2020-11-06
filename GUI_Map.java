package gui_for_epidemic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class GUI_Map extends JPanel
{ int size_x, size_y; // размеры карты

  int center_x, center_y;

  private ArrayList<Man> people = new ArrayList<>();    // список жалких подопытных людишек
  float radius;
  
  public GUI_Map(int size_x, int size_y, int ill, int healthy)
  { //инициализация переменных
    this.size_x = size_x;
    this.size_y = size_y;
    people.add(new Man((byte)1,3,4));
    
    // рамка для обозначения границ панели
    this.setBorder(BorderFactory.createTitledBorder("Симуляция"));
  }
  
  // метод для обновления размера карты
  public void setNewSize(int size_x, int size_y)
  { this.size_x = size_x;
    this.size_y = size_y;
  }
  
  // метод для отрисовки карты
  @Override
  public void paintComponent(Graphics g)
  { super.paintComponent(g);
    g.setColor(Color.BLACK); // цвет фона - чёрный
    
    this.center_x = (int)(this.getWidth()/2 - this.size_x/2);
    this.center_y = (int)(this.getHeight()/2 - this.size_y/2);
    
    // рисуем четырёхугольник,центр которого находится в центре панели карты
    g.fillRect(this.center_x ,this.center_y ,this.size_x , this.size_y);
    
    Man temp;
    temp = people.get(0);

    g.setColor(Color.GREEN);
    g.drawOval(this.center_x+(int)temp.getX(), this.center_y+(int)temp.getY(), (int)(2*radius), (int)(2*radius));    
    
    // перерисовка карты
    repaint();
  }
  
  public void updateMapContent(ArrayList<Man> man, float r)
  { this.people = man;      
    this.radius = r;
  }
  
}
