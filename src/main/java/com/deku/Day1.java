package com.deku;

import static com.deku.Util.printLines;
import static com.deku.Util.readLines;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day1
        implements Puzzle {

    public static final Map<String, String> digitMap = Map.of(
            "one", "1",
            "two", "2",
            "three", "3",
            "four", "4",
            "five", "5",
            "six", "6",
            "seven", "7",
            "eight", "8",
            "nine", "9"
    );

    public static final String PART_ONE_REGEX = "[1-9]";

    public static final String PART_TWO_REGEX = "(one|two|three|four|five|six|seven|eight|nine|[1-9])";

    @Override public void part1() {
        final List<String> strings = readLines("day1.txt");
        final Pattern pattern = Pattern.compile(PART_ONE_REGEX);

        var result = strings.stream()
                .map(line -> {
                    final Matcher match = pattern.matcher(line);
                    if (!match.find()) {
                        throw new IllegalStateException("No number found in line!");
                    }

                    String first = line.substring(match.start(), match.end());

                    String last = "";
                    while (!match.hitEnd()) {
                        last = line.substring(match.start(), match.end());
                        match.find();
                    }

                    return first + last;
                })
                .map(Integer::parseInt)
                .reduce(0, Integer::sum);

        System.out.println(result);
    }

    @Override public void part2() {
        final List<String> strings = readLines("day1.txt");
        final Pattern pattern = Pattern.compile(PART_TWO_REGEX);

        var result = strings.stream()
                .map(line -> {
                    final Matcher match = pattern.matcher(line);
                    if (!match.find()) {
                        throw new IllegalStateException("No number found in line!");
                    }

                    String first = line.substring(match.start(), match.end());

                    String last = "";

                    while (!match.hitEnd()) {
                        last = line.substring(match.start(), match.end());

                        // Numbers can overlap - e.g. eighthree. In that case, the last number "wins", so we need
                        // to do a slight overlap in our searching to account for this. But in case we matched a single
                        // digit last time, don't do it then, or we'll infinite loop.
                        if (match.end() - match.start() > 1) {
                            match.find(match.end() - 1);
                        }
                        else {
                            match.find();
                        }
                    }

                    if (first.length() != 1) {
                        first = digitMap.get(first);
                    }

                    if (last.length() != 1) {
                        last = digitMap.get(last);
                    }

                    return first + last;
                })
                .map(Integer::parseInt)
                .reduce(0, Integer::sum);

        System.out.println(result);
    }

}
