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
import org.bdp4j.types.ColumnDefinition;

/**
 * Several utilities to create and manage CSV files
 *
 * @author María Novo
 * @author José Ramón Méndez
 */
public class CSVDatasetWriter {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(CSVDatasetWriter.class);

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
     * @param CSVSep The character used to separe fields in CSV (semicolon ';'
     * is used as default)
     * @param strQuote The character to mark strings (double quote '"' is used
     * as default)
     * @param strQuoteEscapeChar The character to scape quotes (by default '"'
     * according RFC 4180)
     * @param csvVoidField The character to mark void field (space ' ' by
     * default)
     * @param escapeCR Indicates whether carriage returns should be represented
     * as an escape sequence (\n) or not
     * @param charsToScape Indicates additional characters to scape (only double
     * quotes by default)
     * @param csvDataset The file where the dataset is stored
     */
    public CSVDatasetWriter(String CSVSep, String strQuote, String strQuoteEscapeChar, String csvVoidField, Boolean escapeCR,
            String charsToScape, File csvDataset) {
        CSVDatasetWriter.CSVSep = CSVSep;
        CSVDatasetWriter.strQuote = strQuote;
        CSVDatasetWriter.strQuoteEscapeChar = strQuoteEscapeChar;
        CSVDatasetWriter.csvVoidField = csvVoidField;
        CSVDatasetWriter.escapeCR = escapeCR;
        CSVDatasetWriter.charsToScape = charsToScape;

        this.csvDataset = csvDataset;
    }

    /**
     * Build a CSV file configuring the CSV file
     *
     * @param CSVSep The character used to separe fields in CSV (semicolon ';'
     * is used as default)
     * @param strQuote The character to mark strings (double quote '"' is used
     * as default)
     * @param strQuoteEscapeChar The character to scape quotes (by default '"'
     * according RFC 4180)
     * @param csvVoidField The character to mark void field (space ' ' by
     * default)
     * @param escapeCR Indicates whether carriage returns should be represented
     * as an escape sequence (\n) or not
     * @param charsToScape Indicates additional characters to scape (only double
     * quotes by default)
     * @param csvDatasetPath The file where the dataset is stored
     */
    public CSVDatasetWriter(String CSVSep, String strQuote, String strQuoteEscapeChar, String csvVoidField, Boolean escapeCR,
            String charsToScape, String csvDatasetPath) {
        this(DEFAULT_CSV_SEP, DEFAULT_STR_QUOTE, DEFAULT_STR_QUOTE_ESCAPE_CHAR, DEFAULT_CSV_VOID_FIELD,
                DEFAULT_ESCAPE_CR, DEFAULT_CHARS_TO_SCAPE, new File(csvDatasetPath));
    }

    /**
     * Default constructor. The Generated CSV File has ; as field separator and
     * fits RFC 4180 (https://tools.ietf.org/html/rfc4180) for the rest of the
     * fields
     *
     * @param csvDataset The file where the dataset is stored
     */
    public CSVDatasetWriter(File csvDataset) {
        this(DEFAULT_CSV_SEP, DEFAULT_STR_QUOTE, DEFAULT_STR_QUOTE_ESCAPE_CHAR, DEFAULT_CSV_VOID_FIELD,
                DEFAULT_ESCAPE_CR, DEFAULT_CHARS_TO_SCAPE, csvDataset);
    }

    /**
     * Default constructor. The Generated CSV File has ; as field separator and
     * fits RFC 4180 (https://tools.ietf.org/html/rfc4180) for the rest of the
     * fields
     *
     * @param csvDatasetPath The file where the dataset is stored
     */
    public CSVDatasetWriter(String csvDatasetPath) {
        this(DEFAULT_CSV_SEP, DEFAULT_STR_QUOTE, DEFAULT_STR_QUOTE_ESCAPE_CHAR, DEFAULT_CSV_VOID_FIELD,
                DEFAULT_ESCAPE_CR, DEFAULT_CHARS_TO_SCAPE, new File(csvDatasetPath));
    }

