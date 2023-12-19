package com.deku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public class Util {

    public static final List<Pair<Integer, Integer>> DIRECTIONS = Arrays.asList(
            new Pair<>(1, 0),
            new Pair<>(-1, 0),
            new Pair<>(0, 1),
            new Pair<>(0, -1));

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

    public static <T> List<Pair<Integer, Integer>> dfs(final List<List<T>> map,
                                                       final Pair<Integer, Integer> point,
                                                       final BiPredicate<Pair<Integer, Integer>, Pair<Integer, Integer>> validConnection) {
        return dfsIterative(map, point, validConnection);
    }

    private static <T> List<Pair<Integer, Integer>> dfsIterative(final List<List<T>> map,
                                                                 final Pair<Integer, Integer> startPoint,
                                                                 final BiPredicate<Pair<Integer, Integer>, Pair<Integer, Integer>> validConnection) {
        final Set<Pair<Integer, Integer>> visited = new HashSet<>();
        final List<Pair<Integer, Integer>> path = new ArrayList<>();

        final Deque<Pair<Integer, Integer>> toTraverse = new ArrayDeque<>();
        toTraverse.push(startPoint);

        Pair<Integer, Integer> curPoint;
        while (!toTraverse.isEmpty()) {
            curPoint = toTraverse.pop();

            visited.add(curPoint);
            path.add(curPoint);


            for (Pair<Integer, Integer> dir : DIRECTIONS) {
                var newRow = curPoint.left() + dir.left();
                var newCol = curPoint.right() + dir.right();

                final Pair<Integer, Integer> nextPoint = new Pair<>(newRow, newCol);

                if (!visited.contains(nextPoint) && checkBounds(map, newRow, newCol) && validConnection.test(curPoint,
                        nextPoint)) {
                    toTraverse.push(nextPoint);
                }
            }
        }

        return path;
    }

    private static <T> List<Pair<Integer, Integer>> dfsRecursive(final List<List<T>> map,
                                                                 final Pair<Integer, Integer> point,
                                                                 final BiPredicate<Pair<Integer, Integer>, Pair<Integer, Integer>> validConnection,
                                                                 final Set<Pair<Integer, Integer>> visited,
                                                                 final List<Pair<Integer, Integer>> path) {
        var row = point.left();
        var col = point.right();

        visited.add(point);
        path.add(point);

        for (Pair<Integer, Integer> dir : DIRECTIONS) {
            var newRow = row + dir.left();
            var newCol = col + dir.right();

            final Pair<Integer, Integer> nextPoint = new Pair<>(newRow, newCol);
            if (!visited.contains(nextPoint) && checkBounds(map, newRow, newCol) && validConnection.test(point,
                    nextPoint)) {
                dfsRecursive(map, nextPoint, validConnection, visited, path);
            }
        }

        return path;
    }

    public static <T> boolean checkBounds(final List<List<T>> map, int newRow, int newCol) {
        return newRow >= 0 && newRow < map.size() && newCol >= 0 && newCol < map.get(newRow).size();
    }

    public static void printList(List<?> lines) {
        lines.forEach(System.out::print);
        System.out.println();
    }

    public static <T> void printTable(List<List<T>> table) {
        table.forEach(line -> {
            printList(line);
            System.out.println();
        });
    }
}
