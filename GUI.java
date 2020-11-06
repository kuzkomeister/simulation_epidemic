package gui_for_epidemic;

// РАДИУС СДЕЛАЙ СЛУШАЙ

import javax.swing.*;
import java.awt.*;


// окошко приложения
public class GUI extends JFrame
{  int map_x, map_y; // размеры карты в пикселя
   int window_x, window_y; // размеры окошка
   int proportionX; // для задания размера окна по ширене
   int proportionY; // для задания размера окна по высоте
   

   // создадим окошко пропорциональное размеру карты
   public GUI(int map_x, int map_y, int proportionX, int proportionY, int amountHealthy, int amountIll)
   { super("Эпидемия..."); //Заголовок окна
   
     //инициализация переменных
     this.map_x = map_x;
     this.map_y = map_y;
     this.proportionX = proportionX;
     this.proportionY = proportionY;
     this.window_x = map_x + this.proportionX;
     this.window_y = map_y + this.proportionY;
     
     // минимальный размерчик
     this.setMinimumSize(new Dimension(this.window_x, this.window_y)); 
     // делаем окошко по центру
     setLocationRelativeTo(null); 
     
     // кнопку закрытия делаем...
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
     
     // делаем основную панельку, куда всё будем кидать
     JPanel p = new JPanel(new BorderLayout()); 
     
     // делаем карту
     GUI_Map map = new GUI_Map(this.map_x, this.map_y,amountIll, amountHealthy);
    
     
     // делаем текст
     TextInfo textInfo = new TextInfo();
        
     //------ панель с вводом для пользователя
     GUI_Input input = new GUI_Input();
     input.updateText(textInfo); // для обновления статистики
     input.updateMap(map);// для обновления размера карты
     input.updateGUI(this);// для обновления размера окна
     //--------
 
     // цепляем к панельке наши штуки-дрюки
     p.add(map, BorderLayout.CENTER); // карта по центру
     p.add(textInfo,BorderLayout.EAST); // стата справа
     p.add(input,BorderLayout.WEST); // ввод слева

     // цепляем панель к окну
     this.add(p);

     setVisible(true); // видимость окна
      
   }
   
   // метод для обновления размера окна
   public void setNewSize(int map_x, int map_y)
   { this.window_x = map_x + this.proportionX;
     this.window_y = map_y + this.proportionY;
     
     setSize(this.window_x, this.window_y); // новый размер
     
     setLocationRelativeTo(null); // делаем окошко по центру
     revalidate(); // перерисовка окошка
       
   }

   
}
