package com.example.windows10.work_weather;



import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

class CSVWriter {

    private PrintWriter printWriter;
    private char separator;
    private char quoteCharacter;
    private char escapeCharacter;
    private String lineEnd;

    private static final char DEFAULT_ESCAPE_CHARACTER = '"';
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE_CHARACTER = '"';
    private static final char NO_QUOTE_CHARACTER = '\u0000';
    private static final char NO_ESCAPE_CHARACTER = '\u0000';
    private static final String DEFAULT_LINE_END = "\n";

    CSVWriter(Writer writer) {
        this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
    }

    private CSVWriter(Writer writer, char separator, char quoteCharacter, char escapeCharacter, String lineEnd) {
        this.printWriter = new PrintWriter(writer);
        this.separator = separator;
        this.quoteCharacter = quoteCharacter;
        this.escapeCharacter = escapeCharacter;
        this.lineEnd = lineEnd;
    }

    void writeNext(String[] nextLine) {

        if (nextLine == null)
            return;

        StringBuilder stringbuilder = new StringBuilder();
        for (int i = 0; i < nextLine.length; i++) {
            if (i != 0) {
                stringbuilder.append(separator);
            }
            String nextElement = nextLine[i];
            if (nextElement == null)
                continue;
            if (quoteCharacter !=  NO_QUOTE_CHARACTER)
                stringbuilder.append(quoteCharacter);
            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);
                if (escapeCharacter != NO_ESCAPE_CHARACTER && nextChar == quoteCharacter) {
                    stringbuilder.append(escapeCharacter).append(nextChar);
                } else if (escapeCharacter != NO_ESCAPE_CHARACTER && nextChar == escapeCharacter) {
                    stringbuilder.append(escapeCharacter).append(nextChar);
                } else {
                    stringbuilder.append(nextChar);
                }
            }
            if (quoteCharacter != NO_QUOTE_CHARACTER)
                stringbuilder.append(quoteCharacter);
        }
        stringbuilder.append(lineEnd);
        printWriter.write(stringbuilder.toString());
    }

    void close() throws IOException {
        printWriter.flush();
        printWriter.close();
    }

}