    /**
     * Set the CSV separator configured
     *
     * @param CSVSep separator for CSV files
     */
    public static void setCSVSep(String CSVSep) {
        CSVDatasetWriter.CSVSep = CSVSep;
    }

    protected CSVDatasetWriter() {
        CSVDatasetWriter.CSVSep = DEFAULT_CSV_SEP;
        CSVDatasetWriter.strQuote = DEFAULT_STR_QUOTE;
        CSVDatasetWriter.strQuoteEscapeChar = DEFAULT_STR_QUOTE_ESCAPE_CHAR;
        CSVDatasetWriter.csvVoidField = DEFAULT_CSV_VOID_FIELD;
        CSVDatasetWriter.escapeCR = DEFAULT_ESCAPE_CR;
        CSVDatasetWriter.charsToScape = DEFAULT_CHARS_TO_SCAPE;
    }

    /**
     * Returns the CSV separator configured
     *
     * @return the configured field separator for CSV files
     */
    public static String getCSVSep() {
        return CSVSep;
    }

    public static String getStrVoidField() {
        return csvVoidField;
    }

    public static String getStrQuote() {
        return strQuote;
    }

    public static boolean shouldEscapeCRChars() {
        return escapeCR;
    }

    public static String getCharsToScape() {
        return charsToScape;
    }

