package midedit;

/**
 * Интерфейс прослушивания событий
 * @author user
 */
public interface AbstractListener {

    /**
     * Метод вызываемый при возникновении события
     * @param itemNum номер выбранного пункта
     */
    public void actionPerformed(int itemNum);
}
