package com.deku;

import static com.deku.Util.readLines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day3
        implements Puzzle {

    private record Schematic(List<List<Integer>> engine, Map<Integer, Integer> charMap) {
    }

    private int enginePlaceholderCode = 1;

    @Override public void part1() {
        var schematic = parseInput(readLines("day3.txt"));
        var engine = schematic.engine;

        final Set<Integer> engineParts = new HashSet<>();
        for (int i = 0; i < engine.size(); i++) {
            for (int j = 0; j < engine.get(i).size(); j++) {
                final Integer curValue = engine.get(i).get(j);
                if (curValue > 0 && !engineParts.contains(curValue) && checkDiagonalsForSymbol(engine, j, i)) {
                    engineParts.add(curValue);
                }
            }
        }

        System.out.println(engineParts.stream().map(schematic.charMap::get).reduce(0, Integer::sum));
    }

    @Override public void part2() {
        var schematic = parseInput(readLines("day3.txt"));
        var engine = schematic.engine;

        int ratios = 0;
        for (int i = 0; i < engine.size(); i++) {
            for (int j = 0; j < engine.get(i).size(); j++) {
                if (engine.get(i).get(j) == -1) {
                    ratios += checkGears(engine, schematic.charMap, j, i);
                }
            }
        }

        System.out.println(ratios);
    }

    private boolean checkDiagonalsForSymbol(List<List<Integer>> schematic, int x, int y) {
        // Assumes each line is the same size
        int startX = Math.min(schematic.size(), Math.max(0, x - 1));
        int startY = Math.min(schematic.get(x).size(), Math.max(0, y - 1));

        int endX = Math.min(schematic.size(), Math.max(0, x + 2));
        int endY = Math.min(schematic.get(x).size(), Math.max(0, y + 2));

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                // If we find a symbol orthogonal, return true
                if (schematic.get(j).get(i) < 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private int checkGears(List<List<Integer>> schematic, Map<Integer, Integer> charMap, int x, int y) {
        int startX = Math.min(schematic.size(), Math.max(0, x - 1));
        int startY = Math.min(schematic.get(x).size(), Math.max(0, y - 1));

        int endX = Math.min(schematic.size(), Math.max(0, x + 2));
        int endY = Math.min(schematic.get(x).size(), Math.max(0, y + 2));

        var orthogonalNumbers = new HashSet<Integer>(2);
        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                final int curValue = schematic.get(j).get(i);
                if (curValue > 0) {
                    orthogonalNumbers.add(curValue);
                }
            }
        }

        return orthogonalNumbers.size() == 2 ?
                orthogonalNumbers.stream().map(charMap::get).reduce(1, (gear1, gear2) -> gear1 * gear2) : 0;
    }

    /**
     * Scan each line for contiguous numbers. Each digit of the number gets mapped to a unique code to differentiate them.
     */
    private Schematic parseInput(List<String> lines) {

        var charMap = new HashMap<Integer, Integer>();

        final List<List<Integer>> engine = lines.stream().map(line -> {
            List<Integer> parsedLine = new ArrayList<>(Collections.nCopies(line.length(), 0));

            for (int i = 0; i < line.length(); i++) {
                final char curChar = line.charAt(i);

                if (curChar == '.') {
                    // 0 is a period
                    parsedLine.set(i, 0);
                }

                else if (curChar == '*') {
                    // -1 is a gear
                    parsedLine.set(i, -1);
                }

                else if (isDigit(curChar)) {
                    // Parse the rest of the number
                    final int numberLength = parseNumber(line, i, parsedLine, charMap);
                    // skip ahead so we don't double-parse this number
                    i = numberLength - 1;
                }

                else {
                    // Normalize all other symbols as -2 for my own sanity.
                    parsedLine.set(i, -2);
                }
            }

            return parsedLine;
        }).toList();

        return new Schematic(engine, charMap);
    }

    private int parseNumber(String line, int i, List<Integer> parsedLine, HashMap<Integer, Integer> charMap) {
        final StringBuilder number = new StringBuilder();

        int lookAhead = i;
        while (lookAhead < line.length() && isDigit(line.charAt(lookAhead))) {
            number.append(line.charAt(lookAhead));
            lookAhead++;
        }

        // Every index that is a digit gets a unique id which will map to a number.
        for (int j = i; j < lookAhead; j++) {
            parsedLine.set(j, enginePlaceholderCode);
        }
        charMap.put(enginePlaceholderCode, Integer.parseInt(number.toString()));
        enginePlaceholderCode++;

        return lookAhead;
    }

    private static boolean isDigit(Character curChar) {
        return (curChar > 47 && curChar < 58);
    }
}
