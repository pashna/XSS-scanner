package xss.report;

import java.text.DateFormatSymbols;

/**
 * Created by popka on 24.05.15.
 */
public class RussianDateFormatSymbols extends DateFormatSymbols {

    @Override
    public String[] getMonths() {
        return new String[]{"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня",
                "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};
    }

}
