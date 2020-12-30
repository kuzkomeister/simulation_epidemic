package main;

import java.util.LinkedList;

public class Hospital {

    private int iter = 1;

    public final int timeResTest_a, timeResTest_b;      // Интервал времени, получения результатов анализов
    public final int timeDischarge_a, timeDischarge_b;  // Интервал времени, выписывания из больницы

    private LinkedList<ObjectWithTimer> tests;      // Список сданных анализов
    private LinkedList<ObjectWithTimer> patients;   // Список пациентов
    private LinkedList<Human> listTests;            // Список людей сделавших тест

    public final int amountBeds;                  // Количество коек
    public final float probabilityDiedInHospital; // Вероятность умереть в больнице
    public final float testAccuracy;              // Точность проверки на болезнь

    //===== Статистика
    private int stAmountPatients = 0;           // Количество людей пролежащих в больнице
    private int stAmountDied = 0;               // Количество смертей в больнице
    private int stAmountTest = 0;               // Количество сделанных тестов
    private int stAmountFalseTest = 0;           // Количество ложных тестов

    //////////
    // Конструктор
    public Hospital(
            int amountBeds,
            float probabilityDiedInHospital, float testAccuracy,

            int timeResTest_a, int timeResTest_b,
            int timeDischarge_a, int timeDischarge_b
    ){
        patients = new LinkedList<>();
        tests = new LinkedList<>();
        listTests = new LinkedList<>();

        this.amountBeds = amountBeds;

        this.probabilityDiedInHospital = probabilityDiedInHospital;
        this.testAccuracy = testAccuracy;

        this.timeDischarge_a = timeDischarge_a;
        this.timeDischarge_b = timeDischarge_b;

        this.timeResTest_a = timeResTest_a;
        this.timeResTest_b = timeResTest_b;
    }

    // Основной метод объекта
    public void iterate(Simulation simulation) {
        if (iter % 3 == 0) {
            dischargePatients(simulation);
            putTests(simulation);
            putPatients(simulation);
        }
        ++iter;
    }

    // Сбор анализов у симптомных больных
    private void putTests(Simulation simulation){
        for (Human human:simulation.getPeople()){
            if (human.getCondition() == 2 && listTests.contains(human)){
                tests.add(new ObjectWithTimer(human,(int)(Math.random()*(timeResTest_b-timeResTest_a+1)+timeResTest_a)));
                listTests.add(human);
                ++stAmountTest;
            }
        }
    }

    // Приём пациентов в больницу
    private void putPatients(Simulation simulation){
        int i = 0;  // индекс для прохода по списку тестов
        while (i < tests.size()) {
            // Получаем объект если пришел результат теста
            // null - если еще не пришел
            Human human = (Human) tests.get(i).timeHasCome();
            // Если пришел результат и есть места в больнице
            if (human != null && patients.size() < amountBeds ) {
                // Если тест правильно сработал и человек все еще болеет
                if (Math.random() < testAccuracy && human.getCondition() == 2) {
                    // Принимаем пациента
                    patients.add(new ObjectWithTimer(human, (int) (Math.random() * (timeDischarge_b - timeDischarge_a + 1) + timeDischarge_a)));
                    // Убираем его из списка тестов и очереди по совместимости
                    tests.remove(i);
                    // Убираем из области
                    simulation.getPeople().remove(human);
                    // Статистика людей прошедших больницу
                    ++stAmountPatients;
                }
                else{
                    // В случае ложно-отрицательного ответа теста или если человек уже не болеет
                    // Вычеркиваем из списка тестов и очереди по совместимости
                    tests.remove(i);
                    // Статистика ложно-отрицательных тестов
                    ++stAmountFalseTest;
                }
            }
            else{
                // Следующий элемент списка
                ++i;
            }
        }
    }

    // Выписка выздоровевших
    private void dischargePatients(Simulation simulation){
        int i = 0;  // индекс для прохода по списку
        while (i < patients.size()){
            // Получаем объект если пришел результат теста
            // null - если еще не пришел
            Human human = (Human) patients.get(i).timeHasCome();
            if (human != null){
                // Вероятность умереть
                if (Math.random() < probabilityDiedInHospital) {
                    // Если пациент умер
                    // Вычеркиваем из списка пациентов
                    patients.remove(i);
                    // Статистика людей в симуляции
                    simulation.setAmountCond(2,4);
                    // Статистика умерших в больнице
                    ++stAmountDied;
                }
                else{
                    // Лечим человека
                    human.setCondition((byte) 3);
                    // Располагаем на области
                    double rx, ry;   // Рандомные координаты
                    boolean f;      // Флаг поиска свободного места
                    // Распределение по области
                    do {
                        rx =  Math.random() * simulation.getSizeX();
                        ry =  Math.random() * simulation.getSizeY();
                        human.setX(rx);
                        human.setY(ry);
                        float distance = simulation.getNearDistance(human);
                        if (distance != -1)
                            f = distance > 2 * Human.config.radiusMan;
                        else f = true;
                    } while (!f);
                    // Возвращаем человека в мир
                    simulation.getPeople().add(human);
                    // Вычеркиваем его из пациентов
                    patients.remove(i);
                    // Статистика людей в симуляции
                    simulation.setAmountCond(2,3);
                }
            }
            else{
                // Переход на следующий элемент списка
                ++i;
            }
        }
    }

    public void printDebug(){
        System.out.println("=========================");
        System.out.println("Текущее количество тестов: "+tests.size());
        System.out.println("Текущее количество пациентов: "+patients.size());
        System.out.println();
    }


    //===== Статистика
    public int getStAmountPatients() {
        return stAmountPatients;
    }

    public int getStAmountDied() {
        return stAmountDied;
    }

    public int getStAmountTest() {
        return stAmountTest;
    }

    public int getStAmountFalseTest() {
        return stAmountFalseTest;
    }
}
