package com.deku;

import static com.deku.Util.readLines;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2
        implements Puzzle {

    private record Game(int id, List<Round> rounds) {}
    private record Round(int red, int green, int blue) {}

    private static final String GAME_REGEX = "^Game (\\d+): (.+)$";
    private static final String DICE_REGEX = "(\\d+) ([a-z]+)";

    @Override public void part1() {
        final List<Game> games = parseInput(readLines("day2.txt"));

        var result = games.stream().filter(game ->
                game.rounds.stream().allMatch(round -> round.red <= 12 && round.green <= 13 && round.blue <= 14))
                .map(game -> game.id)
                .reduce(0, Integer::sum);

        System.out.println(result);
    }

    @Override public void part2() {
        final List<Game> games = parseInput(readLines("day2.txt"));

        var result = games.stream().map(game -> {
            var minRed = game.rounds.stream().map(round -> round.red).max(Integer::compare);
            var minGreen = game.rounds.stream().map(round -> round.green).max(Integer::compare);
            var minBlue = game.rounds.stream().map(round -> round.blue).max(Integer::compare);

            return minRed.orElse(0) * minGreen.orElse(0) * minBlue.orElse(0);
        }).reduce(0, Integer::sum);

        System.out.println(result);
    }

    private List<Game> parseInput(final List<String> lines) {
        final Pattern regex = Pattern.compile(GAME_REGEX);
        return lines.stream().map(line -> {
            final Matcher matcher = regex.matcher(line);

            if (!matcher.find()) {
                throw new IllegalStateException("Could not parse " + line);
            }

            // first capture group is the whole input;
            var id = matcher.group(1);
            var rounds = parseRounds(matcher.group(2));

            return new Game(Integer.parseInt(id), rounds);
        }).toList();
    }

    private List<Round> parseRounds(final String roundsStr) {
        final Pattern regex = Pattern.compile(DICE_REGEX);

        return Arrays.stream(roundsStr.split(";")).map(roundStr -> {
            int red = 0;
            int green = 0;
            int blue = 0;
            for (String diceStr : roundStr.split(",")) {
                final Matcher matcher = regex.matcher(diceStr.trim());

                if (!matcher.find()) {
                    throw new IllegalStateException("Could not parse " + diceStr);
                }

                var num = Integer.parseInt(matcher.group(1));
                switch (matcher.group(2)) {
                case "red":
                    red = num;
                    break;
                case "green":
                    green = num;
                    break;
                case "blue":
                    blue = num;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + matcher.group(2));
                }
            }

            return new Round(red, green, blue);
        }).toList();
    }
}
