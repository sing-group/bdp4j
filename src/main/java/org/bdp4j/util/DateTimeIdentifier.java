
package org.bdp4j.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Procesador de fechas en String
 *
 * @author 
 * @since jdk1.8
 */
public class DateTimeIdentifier {

    /**
     * Patron singleton (Referencia local)
     */
    private static DateTimeIdentifier defaultDateTimeProcessor = null;
    boolean debug = true;
    /**
     * Vector de formatos de fecha
     */
    private ArrayList<DateTimeFormatter> sdfs = new ArrayList<>(); 
    private ArrayList<DateTimeFormatter> patternsDate = new ArrayList<>();

    FormatStyle[] styles = new FormatStyle[] {FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL};
    Locale[] locales = Locale.getAvailableLocales();

    
    /**
     * Constructor privado de datetimeprocessor
     */
    private DateTimeIdentifier() {
        sdfs.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        sdfs.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        sdfs.add(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss ZZZZ yyyy"));
        sdfs.add(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss ZZZ yyyy"));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss ZZZZZ (zzz)"));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss ZZZZZ"));
        sdfs.add(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss ZZZZZ"));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, MMM dd HH:mm:ss yyyy").withLocale(Locale.UK));
        sdfs.add(DateTimeFormatter.ofPattern("MMM, dd yyyy H:mm:ss a ZZZZZ"));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss +ZZZZZ"));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zz").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, MMM dd H:mm:ss zzz yyyy").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, d MMM HH:mm:ss yyyy ZZZZZ").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd MMM yy HH:mm ZZZZZ").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'UT'").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss 'UT'").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss 'UT'").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss 'UT'").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss ZZZZZ").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, MMM dd yyyy HH:mm:ss ZZZZZ").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, dd, MMM yyyy HH:mm:ss ZZZZZ").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE, d, MMM yyyy HH:mm:ss ZZZZZ").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy").withLocale(Locale.ENGLISH));
        sdfs.add(DateTimeFormatter.ofPattern("MMM, dd yyyy HH:mm:ss ZZZZZ"));
        sdfs.add(DateTimeFormatter.ofPattern("MMM, d yyyy HH:mm:ss ZZZZZ"));
        sdfs.add(DateTimeFormatter.ofPattern("MMM, dd yyyy HH:mm:ss a"));
        sdfs.add(DateTimeFormatter.ofPattern("MMM, d yyyy HH:mm:ss a"));
        patternsDate.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy").withLocale(Locale.ENGLISH)); //only date
        patternsDate.add(DateTimeFormatter.ofPattern("EEE, d MMM yyyy").withLocale(Locale.ENGLISH)); //only date
        patternsDate.add(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy zzz").withLocale(Locale.ENGLISH)); // only date
    }

    /**
     * Patron Singleton
     *
     * @return devuelve un procesador de fechas por defecto
     */
    public static DateTimeIdentifier getDefault() {
        if (defaultDateTimeProcessor == null) {
            defaultDateTimeProcessor = new DateTimeIdentifier();
        }
        return defaultDateTimeProcessor;
    }

    public LocalDateTime checkDateTime(String dateTimeStr) {
     
        LocalDateTime date = null;

        //Hacer un trim de la cadena
        dateTimeStr = dateTimeStr.trim();

        //Sacar los espacios que sobra
        while (dateTimeStr.indexOf("  ") > 0) {
            if (debug) {
                System.out.print("*");
            }
            dateTimeStr = dateTimeStr.replaceAll("  ", " ");
        }

        //Si la cadena est� vac�a devolver null
        if (dateTimeStr.equals("")) {
            return null;
        }
        
        DateTimeFormatter dtf;
        // DateTime Using localized styles and default locale
        for (Locale current:locales){
            for(int i = 0; i < styles.length; i++){
                for(int j = 0; j < styles.length; j++){                
                    if (date == null){    
                        dtf = DateTimeFormatter.ofLocalizedDateTime(styles[i], styles[j]).withLocale(Locale.getDefault());
                        try {
                            date = LocalDateTime.parse(dateTimeStr , dtf);
                            System.out.println("Style " +styles[i] + ", " + styles[j] + ", " + current + ": " + date);
                        } catch (java.time.format.DateTimeParseException pe) {
                            date = null;
                        }
                    }
                }
            }  
        }
        
        // DateTime Using localized styles
         if (date == null) {
            for (Locale current:locales){
                for(int i = 0; i < styles.length; i++){
                    for(int j = 0; j < styles.length; j++){                
                        if (date == null){    
                            dtf = DateTimeFormatter.ofLocalizedDateTime(styles[i], styles[j]).withLocale(current);
                            try {
                                date = LocalDateTime.parse(dateTimeStr , dtf);
                                System.out.println("Style " +styles[i] + ", " + styles[j] + ", " + current + ": " + date);
                            } catch (java.time.format.DateTimeParseException pe) {
                                date = null;
                            }
                        }
                    }
                }  
            }
         }
        
        // DateTime using pattern 
         if (date == null) {
            
            byte i = 0;
            while (date == null && i < sdfs.size()) {
                try{
                    date = LocalDateTime.parse(dateTimeStr, sdfs.get(i));    
                    System.out.println("Pattern DateTime");
                    System.out.println("caso -> "+ date);
                    
                } catch (java.time.format.DateTimeParseException pe) {
                    date=null;
                }
                i++;
            }
            
        }
         
        // Date Using localized styles
        
        for (Locale current:locales){
            for(int i = 0; i < styles.length; i++){
                if (date == null){ 
                    LocalDate ld;
                    dtf = DateTimeFormatter.ofLocalizedDate(styles[i]).withLocale(current);
                    try {
                        ld = LocalDate.parse(dateTimeStr, dtf);
                        System.out.println("Style " + styles[i] + ", " + current + ": " + ld);
                        date = ld.atStartOfDay();
                    } catch (java.time.format.DateTimeParseException pe) {
                        date = null;
                    }
                }
            }  
        }
        
        //Date using pattern 
         if (date == null) {            
            LocalDate ld;
            byte i = 0;
            while (date == null && i < sdfs.size()) {
                try{
                    ld = LocalDate.parse(dateTimeStr, patternsDate.get(i));
                    System.out.println("Pattern Date");
                    System.out.println("caso -> "+ ld);
                    date = ld.atStartOfDay();
                    
                } catch (java.time.format.DateTimeParseException pe) {
                    date=null;
                }
                i++;
            }
        }
         
        return date;
    }

 
}
