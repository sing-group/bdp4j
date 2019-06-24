/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
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
    
    /**
     * DateTime format vector
     */    
    private static final ArrayList<DateTimeFormatter> sdfs = new ArrayList<>();

    /**
     * Date format vector
     */
    private static final ArrayList<DateTimeFormatter> patternsDate = new ArrayList<>();

    FormatStyle[] styles = new FormatStyle[] {FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL};
    Locale[] locales = Locale.getAvailableLocales();

    static{
        
        for (String pattern: new String[]{
            "yyyy-MM-dd'T'HH:mm:ss", 
            "yyyy-MM-dd'T'HH:mm", 
            "EEE MMM dd HH:mm:ss ZZZZ yyyy", 
            "EEE MMM dd HH:mm:ss ZZZ yyyy",
            "EEE, dd MMM yyyy HH:mm:ss ZZZZZ (zzz)", 
            "EEE, dd MMM yyyy HH:mm:ss ZZZZZ",
            "dd MMM yyyy HH:mm:ss ZZZZZ",
            "EEE, MMM dd HH:mm:ss yyyy", 
            "MMM, dd yyyy H:mm:ss a ZZZZZ", 
            "EEE, dd MMM yyyy HH:mm:ss +ZZZZZ", 
            "EEE, dd MMM yyyy HH:mm:ss zz", 
            "EEE, MMM dd H:mm:ss zzz yyyy", 
            "EEE, d MMM HH:mm:ss yyyy ZZZZZ", 
            "EEE, dd MMM yy HH:mm ZZZZZ", 
            "EEE, dd MMM yyyy HH:mm:ss 'UT'", 
            "EEE, d MMM yyyy HH:mm:ss 'UT'", 
            "d MMM yyyy HH:mm:ss 'UT'", 
            "dd MMM yyyy HH:mm:ss 'UT'", 
            "EEE, dd MMM yyyy HH:mm:ss", 
            "EEE, d MMM yyyy HH:mm:ss", 
            "dd-MMM-yyyy HH:mm:ss ZZZZZ", 
            "dd/MM/yyyy HH:mm:ss", 
            "EEE, MMM dd yyyy HH:mm:ss ZZZZZ", 
            "dd MMM yyyy HH:mm:ss", 
            "EEE, dd, MMM yyyy HH:mm:ss ZZZZZ", 
            "EEE, d, MMM yyyy HH:mm:ss ZZZZZ", 
            "EEE MMM dd HH:mm:ss zzz yyyy", 
            "MMM, dd yyyy HH:mm:ss ZZZZZ", 
            "MMM, d yyyy HH:mm:ss ZZZZZ", 
            "MMM, dd yyyy HH:mm:ss a", 
            "MMM, d yyyy HH:mm:ss a"}){
            
            sdfs.add(DateTimeFormatter.ofPattern(pattern));
        }
      
        for (String pattern: new String[]{
            "EEE, dd MMM yyyy", 
            "EEE, d MMM yyyy", 
            "EEE, dd MMM yyyy zzz"}){
            
            patternsDate.add(DateTimeFormatter.ofPattern(pattern));
        }   
    }
    
    /**
     * Private constructor
     */
    private DateTimeIdentifier() {
    }

    /**
     * Achives a instance of a Date Identifier
     *
     * @return The default date processor
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
            dateTimeStr = dateTimeStr.replaceAll("  ", " ");
        }

        //Return null if the input string is void
        if (dateTimeStr.equals("")) {
            return null;
        }
        
        DateTimeFormatter dtf;
        
        // DateTime Using localized styles and default locale
        for(int i = 0; date==null && i < styles.length; i++){
            for(int j = 0; date==null &&  j < styles.length; j++){                
                dtf = DateTimeFormatter.ofLocalizedDateTime(styles[i], styles[j]).withLocale(Locale.getDefault());
                try {
                    date = LocalDateTime.parse(dateTimeStr , dtf);
                } catch (DateTimeParseException pe) {
                    date = null;
                }
            }
        } 
         
        
        // DateTime using localized styles
         if (date == null) {
            for (Locale current:locales){
                for(int i = 0; date == null && i < styles.length; i++){
                    for(int j = 0; date == null && j < styles.length; j++){                
                        dtf = DateTimeFormatter.ofLocalizedDateTime(styles[i], styles[j]).withLocale(current);
                        try {
                            date = LocalDateTime.parse(dateTimeStr , dtf);
                        } catch (DateTimeParseException pe) {
                            date = null;
                        }
                   }
                }
                if (date != null) break;
            }
         }
        
        // DateTime using pattern 
        if (date == null) {
            for(Locale current:locales){
                byte i = 0;
                while (date == null && i < sdfs.size()) {                    
                    try{
                        date = LocalDateTime.parse(dateTimeStr, sdfs.get(i).withLocale(current));                 
                    } catch (DateTimeParseException pe) {
                        date = null;
                    }
                    i++;
                }
                if (date!=null) break;
            }
        }
         
        // Date Using localized styles
        if (date==null){
            for (Locale current:locales){
                for(int i = 0; date == null && i < styles.length; i++){
                    LocalDate ld;
                    dtf = DateTimeFormatter.ofLocalizedDate(styles[i]).withLocale(current);
                    try {
                        ld = LocalDate.parse(dateTimeStr, dtf);
                        date = ld.atStartOfDay();
                    } catch (DateTimeParseException pe) {
                        date = null;
                    }
                }
                if (date!=null) break;
            }  
        }
        
        
        //Date using pattern 
        if (date == null) {            
            LocalDate ld;
            byte i = 0;
            for (Locale current:locales){
                while (date == null && i < patternsDate.size()) {
                    try{
                        ld = LocalDate.parse(dateTimeStr, patternsDate.get(i).withLocale(current));
                        date = ld.atStartOfDay();
                    } catch (DateTimeParseException pe) {
                        date=null;
                    }
                    i++;
                }
                if (date!=null) break;
            }
        }
         
        return date;
    }

 
}
