package com.simprints.id.tools;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

// TODO: improve language switching. Either don't go against android, or implement a bulletproof solution.
public class LanguageHelper {

    public static Configuration selectLanguage(String languageString) {
        Locale locale = localeFor(languageString);

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        return config;
    }

    private static Locale localeFor(String languageString) {
        Locale locale;

        /*
         * Languages are usually two characters, except if they also contain a region.
         * If they contain a region they follow the format [language]-r[REGION] e.g. fa-rAF
         */
        if (languageString.contains("-r") && languageString.length() > 2) {

            int indexOfFlag = -1;
            for (int i = 0; i < languageString.length() - 1; i++) {
                if (languageString.substring(i, i + 2).equals("-r")) {
                    indexOfFlag = i;
                }
            }

            String language = languageString.substring(0, indexOfFlag);
            String country = languageString.substring(indexOfFlag + 1, languageString.length());

            locale = new Locale(language, country);

        } else {
            locale = new Locale(languageString);
        }

        return locale;
    }

    public static void setLanguage(Context context, String languageString) {
        Resources res = context.getResources();
        res.updateConfiguration(LanguageHelper.selectLanguage(languageString), res.getDisplayMetrics());
    }
}