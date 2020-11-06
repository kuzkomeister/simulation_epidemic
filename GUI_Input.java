package gui_for_epidemic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Integer.parseInt;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class GUI_Input extends JPanel
{  // шрифт 
   Font font = new Font(null, Font.PLAIN, 10);

   // максимальные размеры (ширина и длина) компонент на панели
   int maxX = 40;
   int maxY = 20;
   

   // размеры для отображения компонент и пробелов (пустоты)
   Dimension editD = new Dimension(maxX, maxY);
   Dimension emptyD = new Dimension(0, 5);
   
   // экземпляры классов со статистикой, картой и интерфейса ( нужны для обновления)
   TextInfo textInfo;
   GUI_Map map;
   GUI gui;
    
   // Эдиты для размера карты, количества больных и здоровых
   private final JEditorPane inputX;
   private final JEditorPane inputY;
   private final JEditorPane inputH;
   private final JEditorPane inputI;

   public GUI_Input()
   { // расположение компонент - коробка (по дизайну как стек)
     this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
     
     // рамка для обозначения границ панели
     this.setBorder(BorderFactory.createTitledBorder("Ввод"));
     
     // инициализация текстовых плашек
     JLabel inputTextXY = new JLabel("Размеры карты:");
     JLabel inputTextH = new JLabel("Кол-во здоровых:");
     JLabel inputTextI = new JLabel("Кол-во больных:");

    
     // инициализация Эдитов с дефолтными значениями
     inputX = new JEditorPane("100","100");
     inputY = new JEditorPane("100","100");
     inputH = new JEditorPane("0","0");
     inputI = new JEditorPane("0","0");
     
     // инициализация кнопка запуска симуляции
     JButton okBtn = new JButton("Симуляция!!!!");
     ActionListener btnPressed = new BtnPressed();
     okBtn.addActionListener(btnPressed);
     
     // ставим шрифт для всех компонент
     inputTextXY.setFont(font);
     inputTextH.setFont(font);
     inputTextI.setFont(font);
     okBtn.setFont(font);
     
     // ставим максимальный размер всех компонент
     inputX.setMaximumSize(editD);
     inputY.setMaximumSize(editD);
     inputI.setMaximumSize(editD);
     inputH.setMaximumSize(editD);
     //okBtn.setMaximumSize(editD);
     
     // ставим рамки для эдитов
     inputX.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
     inputY.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
     inputH.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
     inputI.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    
     
     // добавляем на основную панельку все компоненты
     this.add(inputTextXY);
     this.add(Box.createRigidArea(emptyD)); // пустое пространство
     this.add(inputX);
     this.add(Box.createRigidArea(emptyD)); // пустое пространство
     this.add(inputY);
     this.add(Box.createRigidArea(emptyD)); // пустое пространство
     this.add(inputTextH);
     this.add(Box.createRigidArea(emptyD)); // пустое пространство
     this.add(inputH);
     this.add(Box.createRigidArea(emptyD)); // пустое пространство
     this.add(inputTextI);
     this.add(Box.createRigidArea(emptyD)); // пустое пространство
     this.add(inputI);
     this.add(Box.createRigidArea(new Dimension(0, 10))); // пустое пространство  
     this.add(okBtn);
   }
   
   // метод обработки кнопки "симуляция".
   class BtnPressed implements ActionListener
   {  
       @Override
       public void actionPerformed(ActionEvent e) 
       {   // получаем из эдитов строки
           String mapX = inputX.getText(); 
           String mapY = inputY.getText(); 
           String infoHealthy = inputH.getText(); 
           String infoIll = inputI.getText();
           
           // проверка строк на пустоту
           if ( (mapX.isEmpty() ) || (mapY.isEmpty()) || (infoHealthy.isEmpty()) || (infoIll.isEmpty()) )
           { // вывод диалогового окна с ошибкой
             JOptionPane.showMessageDialog(gui, "Заполните все поля!", "Ошибка заполнения", JOptionPane.ERROR_MESSAGE); }
           // проверка строк на принадлежность к числам
           else if ( !isInt(mapX)  || !isInt(mapY) || !isInt(infoHealthy) || !isInt(infoIll) )
           { // вывод диалогового окна с ошибкой
             JOptionPane.showMessageDialog(gui, "Поля должны быть целыми числами!", "Ошибка заполнения", JOptionPane.ERROR_MESSAGE); } 
           // иначе передаём полученные числа на обработку
           else
           { // парсим строки в целые числа
             int map_x = parseInt(mapX); 
             int map_y = parseInt(mapY); 
             int info_healthy = parseInt(infoHealthy); 
             int info_ill = parseInt(infoIll); 
              
              
              // настройка статистики
              String text_info ="<html>Количество людей:  "+ (int)(info_ill+info_healthy)+ "<br><br>" 
                                 + "Количество заболевших: "+ info_ill+ "<br>" 
                                 + "Количество здоровых: "+ info_healthy+ "<br><br></html>";
              
              
              // если панелька со статой существует, то обновляем текст
              if (textInfo != null)
              { textInfo.setLabelText(text_info);}
              // если панелька с картой существует, то обновляем её размеры
              if(map != null)
              { map.setNewSize(map_x, map_y); 
              }
              // интерфейс существует, обновляем его размеры
              gui.setNewSize(map_x, map_y);
              
              //запускаем симуляцию
              Simulation simulation = new Simulation(map_x, map_y, info_healthy, info_ill);
              simulation.printInfoMans();
              simulation.start(10000);
              simulation.printInfoMap();
              
               map.updateMapContent(simulation.getPeople(), 4);
              
            }
       } 
    
    // проверяем является ли введённая строка числом
    private boolean isInt(String s) 
    {  for (char c : s.toCharArray())
       { if (!Character.isDigit(c)) return false; }
       return true; 
    }
    
  }
   
   // для апдейта инфо плашки
   public void updateText(TextInfo textInfo)
   { this.textInfo = textInfo;
   }
   
   // для апдейта размера карты
   public void updateMap(GUI_Map map)
   { this.map = map;
   
   }
   
   //для апдейта размера окошка
   public void updateGUI(GUI gui)
   { this.gui = gui;       
   }
   
}


//класс для заполнения статистики
class TextInfo extends JPanel
{ // строка для отображения статистики
  String text_info;

  // шрифт 
  Font font = new Font(null, Font.PLAIN, 10);  

  // текстовый лейбл для статистики
  private JLabel label = new JLabel("");
  
  public TextInfo()
  { super(new FlowLayout());
    
    // рамка для обозначения границ панели
    this.setBorder(BorderFactory.createTitledBorder("Статистика"));
    
    // начальное значение статистики
    text_info ="<html>Количество людей:  "+ 0+ "<br><br>" 
               + "Количество заболевших: "+ 0+ "<br>" 
               + "Количество здоровых: "+ 0 + "<br><br></html>";

    // инициализация текстового лейбла
    label.setText(text_info); // передаём лейблу текст
    label.setVerticalTextPosition(JLabel.CENTER); // выравниваем текст по центру
    label.setHorizontalTextPosition(JLabel.LEFT); // и слева
    label.setFont(font); // устанавливаем шрифт
    
    // добавляем на панельку лейбл
    this.add(label); 
  } 
  
  // метод для обновления текста на лейбле
  public void setLabelText(String text) {
      label.setText(text);
   }
  
}       