    public static String getStrQuoteEscapeChar() {
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
        final String lineSep = System.getProperty("line.separator");

        //
        if (values.length != this.getColumnCount()) {
            logger.error("Adding row with " + values.length + " columns when the dataset has " + getColumnCount());
            return false;
        }

        if (br != null) {
            flushAndClose();
        }

        try {
            //Open the file for appending
            if (bw == null) {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvDataset, true)));
            }

            for (int j = 0; j < values.length; j++) {
                Object o = values[j];
                bw.append(escapeCSV(o.toString()));
                if (j < values.length - 1) {
                    bw.append(getCSVSep());
                }
            }
            bw.append(lineSep);
        } catch (FileNotFoundException e) {
            logger.error("Unable to find/create file " + csvDataset.getAbsolutePath());
            return false;
        } catch (IOException e) {
            logger.error("I/O error when manipulating file " + csvDataset.getAbsolutePath());
            return false;
        }

        return true;
    }

    /**
     * Add several rows to the dataset
     *
     * @param values The rows of values
     * @return true if sucessfully added, false otherwise
     */
    public boolean addRows(Object[][] values) {
        boolean retVal = true;
        for (Object currentRow[] : values) {
            retVal = retVal & addRow(currentRow);
            if (!retVal) {
                return retVal;
            }
        }

        return retVal;
    }

    /**
     * Add a col to the dataset
     *
     * @param columnName The name of the col
     * @param defaultValue The default value for the rows included
     * @return true if sucessfull, false otherwise
     */
    public boolean addColumn(String columnName, Object defaultValue) {
        if (defaultValue == null || defaultValue.toString().length() == 0) {
            defaultValue = "0";
        }

        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        if (!sourceFile.exists()) {
            try {
                sourceFile.createNewFile();
            } catch (IOException e1) {
                logger.error("Unable to create the file " + sourceFile);
            }
        }

        if (sourceFile.length() == 0) {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sourceFile)));
                bw.write(columnName + lineSep);
                bw.close();
                bw = null;
                this.columnCount = 1;
                return true;
            } catch (FileNotFoundException e) {
                logger.error("Unable to add column" + e.getMessage());
            } catch (IOException e) {
                logger.error("Unable to add column" + e.getMessage());
            }
        }

        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                String column = ((i == 0) ? columnName : defaultValue.toString());
                bw.write((line == null || line.equals("") ? "" : line + getCSVSep()) + column + lineSep);
            }
            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;
            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            columnCount++;
        } catch (Exception e) {
            logger.error("Unable to add column" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Add a col to the dataset
     *
     * @param column The information of the col
     * @return true if sucessfull, false otherwise
     */
    public boolean addColumn(ColumnDefinition column) {
        Object defaultValue;
        if (column.getDefaultValue() == null || column.getDefaultValue().toString().length() == 0) {
            defaultValue = "0";
        } else {
            defaultValue = column.getDefaultValue();
        }

        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        if (!sourceFile.exists()) {
            try {
                sourceFile.createNewFile();
            } catch (IOException e1) {
                logger.error("Unable to create the file " + sourceFile);
            }
        }

        if (sourceFile.length() == 0) {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sourceFile)));
                bw.write(column.getColumnName() + lineSep);
                bw.close();
                bw = null;
                this.columnCount = 1;
                return true;
            } catch (FileNotFoundException e) {
                logger.error("Unable to add column" + e.getMessage());
            } catch (IOException e) {
                logger.error("Unable to add column" + e.getMessage());
            }
        }

        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                String col = ((i == 0) ? column.getColumnName() : defaultValue.toString());
                bw.write((line == null || line.equals("") ? "" : line + getCSVSep()) + col + lineSep);
            }
            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;
            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            columnCount++;
        } catch (Exception e) {
            logger.error("Unable to add column" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Add a columns to the dataset (inserted before the last one)
     *
     * @param columnNames The name of the columns
     * @param defaultValues The default values for the columns included
     * @return true if sucessfull, false otherwise
     */
    public boolean addColumns(String columnNames[], Object defaultValues[]) {
        if (columnNames.length != defaultValues.length) {
            return false;
        }

        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        if (!sourceFile.exists()) {
            try {
                sourceFile.createNewFile();
            } catch (IOException e1) {
                logger.error("Unable to create the file " + sourceFile);
            }
        }

        if (sourceFile.length() == 0) {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sourceFile)));
                String columns = "";
                for (int k = 0; k < columnNames.length; k++) {
                    columns += columnNames[k];
                    columns += (k != columnNames.length - 1) ? getCSVSep() : "";
                }
                bw.write(columns + lineSep);
                bw.close();
                bw = null;

                columnCount += columnNames.length;
                return true;
            } catch (FileNotFoundException e) {
                logger.error("Unable to add column" + e.getMessage());
            } catch (IOException e) {
                logger.error("Unable to add column" + e.getMessage());
            }
        }

        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                String columns = "";
                for (int k = 0; k < columnNames.length; k++) {
                    columns += ((i == 0) ? columnNames[k] : defaultValues[k]);
                    columns += (k != columnNames.length - 1) ? getCSVSep() : "";
                }
                bw.write((line == null || line.equals("") ? "" : line + getCSVSep()) + columns + lineSep);
            }

            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;

            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            columnCount += columnNames.length;
        } catch (Exception e) {
            logger.error("The column could not be inserted " + e.getMessage());
        }

        return true;
    }

    /**
     * Add a columns to the dataset (inserted before the last one)
     *
     * @param columns The information of the columns
     * @return true if sucessfull, false otherwise
     */
    public boolean addColumns(ColumnDefinition[] columns) {
        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        if (!sourceFile.exists()) {
            try {
                sourceFile.createNewFile();
            } catch (IOException e1) {
                logger.error("Unable to create the file " + sourceFile);
            }
        }

        if (sourceFile.length() == 0) {
            try {
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sourceFile)));
                String columnsW = "";
                for (int k = 0; k < columns.length; k++) {
                    columnsW += columns[k].getColumnName();
                    columnsW += (k != columns.length - 1) ? getCSVSep() : "";
                }
                bw.write(columnsW + lineSep);
                bw.close();
                bw = null;

                columnCount += columns.length;
                return true;
            } catch (FileNotFoundException e) {
                logger.error("Unable to add column" + e.getMessage());
            } catch (IOException e) {
                logger.error("Unable to add column" + e.getMessage());
            }
        }

        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                String columnsW = "";
                for (int k = 0; k < columns.length; k++) {
                    columnsW += ((i == 0) ? columns[k].getColumnName() : columns[k].getDefaultValue());
                    columnsW += (k != columns.length - 1) ? getCSVSep() : "";
                }
                bw.write((line == null || line.equals("") ? "" : line + getCSVSep()) + columnsW + lineSep);
            }

            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;

            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            columnCount += columns.length;
        } catch (Exception e) {
            logger.error("The column could not be inserted " + e.getMessage());
        }

        return true;
    }

    /**
     * Insert columns to the dataset (inserted before a certain position)
     *
     * @param columnNames The name of the columns
     * @param defaultValues The default values for the columns included
     * @param position The index where the new columns will be inserted (0 upto
     * the number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnsAt(String columnNames[], Object defaultValues[], int position) {
        if (columnNames.length != defaultValues.length) {
            return false;
        }
        if (columnNames.length == 0) {
            return true;
        }

        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        //System.out.println("Adding "+columnNames.length+" current size: "+getColumnCount()+" first "+columnNames[0]+" last: "+columnNames[columnNames.length-1]);
        if (position > getColumnCount() - 1) {
            return false;
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {

                int whereToInsert = 0;
                if (position == getColumnCount()) {
                    whereToInsert = line.length() - 1;
                } else {
                    for (int k = 0; k < position; k++) {
                        whereToInsert = line.indexOf(getCSVSep(), whereToInsert + 1);
                    }
                }

                if (whereToInsert > 0) {
                    String beforeNewColumn = line.substring(0, whereToInsert);
                    String afterNewColumn = line.substring(whereToInsert + 1);
                    String columns = "";
                    for (int k = 0; k < columnNames.length; k++) {
                        columns += ((i == 0) ? columnNames[k] : defaultValues[k]);
                        columns += (k != columnNames.length - 1) ? getCSVSep() : "";
                    }
                    bw.write(beforeNewColumn + (whereToInsert != 0 ? getCSVSep() : "") + columns + (afterNewColumn.trim().length() == 0 ? "" : (getCSVSep() + afterNewColumn)) + lineSep);
                }
            }

            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;

            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            //columnCount=null;
            columnCount += columnNames.length;
        } catch (Exception e) {
            logger.error("The column could not be inserted " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Insert columns to the dataset (inserted before a certain position)
     *
     * @param columns The information of the columns
     * @param position The index where the new columns will be inserted (0 upto
     * the number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnsAt(ColumnDefinition[] columns, int position) {

        if (columns.length == 0) {
            return true;
        }

        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        //System.out.println("Adding "+columnNames.length+" current size: "+getColumnCount()+" first "+columnNames[0]+" last: "+columnNames[columnNames.length-1]);
        if (position > getColumnCount() - 1) {
            return false;
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {

                int whereToInsert = 0;
                if (position == getColumnCount()) {
                    whereToInsert = line.length() - 1;
                } else {
                    for (int k = 0; k < position; k++) {
                        whereToInsert = line.indexOf(getCSVSep(), whereToInsert + 1);
                    }
                }

                if (whereToInsert > 0) {
                    String beforeNewColumn = line.substring(0, whereToInsert);
                    String afterNewColumn = line.substring(whereToInsert + 1);
                    String columnsW = "";
                    for (int k = 0; k < columns.length; k++) {
                        columnsW += ((i == 0) ? columns[k].getColumnName() : columns[k].getDefaultValue());
                        columnsW += (k != columns.length - 1) ? getCSVSep() : "";
                    }
                    bw.write(beforeNewColumn + (whereToInsert != 0 ? getCSVSep() : "") + columnsW + (afterNewColumn.trim().length() == 0 ? "" : (getCSVSep() + afterNewColumn)) + lineSep);
                }
            }

            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;

            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            //columnCount=null;
            columnCount += columns.length;
        } catch (Exception e) {
            logger.error("The column could not be inserted " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Insert a col to the dataset (inserted before a certain position)
     *
     * @param columnName The name of the col
     * @param defaultValue The default values for the col included
     * @param position The index where the new col will be inserted (0 upto the
     * number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnAt(String columnName, Object defaultValue, int position) {
        if (defaultValue == null || defaultValue.toString().length() == 0) {
            defaultValue = getStrVoidField();
        }

        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        if (position > getColumnCount() - 1) {
            return false;
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                int whereToInsert = 0;
                if (position == getColumnCount()) {
                    whereToInsert = line.length() - 1;
                } else {
                    for (int k = 0; k < position; k++) {
                        whereToInsert = line.indexOf(getCSVSep(), whereToInsert + 1);
                    }
                }

                if (whereToInsert > 0) {
                    String beforeNewColumn = line.substring(0, whereToInsert);
                    String afterNewColumn = line.substring(whereToInsert + 1);
                    String column = ((i == 0) ? columnName : defaultValue.toString());
                    bw.write(beforeNewColumn + (whereToInsert != 0 ? getCSVSep() : "") + column + (afterNewColumn.trim().length() == 0 ? "" : (getCSVSep())) + afterNewColumn + lineSep);
                }
            }

            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;

            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            columnCount++;
        } catch (Exception e) {
            logger.error("The column could not be inserted " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Insert a col to the dataset (inserted before a certain position)
     *
     * @param column The name of the col
     * @param position The index where the new col will be inserted (0 upto the
     * number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnAt(ColumnDefinition column, int position) {
        Object defaultValue = "";
        if (column.getDefaultValue() == null || column.getDefaultValue().toString().length() == 0) {
            defaultValue = getStrVoidField();
        } else {
            defaultValue = column.getDefaultValue();
        }

        flushAndClose();
        if (columnCount == null) {
            getColumnCount();
        }

        if (position > getColumnCount() - 1) {
            return false;
        }

        final String lineSep = System.getProperty("line.separator");

        File sourceFile = csvDataset;
        File destinationFile = new File(csvDataset.getParent(), "copy_" + csvDataset.getName());

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile)));
            int i = 0;
            for (String line = br.readLine(); line != null; line = br.readLine(), i++) {
                int whereToInsert = 0;
                if (position == getColumnCount()) {
                    whereToInsert = line.length() - 1;
                } else {
                    for (int k = 0; k < position; k++) {
                        whereToInsert = line.indexOf(getCSVSep(), whereToInsert + 1);
                    }
                }

                if (whereToInsert > 0) {
                    String beforeNewColumn = line.substring(0, whereToInsert);
                    String afterNewColumn = line.substring(whereToInsert + 1);
                    String col = ((i == 0) ? column.getColumnName() : defaultValue.toString());
                    bw.write(beforeNewColumn + (whereToInsert != 0 ? getCSVSep() : "") + col + (afterNewColumn.trim().length() == 0 ? "" : (getCSVSep())) + afterNewColumn + lineSep);
                }
            }

            bw.flush();
            bw.close();
            br.close();
            br = null;
            bw = null;

            sourceFile.delete();
            destinationFile.renameTo(sourceFile);
            columnCount++;
        } catch (Exception e) {
            logger.error("The column could not be inserted " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Returns the number of columns for the dataset
     *
     * @return the number of the columns for the dataset
     */
    public int getColumnCount() {
        if (columnCount == null) {
            getColumnNames();
        }
        return columnCount;
    }

    /**
     * Returns the colummn names for the CSVDataset
     *
     * @return an array with the col names of the dataset
     */
    public String[] getColumnNames() {
        flushAndClose();

        try {
            if (!csvDataset.exists() || csvDataset.length() == 0) {
                this.columnCount = 0;
                return new String[0];
            }
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csvDataset)));
            String line = br.readLine();
            if (line != null) {
                String[] retVal = line.split(Pattern.quote(getCSVSep()));
                this.columnCount = retVal.length;
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
    public void flushAndClose() {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                logger.error("The Buffered Reader (br) is probably opened but it could not be flushed/closed for further operation");
                System.exit(1);
            }
            br = null;
        }

        if (bw != null) {
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                logger.error("The Buffered Writer (bw) is probably opened but it could not be flushed/closed for further operation");
                System.exit(1);
            }
            bw = null;
        }
    }

}
