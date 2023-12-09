package com.deku;

import static com.deku.Util.readLines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day9
        implements Puzzle {

    private record Histories(List<List<Integer>> values) {
    }

    @Override public void part1() {
        var histories = parseInput(readLines("day9.txt"));

        histories.forEach(history -> {
            buildHistory(history);

            // Walk up the histories and extrapolate the final values of each.
            for (int i = history.values.size() - 1; i >= 0; i--) {
                final List<Integer> line = history.values.get(i);
                if (i == history.values.size() - 1) {
                    line.add(0);
                }
                else {
                    final List<Integer> lineBelow = history.values.get(i + 1);
                    line.add(line.get(line.size() - 1) + lineBelow.get(lineBelow.size() - 1));
                }
            }
        });

        final Integer result = histories.stream()
                .map(history -> history.values.get(0).get(history.values.get(0).size() - 1))
                .reduce(0, Integer::sum);

        System.out.println(result);
    }

    @Override public void part2() {
        var histories = parseInput(readLines("day9.txt"));

        histories.forEach(history -> {
            buildHistory(history);

            // Walk up the histories and extrapolate the first values of each.
            for (int i = history.values.size() - 1; i >= 0; i--) {
                final List<Integer> line = history.values.get(i);
                if (i == history.values.size() - 1) {
                    line.add(0);
                }
                else {
                    final List<Integer> lineBelow = history.values.get(i + 1);
                    line.add(0, line.get(0) - lineBelow.get(0));
                }
            }
        });

        final Integer result = histories.stream()
                .map(history -> history.values.get(0).get(0))
                .reduce(0, Integer::sum);

        System.out.println(result);
    }

    private static void buildHistory(Histories history) {
        while (!history.values.get(history.values.size() - 1).stream().allMatch(num -> num == 0)) {
            history.values.add(extrapolateHistory(history.values.get(history.values.size() - 1)));
        }
    }

    private static List<Integer> extrapolateHistory(List<Integer> list) {
        return IntStream.range(1, list.size())
                .mapToObj(i -> list.get(i) - list.get(i - 1))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Histories> parseInput(List<String> lines) {
        return lines.stream()
                .map(line -> {
                    var list = new ArrayList<List<Integer>>();
                    list.add(Arrays.stream(line.split(" "))
                            .map(Integer::parseInt)
                            .collect(Collectors.toCollection(ArrayList::new)));
                    return new Histories(list);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
