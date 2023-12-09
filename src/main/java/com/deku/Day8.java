package com.deku;

import static com.deku.Util.Pair;
import static com.deku.Util.readLines;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8
        implements Puzzle {

    private record Network(Map<String, Pair<String, String>> paths, char[] instructions) {
    }

    private final Pattern pathRegex = Pattern.compile("([1-9A-Z]{3}) = \\(([1-9A-Z]{3}), ([1-9A-Z]{3})\\)");

    @Override public void part1() {
        final Network network = parseInput(readLines("day8.txt"));

        final int i = findExit(network, "AAA");
        System.out.println(i);
    }


    @Override public void part2() {
        final Network network = parseInput(readLines("day8.txt"));

        final List<String> startingNodes = network.paths.keySet().stream().filter(node -> node.endsWith("A")).toList();

        final long[] list = startingNodes.stream().mapToLong(startingNode -> findExit(network, startingNode)).toArray();

        System.out.println(lcm(list));
    }

    private static int findExit(Network network, final String start) {
        String curNode = start;
        int i = 0;
        for(; !curNode.endsWith("Z"); i++) {
            char instruction =  network.instructions[i % network.instructions.length];

            curNode = switch (instruction) {
                case 'L' -> network.paths.get(curNode).getLeft();
                case 'R'-> network.paths.get(curNode).getRight();
                default -> throw new IllegalStateException("Unexpected instruction: " + instruction);
            };

        }
        return i;
    }

    // Stolen from Google because of course I stole it from Google.
    private static long lcm(long... numbers) {
        return Arrays.stream(numbers).reduce(1, (x, y) -> x * (y / gcd(x, y)));
    }

    private static long gcd(long a, long b) {
        return (b == 0) ? a : gcd(b, a % b);
    }

    private Network parseInput(List<String> lines) {
        var instructions = lines.get(0).toCharArray();

        final Map<String, Pair<String, String>> map = lines.stream()
                .skip(2)
                .map(line -> pathRegex.matcher(line).results().findFirst().orElseThrow())
                .collect(Collectors.toMap((result -> result.group(1)), result ->
                        new Pair<>(result.group(2), result.group(3))));

        return new Network(map, instructions);
    }

}
