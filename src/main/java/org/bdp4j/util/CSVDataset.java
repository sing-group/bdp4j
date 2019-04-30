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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Several utilities to create and manage CSV files
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
public class CSVDataset {
    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(CSVDataset.class);

    /**
     * The default CSV filed separator
     */
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
     * Represents default value to indicate if non printable characters should be
     * escaped
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

    /**
     * The file where the dataset is stored
     */
    File csvDataset;

    /**
     * Buffered reader to operate with the file
     */
    private BufferedReader br = null;

    /**
     * Buffered writer to operate with the file
     */
    private BufferedWriter bw = null;

    /**
     * Number of columns for the CSV file
     */
    private Integer columnCount = null;

    private static final Pattern quoteRequiredPattern = Pattern.compile("[" + getCSVSep() + getCharsToScape() + "\\n\\r\u0085'\u2028\u2029]");

    /**
     * Build a CSV file configuring the CSV file
     * 
     * @param CSVSep             The character used to separe fields in CSV
     *                           (semicolon ';' is used as default)
     * @param strQuote           The character to mark strings (double quote '"' is
     *                           used as default)
     * @param strQuoteEscapeChar The character to scape quotes (by default '"'
     *                           according RFC 4180)
     * @param csvVoidField       The character to mark void field (space ' ' by
     *                           default)
     * @param escapeCR           Indicates whether carriage returns should be
     *                           represented as an escape sequence (\n) or not
     * @param charsToScape       Indicates additional characters to scape (only
     *                           double quotes by default)
     * @param csvDataset         The file where the dataset is stored
     */
    public CSVDataset(String CSVSep, String strQuote, String strQuoteEscapeChar, String csvVoidField, Boolean escapeCR,
            String charsToScape, File csvDataset) {
        CSVDataset.CSVSep = CSVSep;
        CSVDataset.strQuote = strQuote;
        CSVDataset.strQuoteEscapeChar = strQuoteEscapeChar;
        CSVDataset.csvVoidField = csvVoidField;
        CSVDataset.escapeCR = escapeCR;
        CSVDataset.charsToScape = charsToScape;

        this.csvDataset = csvDataset;
    }

    /**
     * Build a CSV file configuring the CSV file
     * 
     * @param CSVSep             The character used to separe fields in CSV
     *                           (semicolon ';' is used as default)
     * @param strQuote           The character to mark strings (double quote '"' is
     *                           used as default)
     * @param strQuoteEscapeChar The character to scape quotes (by default '"'
     *                           according RFC 4180)
     * @param csvVoidField       The character to mark void field (space ' ' by
     *                           default)
     * @param escapeCR           Indicates whether carriage returns should be
     *                           represented as an escape sequence (\n) or not
     * @param charsToScape       Indicates additional characters to scape (only
     *                           double quotes by default)
     * @param csvDatasetPath     The file where the dataset is stored
     */
    public CSVDataset(String CSVSep, String strQuote, String strQuoteEscapeChar, String csvVoidField, Boolean escapeCR,
            String charsToScape, String csvDatasetPath) {
        this(DEFAULT_CSV_SEP, DEFAULT_STR_QUOTE, DEFAULT_STR_QUOTE_ESCAPE_CHAR, DEFAULT_CSV_VOID_FIELD,
                DEFAULT_ESCAPE_CR, DEFAULT_CHARS_TO_SCAPE, new File(csvDatasetPath));
    }

    /**
     * Default constructor. The Generated CSV File has ; as field separator and fits
     * RFC 4180 (https://tools.ietf.org/html/rfc4180) for the rest of the fields
     * 
     * @param csvDataset The file where the dataset is stored
     */
    public CSVDataset(File csvDataset) {
        this(DEFAULT_CSV_SEP, DEFAULT_STR_QUOTE, DEFAULT_STR_QUOTE_ESCAPE_CHAR, DEFAULT_CSV_VOID_FIELD,
                DEFAULT_ESCAPE_CR, DEFAULT_CHARS_TO_SCAPE, csvDataset);
    }

    /**
     * Default constructor. The Generated CSV File has ; as field separator and fits
     * RFC 4180 (https://tools.ietf.org/html/rfc4180) for the rest of the fields
     * 
     * @param csvDatasetPath The file where the dataset is stored
     */
    public CSVDataset(String csvDatasetPath) {
        this(DEFAULT_CSV_SEP, DEFAULT_STR_QUOTE, DEFAULT_STR_QUOTE_ESCAPE_CHAR, DEFAULT_CSV_VOID_FIELD,
                DEFAULT_ESCAPE_CR, DEFAULT_CHARS_TO_SCAPE, new File(csvDatasetPath));
    }

    /**
     * Set the CSV separator configured
     *
     * @param CSVSep separator for CSV files
     */
    public static void setCSVSep(String CSVSep) {
        CSVDataset.CSVSep = CSVSep;
    }

