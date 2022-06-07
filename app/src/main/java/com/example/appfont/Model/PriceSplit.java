package com.example.appfont.Model;

public class PriceSplit {

    public static  String Nam() {

        return Nam();
    }

    public static  String Nam(String number) {
        String str = String.valueOf(number);
        String dau = "";
        if (str.charAt(0) == '-') {
            dau = "-";
            str = str.substring(1);
        }
        while (str.length() % 3 != 0)
            str = "0" + str;
        String result = "";
        for (int i = str.length() - 1; i >= 0; i -= 3) {
            result = "." + str.substring(i - 2, i + 1) + result;
        }
        result = result.substring(1);
        while (result.charAt(0) == '0' && result.length() > 1)
            result = result.substring(1);
        return dau + result;
    }
}
