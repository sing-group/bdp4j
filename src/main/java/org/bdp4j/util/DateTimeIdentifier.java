/*
 * BDP4j implements a pipeline framework to allow definining 
 * project pipelines from XML. The main goal of the pipelines of this 
 * application is to transform imput data received from multiple sources 
 * into fully qualified datasets to be used with Machine Learning.
 *
 * Copyright (C) 2018  Sing Group (University of Vigo)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.bdp4j.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.time.format.DateTimeParseException;

/**
 * Identifies dates from their string representation
 *
 * @author Maria Novo
 * @author Reyes Pavon
 * @since jdk1.8
 */
public class DateTimeIdentifier {

    /**
     * Singleton pattern (local reference)
     */
    private static DateTimeIdentifier defaultDateTimeProcessor = null;
    //boolean debug = true;

    /**
     * Date format vector
     */
    private ArrayList<DateTimeFormatter> sdfs = new ArrayList<>(); 
    private ArrayList<DateTimeFormatter> patternsDate = new ArrayList<>();

    FormatStyle[] styles = new FormatStyle[] {FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL};
    Locale[] locales = Locale.getAvailableLocales();

    
    /**
     * Private constructor
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
     * Achives a instance of a Date Identifier
     *
     * @return Te default date processor
     */
    public static DateTimeIdentifier getDefault() {
        if (defaultDateTimeProcessor == null) {
            defaultDateTimeProcessor = new DateTimeIdentifier();
        }
        return defaultDateTimeProcessor;
    }


    /**
     * Scan a string to find the represented dateTime
     * @param dateTimeStr The string to analyze
     * @return The date/time represented by the string (null if the format can not be guessed)
     */
    public LocalDateTime checkDateTime(String dateTimeStr) {
     
        LocalDateTime date = null;

        //Do a trim from string
        dateTimeStr = dateTimeStr.trim();

        //Drop unnecesary spaces
        while (dateTimeStr.indexOf("  ") > 0) {
            //if (debug) {
            //    System.out.print("*");
            //}
            dateTimeStr = dateTimeStr.replaceAll("  ", " ");
        }

        //Return null if the input string is void
        if (dateTimeStr.equals("")) {
            return null;
        }
        
        DateTimeFormatter dtf;
        
        // DateTime Using localized styles and default locale
        boolean found=false;
        for (Locale current:locales){
            for(int i = 0; !found && i < styles.length; i++){
                for(int j = 0; !found &&  j < styles.length; j++){                
                    if (date == null){    
                        dtf = DateTimeFormatter.ofLocalizedDateTime(styles[i], styles[j]).withLocale(current);
                        // dtf = DateTimeFormatter.ofLocalizedDateTime(styles[i], styles[j]).withLocale(Locale.getDefault());
                        try {
                            date = LocalDateTime.parse(dateTimeStr , dtf);
                            //System.out.println("Style " +styles[i] + ", " + styles[j] + ", " + current + ": " + date);
                            found=true;
                        } catch (DateTimeParseException pe) {
                            date = null;
                        }
                    }
                }
            } 
            if (found) break;
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
                                //System.out.println("Style " +styles[i] + ", " + styles[j] + ", " + current + ": " + date);
                            } catch (DateTimeParseException pe) {
                                date = null;
                            }
                        }
                    }
                }  
            }
         }
        
        // DateTime using pattern 
         if (date == null) {
            for(Locale current:locales){
                //System.out.println("Locale:"+ current.toString());
                byte i = 0;
                while (date == null && i < sdfs.size()) {
                    //System.out.println("format "+i);
                    try{
                        date = LocalDateTime.parse(dateTimeStr, sdfs.get(i).withLocale(current));
                        //System.out.println("Pattern DateTime");
                        //System.out.println("caso -> "+ date);                    
                    } catch (DateTimeParseException pe) {

                    }
                    i++;
                }
                if (date!=null) break;
            }
        }
         
        // Date Using localized styles
        if (date==null){
            for (Locale current:locales){
                for(int i = 0; i < styles.length; i++){
                    if (date == null){ 
                        LocalDate ld;
                        dtf = DateTimeFormatter.ofLocalizedDate(styles[i]).withLocale(current);
                        try {
                            ld = LocalDate.parse(dateTimeStr, dtf);
                            //System.out.println("Style " + styles[i] + ", " + current + ": " + ld);
                            date = ld.atStartOfDay();
                        } catch (DateTimeParseException pe) {
                            date = null;
                        }
                    }
                }  
            }
        }
        
        //Date using pattern 
        if (date == null) {            
            LocalDate ld;
            byte i = 0;
            
            //while (date == null && i < sdfs.size()) {
            while (date == null && i < patternsDate.size()) {
                try{
                    ld = LocalDate.parse(dateTimeStr, patternsDate.get(i));
                    //System.out.println("Pattern Date");
                    //System.out.println("caso -> "+ ld);
                    date = ld.atStartOfDay();
                    
                } catch (DateTimeParseException pe) {
                    date=null;
                }
                i++;
            }
        }
         
        return date;
    }

 
}
