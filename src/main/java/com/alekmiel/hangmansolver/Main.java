package com.alekmiel.hangmansolver;

import com.beust.jcommander.JCommander;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Aleksander on 15.07.2016.
 */
public class Main {

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        new JCommander(arguments, args);

        List<String> patterns = arguments.getPatterns().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        Table<String, String, List<String>> matches = HashBasedTable.create(arguments.getDictionaries().size(), patterns.size());

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (File dictionary : arguments.getDictionaries()) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dictionary)))) {
                bufferedReader.lines()
                        .map(String::toLowerCase)
                        .forEach(target -> patterns.stream()
                                .filter(pattern -> checkIfMatch(pattern, target))
                                .forEach(pattern -> {
                                    if (matches.get(pattern, dictionary.getAbsolutePath()) == null) {
                                        matches.put(pattern, dictionary.getAbsolutePath(), new ArrayList<>());
                                    }
                                    matches.get(pattern, dictionary.getAbsolutePath()).add(target);
                                }));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long time = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        printResult(arguments.getDictionaries(), patterns, matches, time);
    }

    private static boolean checkIfMatch(String pattern, String target) {
        if (pattern.length() != target.length()) {
            return false;
        }
        boolean match = true;
        for (int i = 0; i < pattern.length(); i++) {
            char patternChar = pattern.charAt(i);
            char targetChar = target.charAt(i);
            if (patternChar != '_') {
                if (patternChar != targetChar) {
                    match = false;
                    break;
                }
            }
        }
        return match;
    }

    private static void printResult(List<File> dictionaries, List<String> patterns, Table<String, String, List<String>> matches, long time) {
        org.nocrala.tools.texttablefmt.Table table = new org.nocrala.tools.texttablefmt.Table(matches.rowKeySet().size() + 1, BorderStyle.CLASSIC, ShownBorders.ALL);
        List<String> dictionariesNames = dictionaries.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());

        table.addCell("dict/patterns", new CellStyle());
        patterns.forEach(table::addCell);

        dictionariesNames.stream()
                .peek(table::addCell)
                .forEach(dictionaryName -> patterns.forEach(pattern -> {
                    List<String> targets = matches.get(pattern, dictionaryName);
                    if (targets == null) {
                        table.addCell("");
                    } else {
                        table.addCell(targets.stream().collect(Collectors.joining(", ")));
                    }
                }));

        System.out.println(table.render());
        System.out.println("time: " + time + " [ms]");
    }

}
