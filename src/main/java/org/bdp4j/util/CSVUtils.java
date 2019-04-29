/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Several utilities to create and manage CSV files
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
public class CSVUtils {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(CSVUtils.class);

    private static final String DEFAULT_CSV_SEP = ";";

    /**
     * The default value to String Quote delimiter
     */
    private static final String DEFAULT_STR_QUOTE = "\"";

    /**
     * The default value to Char to Escape String Quote delimiters
     */
    private static final String DEFAULT_STR_QUOTE_ESCAPE_CHAR = "\"";

    /**
     * The default value to a representation of a CSV VOID FIELD
     */
    private static final String DEFAULT_CSV_VOID_FIELD = "<space>";

    /**
     * Represents default value to indicate if non printable characters should
     * be escaped
     */
    private static final Boolean DEFAULT_ESCAPE_CR = false;

    /**
     * Default value to chars that should be scapped
     */
    private static final String DEFAULT_CHARS_TO_SCAPE = "\"";

    /**
     * The configured CSV Separator
     */
    private static String CSVSep = DEFAULT_CSV_SEP;

    /**
     * The String Quote delimiter
     */
    private static String strQuote = DEFAULT_STR_QUOTE;

    /**
     * The Char to Escape String Quote delimiters
     */
    private static String strQuoteEscapeChar = DEFAULT_STR_QUOTE_ESCAPE_CHAR;

    /**
     * The representation of a CSV VOID FIELD
     */
    private static String csvVoidField = DEFAULT_CSV_VOID_FIELD;

    /**
     * Represents if \n \r and other non printable characters should be escaped
     */
    private static Boolean escapeCR = DEFAULT_ESCAPE_CR;

    /**
     * Chars that should be scapped
     */
    private static String charsToScape = DEFAULT_CHARS_TO_SCAPE;

    public CSVUtils(String CSVSep, String strQuote, String strQuoteEscapeChar, String csvVoidField, Boolean escapeCR, String charsToScape) {
        CSVUtils.CSVSep = CSVSep;
        CSVUtils.strQuote = strQuote;
        CSVUtils.strQuoteEscapeChar = strQuoteEscapeChar;
        CSVUtils.csvVoidField = csvVoidField;
        CSVUtils.escapeCR = escapeCR;
        CSVUtils.charsToScape = charsToScape;
    }

    public CSVUtils() {

    }

    /**
     * Set the CSV separator configured
     *
     * @param CSVSep separator for CSV files
     */
    public static void setCSVSep(String CSVSep) {
        CSVUtils.CSVSep = CSVSep;
    }

    /**
     * Returns the CSV separator configured
     *
     * @return the configured field separator for CSV files
     */
    public static String getCSVSep() {
        return CSVSep;
    }

    public static void addColumnToCSV(String path, String filename, String columnName, String defaultValue) {
        if (defaultValue == null || defaultValue.length() == 0) {
            defaultValue = "0";
        }
        final String lineSep = System.getProperty("line.separator");

        File sourceFile = new File(path, filename);
        File destinationFile = new File(path, "copy_" + filename);

        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));) {
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                int initLastColumn = line.lastIndexOf(getCSVSep());
                if (initLastColumn > 0) {
                    String beforeNewColumn = line.substring(0, initLastColumn);
                    String afterNewColumn = line.substring(initLastColumn + 1);
                    String column = ((i == 0) ? columnName : defaultValue);
                    bw.write(beforeNewColumn + getCSVSep() + column + getCSVSep() + afterNewColumn + lineSep);
                }
            }
            bw.flush();
            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public static String getColumns(String filename) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {

            String line = br.readLine();
            if (line != null) {
                return line;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return "";
    }

    /* PRUEBA */
    public static void main(String[] args) {
        addColumnToCSV("./", "output_youtube_test.csv", "bn:23232367", null);
        CSVUtils x = new CSVUtils(CSVSep, strQuote, strQuoteEscapeChar, csvVoidField, escapeCR, charsToScape);
    }
}
