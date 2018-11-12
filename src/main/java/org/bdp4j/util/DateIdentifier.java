package org.bdp4j.util;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;

/**
 * Procesador de fechas en String
 *
 * @author Jos� Ram�n M�ndez Reboredo
 * @since jdk1.5
 */
public class DateIdentifier {

    boolean debug = true;

    /**
     * Vector de formatos de fecha
     */
    private ArrayList<SimpleDateFormat> sdfs = new ArrayList<SimpleDateFormat>();

    /**
     * Patron singleton (Referencia local)
     */
    private static DateIdentifier defaultDateProcessor = null;

    /**
     * Patron Singleton
     *
     * @return devuelve un procesador de fechas por defecto
     */
    public static DateIdentifier getDefault() {
        if (defaultDateProcessor == null) {
            defaultDateProcessor = new DateIdentifier();
        }
        return defaultDateProcessor;
    }

    /**
     * Constructor privado de dateprocessor
     */
    private DateIdentifier() {
        sdfs.add(new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ (zzz)", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("dd MMM yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("MMM, dd yyyy H:mm:ss aa ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss +ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy zzz", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zz", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, MMM dd H:mm:ss zzz yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, d MMM HH:mm:ss yyyy ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yy HH:mm ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'UT'", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'UT'", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("d MMM yyyy HH:mm:ss 'UT'", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("dd MMM yyyy HH:mm:ss 'UT'", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, dd, MMM yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("EEE, d, MMM yyyy HH:mm:ss ZZZZZ", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH));
        sdfs.add(new SimpleDateFormat("MMM, dd yyyy HH:mm:ss ZZZZZ"));
        sdfs.add(new SimpleDateFormat("MMM, d yyyy HH:mm:ss ZZZZZ"));
        sdfs.add(new SimpleDateFormat("MMM, dd yyyy HH:mm:ss aa"));
        sdfs.add(new SimpleDateFormat("MMM, d yyyy HH:mm:ss aa"));
    }

    public Date checkDate(String dateStr) {
        Date date = null;

        //Hacer un trim de la cadena
        dateStr = dateStr.trim();

        //Sacar los espacios que sobran
        while (dateStr.indexOf("  ") > 0) {
            if (debug) {
                System.out.print("*");
            }
            dateStr = dateStr.replaceAll("  ", " ");
        }

        //Si la cadena est� vac�a devolver null
        if (dateStr.equals("")) {
            return null;
        }

        //Primer intento con DateFormat.FULL en Ingl�s
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, Locale.ENGLISH);
        try {
            date = df.parse(dateStr);
        } catch (java.text.ParseException pe) {
            date = null;
        }

        //Segundo intento con DateFormat.LONG
        if (date == null) {
            df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
            try {
                date = df.parse(dateStr);
            } catch (java.text.ParseException pe) {
                date = null;
            }
        }

        //Tercer intento con DateFormat.MEDIUM
        if (date == null) {
            df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH);
            try {
                date = df.parse(dateStr);
            } catch (java.text.ParseException pe) {
                date = null;
            }
        }

        //Cuarto intento con DateFormat.SHORT
        if (date == null) {
            df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
            try {
                date = df.parse(dateStr);
            } catch (java.text.ParseException pe) {
                date = null;
            }
        }

//		Primer intento con DateFormat.FULL y locale por defecto
        if (date == null) {
            df = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
            try {
                date = df.parse(dateStr);
            } catch (java.text.ParseException pe) {
                date = null;
            }
        }

        //Segundo intento con DateFormat.LONG  y locale por defecto
        if (date == null) {
            df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            try {
                date = df.parse(dateStr);
            } catch (java.text.ParseException pe) {
                date = null;
            }
        }

        //Tercer intento con DateFormat.MEDIUM y locale por defecto
        if (date == null) {
            df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            try {
                date = df.parse(dateStr);
            } catch (java.text.ParseException pe) {
                date = null;
            }
        }

        //Cuarto intento con DateFormat.SHORT y locale por defecto
        if (date == null) {
            df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            try {
                date = df.parse(dateStr);
            } catch (java.text.ParseException pe) {
                date = null;
            }
        }

        //Intento con formatos alternativos
        byte i = 0;
        while (date == null && i < sdfs.size()) {
            date = sdfs.get(i).parse(dateStr, new ParsePosition(0));
            i++;
        }

        return date;
    }
}
