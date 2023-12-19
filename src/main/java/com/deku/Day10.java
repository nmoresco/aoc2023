package com.deku;

import static com.deku.Util.dfs;
import static com.deku.Util.readLines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day10
        implements Puzzle {

    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String[] colors = { ANSI_BLACK, ANSI_RED, ANSI_GREEN, ANSI_YELLOW, ANSI_BLUE, ANSI_PURPLE,
            ANSI_CYAN, ANSI_WHITE };

    @Override public void part1() {
        final Graph graph = parseInput(readLines("day10.txt"), false);

        final List<Pair<Integer, Integer>> path = dfs(graph.tiles, graph.start,
                (current, next) -> testPipeConnection(current, next, graph));

        // Fun to visualize
        for (int i = 0; i < graph.tiles.size(); i++) {
            for (int j = 0; j < graph.tiles.get(i).size(); j++) {
                if (((Collection<Pair<Integer, Integer>>) path).contains(new Pair<>(i, j))) {
                    final char tile = getTile(graph, new Pair<>(i, j));
                    System.out.format("%s", ANSI_RED + tile + ANSI_RESET);
                }
                else {
                    System.out.format("%s", getTile(graph, new Pair<>(i, j)));
                }
            }
            System.out.println();
        }

        System.out.println("\n" + (path.size() / 2));
    }

    private boolean testPipeConnection(Pair<Integer, Integer> current, Pair<Integer, Integer> next, Graph graph) {
        if (getTile(graph, next) == '.') {
            return false;
        }

        // Assumes diagonals aren't a thing.
        return switch (getTile(graph, current)) {
            case '║' -> current.right().equals(next.right()) && Math.abs(current.left() - next.left()) == 1;

            case '═' -> current.left().equals(next.left()) && Math.abs(current.right() - next.right()) == 1;

            case '╚' -> current.left().equals(next.left() + 1) || current.right().equals(next.right() - 1);

            case '╝' -> current.left().equals(next.left() + 1) || current.right().equals(next.right() + 1);

            case '╗' -> current.left().equals(next.left() - 1) || current.right().equals(next.right() + 1);

            case '╔' -> current.left().equals(next.left() - 1) || current.right().equals(next.right() - 1);
            // We don't know what S is, but it must form a valid connection so let's check if it does.
            case 'S' -> switch (getTile(graph, next)) {
                case '║' -> current.right().equals(next.right()) && Math.abs(current.left() - next.left()) == 1;

                case '═' -> current.left().equals(next.left()) && Math.abs(current.right() - next.right()) == 1;

                case '╚' -> current.left().equals(next.left() - 1) || current.right().equals(next.right() + 1);

                case '╝' -> current.left().equals(next.left() - 1) || current.right().equals(next.right() - 1);

                case '╗' -> current.left().equals(next.left() + 1) || current.right().equals(next.right() - 1);

                case '╔' -> current.left().equals(next.left() + 1) || current.right().equals(next.right() + 1);

                default -> false;
            };
            default -> false;
        };
    }

    @Override public void part2() {
        final Graph graph = parseInput(readLines("day10.txt"), true);

        final List<Pair<Integer, Integer>> path = dfs(graph.tiles, graph.start,
                (current, next) -> testPipeConnection(current, next, graph));

        // Yes, our "colors" are just numbers. All colors are numbers in a computer anyway.
        int curColor = 1;
        // Our loop is the first color.
        Map<Pair<Integer, Integer>, Integer> colorMap = new HashMap<>();
        path.forEach(pair -> colorMap.put(pair, 0));

        // Flood fill each pixel
        for (int i = 0; i < graph.tiles.size(); i++) {
            for (int j = 0; j < graph.tiles.get(i).size(); j++) {
                final Pair<Integer, Integer> curTile = new Pair<>(i, j);

                if (!colorMap.containsKey(curTile)) {
                    final List<Pair<Integer, Integer>> tilesFlooded = dfs(graph.tiles, curTile,
                            (current, next) -> !colorMap.containsKey(next)
                                    || graph.tiles.get(next.left()).get(next.right()) == '?');

                    for (Pair<Integer, Integer> pair : tilesFlooded) {
                        colorMap.put(pair, curColor);
                    }

                    curColor++;
                }
            }
        }

        // Fun to visualize
        for (int i = 0; i < graph.tiles.size(); i++) {
            for (int j = 0; j < graph.tiles.get(i).size(); j++) {
                final Pair<Integer, Integer> pair = new Pair<>(i, j);
                final char tile = graph.tiles.get(pair.left()).get(pair.right());
                System.out.format("%s", colors[colorMap.getOrDefault(new Pair<>(i, j), 0) % 7] + tile + ANSI_RESET);
            }
            System.out.println();
        }

        final List<List<Character>> smallerTiles = shrinkInput(graph.tiles);

        // In my solution, the inner tiles are colored with 2. So we just count those.
        final long count = IntStream.range(0, smallerTiles.size()).flatMap(i ->
                IntStream.range(0, smallerTiles.get(i).size())
                        .map(j -> colorMap.get(new Pair<>(i * 3, j * 3)))
                        .filter(color -> color == 2)).count();

        System.out.println(count);
    }

    private char getTile(final Graph graph, final Pair<Integer, Integer> pair) {
        return graph.tiles.get(pair.left()).get(pair.right());
    }

    private Graph parseInput(List<String> lines, boolean expand) {
        Pair<Integer, Integer> start = null;

        List<List<Character>> tiles = lines.stream()
                .map(str -> str.chars().mapToObj(this::convertChar).toList())
                .toList();

        if (expand) {
            tiles = expandInput(tiles);
        }

        for (int i = 0; i < tiles.size(); i++) {
            for (int j = 0; j < tiles.get(i).size(); j++) {
                if (tiles.get(i).get(j) == 'S') {
                    start = new Pair<>(i, j);
                }
            }
        }

        return new Graph(tiles, start);
    }

    /**
     * Expands the input graph to be 3x as big, so we can properly squeeze between the pipes.
     */
    private List<List<Character>> expandInput(List<List<Character>> input) {
        List<List<Character>> bigger = new ArrayList<>(input.size() * 3);

        for (int i = 0; i < input.size(); i++) {
            bigger.add(new ArrayList<>(input.get(i).size() * 3));
            bigger.add(new ArrayList<>(input.get(i).size() * 3));
            bigger.add(new ArrayList<>(input.get(i).size() * 3));
            for (int j = 0; j < input.get(i).size(); j++) {
                // First row we just continue horizontal walls
                bigger.get(i * 3).add(input.get(i).get(j));
                bigger.get(i * 3).add(switch (input.get(i).get(j)) {
                    case '║', '╝', '╗', '.' -> '.';
                    // Note my puzzle works with treating S as '═' but that's not universal
                    case '═', '╚', '╔', 'S' -> '═';

                    default -> '?';
                });
                bigger.get(i * 3).add(switch (input.get(i).get(j)) {
                    case '║', '╝', '╗', '.' -> '.';

                    case '═', '╚', '╔', 'S' -> '═';

                    default -> '?';
                });

                // Second row we have to continue vertical ones if they exist
                bigger.get(i * 3 + 1).add(switch (input.get(i).get(j)) {
                    case '╝', 'S', '.', '═', '╚' -> '.';
                    case '╗', '║', '╔' -> '║';
                    default -> '?';
                });
                bigger.get(i * 3 + 1).add('.');
                bigger.get(i * 3 + 1).add('.');

                // Third row we have to continue vertical ones if they exist
                bigger.get(i * 3 + 2).add(switch (input.get(i).get(j)) {
                    case '╝', 'S', '.', '═', '╚' -> '.';
                    case '╗', '║', '╔' -> '║';
                    default -> '?';
                });
                bigger.get(i * 3 + 2).add('.');
                bigger.get(i * 3 + 2).add('.');
            }
        }

        return bigger;
    }

    /**
     * Shrink the input back to our original size by taking every third character.
     */
    private List<List<Character>> shrinkInput(List<List<Character>> input) {
        List<List<Character>> smaller = new ArrayList<>(input.size() / 3);

        for (int i = 0; i < input.size(); i += 3) {
            smaller.add(new ArrayList<>(input.get(i).size() / 3));
            for (int j = 0; j < input.get(i).size(); j += 3) {
                smaller.get(i / 3).add(input.get(i).get(j));
            }
        }

        return smaller;
    }

    // These letters are silly. We can do better.
    private char convertChar(int ch) {
        return switch (ch) {
            case '|' -> '║';

            case '-' -> '═';

            case 'L' -> '╚';

            case 'J' -> '╝';

            case '7' -> '╗';

            case 'F' -> '╔';

            case 'S' -> 'S';

            case '.' -> '.';

            default -> throw new IllegalStateException("Unexpected character: " + ch);
        };
    }

    private record Graph(List<List<Character>> tiles, Pair<Integer, Integer> start) {
    }
}
