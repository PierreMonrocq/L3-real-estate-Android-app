package unicaen.tp.programmationandroid.classes;

import java.text.NumberFormat;
import java.util.Locale;

public class Conversion {

    public static String conversionEntierEuro(int entier){
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        return format.format(entier);
    }

    public static String conversionMinusculeMajuscule(String str){
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }


}