    protected CSVDataset() {
        CSVDataset.CSVSep = DEFAULT_CSV_SEP;
        CSVDataset.strQuote = DEFAULT_STR_QUOTE;
        CSVDataset.strQuoteEscapeChar = DEFAULT_STR_QUOTE_ESCAPE_CHAR;
        CSVDataset.csvVoidField = DEFAULT_CSV_VOID_FIELD;
        CSVDataset.escapeCR = DEFAULT_ESCAPE_CR;
        CSVDataset.charsToScape = DEFAULT_CHARS_TO_SCAPE;   
    }

    /**
     * Returns the CSV separator configured
     *
     * @return the configured field separator for CSV files
     */
    public static String getCSVSep() {
        return CSVSep;
    }
    
    public static String getStrVoidField(){
        return csvVoidField;
    }

    public static String getStrQuote(){
        return strQuote;
    }

    public static boolean shouldEscapeCRChars(){
        return escapeCR;
    }

    public static String getCharsToScape(){
        return charsToScape;
    }

    public static String getStrQuoteEscapeChar(){
        return strQuoteEscapeChar;
    }

    /**
     * Escapes CR characters (if required) and quotes
     */
    private static String escapeAll(String in) {
        StringBuilder strb = new StringBuilder();

        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == '\n') {
                strb.append(shouldEscapeCRChars() ? "\\n" : "\n");
            } else if (in.charAt(i) == '\r') {
                strb.append(shouldEscapeCRChars() ? "\\r" : "\r");
            } else if (in.charAt(i) == '\u0085') {
                strb.append(shouldEscapeCRChars() ? "\\u0085" : "\u0085");
            } else if (in.charAt(i) == '\u2028') {
                strb.append(shouldEscapeCRChars() ? "\\u2028" : "\u2028");
            } else if (in.charAt(i) == '\u2029') {
                strb.append(shouldEscapeCRChars() ? "\\u2029" : "\u2029");
            } else if (getCharsToScape().indexOf(in.charAt(i)) != -1 || in.charAt(i) == getStrQuote().charAt(0)) {
                strb.append(getStrQuoteEscapeChar()).append(in.charAt(i));
            } else {
                strb.append(in.charAt(i));
            }
        }
        return strb.toString();
    }

    /**
     * Escape a CSV String to allow including texts into cells
     *
     * @param str The string to scape
     * @return the scaped string
     */
    private static String escapeCSV(String str) {
        StringBuilder str_scape = new StringBuilder();

        if (str == null || str.length() == 0) {
            str_scape.append(getStrVoidField());
        } else {
            if (quoteRequiredPattern.matcher(str).find()) { //If quote is required
                str_scape.append(getStrQuote());
                str_scape.append(
                        escapeAll(str.replaceAll("[\\p{Cntrl}]", ""))
                );
                str_scape.append(getStrQuote());
            } else {
                str_scape.append(str.replaceAll("[\\p{Cntrl}]", ""));
            }
        }
        return str_scape.toString();
    }

    /**
     * Add a row to the dataset
     * 
     * @param values The values
     * @return true if sucessfully added, false otherwise
     */
    public boolean addRow(Object[] values) {
        if (values.length == this.getColumnCount())
            return false;

        if (br != null) flushAndClose();

        try {
            //Open the file for appending
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvDataset, true)));
            for (Object o:values){
                bw.append(escapeCSV(o.toString())).append(getCSVSep());
                
            }
        } catch (FileNotFoundException e) {
            // TODO error and log
            return false;
        } catch ( IOException e){
            // TODO error and log
            return false;
        }

        return true;
    }

    /**
     * Add a column to the dataset
     * @param columnName The name of the column
     * @param defaultValue The default value for the rows included
     */
    public void addColumn(String columnName, String defaultValue) {
        if (defaultValue == null || defaultValue.length() == 0) {
            defaultValue = "0";
        }

        flushAndClose();
        if (columnCount==null){
            getColumnCount();
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
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
            columnCount++;
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     * Returns the number of columns for the dataset
     * @return the number of the columns for the dataset
     */
    public int getColumnCount(){
        if (columnCount==null) getColumnNames();
        return columnCount;
    }

    /**
     * Returns the colummn names for the CSVDataset
     * @return an array with the column names of the dataset
     */
    public String[] getColumnNames() {
        flushAndClose();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csvDataset)));
            String line = br.readLine();
            if (line != null) {
                String []retVal=line.split(Pattern.quote(getCSVSep()));
                this.columnCount=retVal.length;
                flushAndClose();    
                return retVal;
            }

        } catch (Exception e) {
            logger.warn(e.getMessage());
        } 

        return null;
    }

    /**
     * Flush and close a dataset
     */
    public void flushAndClose(){
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                //TODO: log and exit
            }
            br=null;
        }

        if (bw != null){
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                //TODO: log and exit
            }
            bw=null;
        }
    }


}
