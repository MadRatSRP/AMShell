package midedit;

/**
 * Интерфейс статуса операций
 * @author user
 */
public interface Waitable {

    /**
     * Получить текущее состояние
     * @return процент выполненной задачи
     */
    public int getCurPercent();

    /**
     * Отмена операции
     */
    public void cancel();

    /**
     * Сброс операции
     */
    public void reset();
}
