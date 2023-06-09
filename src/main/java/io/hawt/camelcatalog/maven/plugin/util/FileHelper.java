package io.hawt.camelcatalog.maven.plugin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileHelper {

    private FileHelper() {
    }

    /**
     * Loads the entire stream into memory as a String and returns it.
     * <p/>
     * <b>Notice:</b> This implementation appends a <tt>\n</tt> as line terminator
     * at the of the text.
     * <p/>
     * Warning, don't use for crazy big streams :)
     */
    public static String loadText(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStreamReader isr = new InputStreamReader(in);
        try {
            BufferedReader reader = new LineNumberReader(isr);
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    builder.append(line);
                    builder.append("\n");
                } else {
                    break;
                }
            }
            return builder.toString();
        } finally {
            isr.close();
            in.close();
        }
    }

    public static String after(String text, String after) {
        if (!text.contains(after)) {
            return null;
        }
        return text.substring(text.indexOf(after) + after.length());
    }

    /**
     * Parses the text as a map (eg key=value)
     * 
     * @param data the data
     * @return the map
     */
    public static Map<String, String> parseAsMap(String data) {
        Map<String, String> answer = new HashMap<String, String>();
        if (data != null) {
            String[] lines = data.split("\n");
            for (String line : lines) {
                int idx = line.indexOf('=');
                if (idx != -1) {
                    String key = line.substring(0, idx);
                    String value = line.substring(idx + 1);
                    // remove ending line break for the values
                    value = value.trim().replaceAll("\n", "");
                    answer.put(key.trim(), value);
                }
            }
        }
        return answer;
    }

    public static void findJsonFiles(File dir, Set<File> found, FileFilter filter) {
        File[] files = dir.listFiles(filter);
        if (files != null) {
            for (File file : files) {
                // skip files in root dirs as Camel does not store information there but others
                // may do
                boolean jsonFile = file.isFile() && file.getName().endsWith(".json");
                if (jsonFile) {
                    found.add(file);
                } else if (file.isDirectory()) {
                    findJsonFiles(file, found, filter);
                }
            }
        }
    }

    public static class JsonFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() || pathname.getName().endsWith(".json");
        }
    }

}
