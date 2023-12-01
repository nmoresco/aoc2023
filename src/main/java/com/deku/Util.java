package com.deku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {

    public static List<String> readLines(final String file) {
        try {
            URL url = Util.class.getResource('/' + file);
            if (url == null) {
                throw new FileNotFoundException();
            }

            try (var reader = new FileReader(url.getPath())) {
                var br = new BufferedReader(reader);

                var lines = new ArrayList<String>();
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }

                return lines;
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Could not find file " + file);
        }
        catch (IOException e) {
            System.err.println("Could not parse file " + file);
        }

        return Collections.emptyList();
    }

    public static void printLines(List<?> lines) {
        lines.forEach(System.out::println);
    }
}
