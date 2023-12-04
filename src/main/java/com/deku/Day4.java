package com.deku;

import static com.deku.Util.readLines;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4
        implements Puzzle {

    private record Card(int id, Set<Integer> winningNumbers, Set<Integer> cardNumbers) {
    }

    private static final String CARD_REGEX = "^Card\\s+(\\d+): ([\\s\\d]+)\\|([\\s\\d]+)$";

    @Override public void part1() {
        var cards = parseInput(readLines("day4.txt"));

        // map to score
        final Double totalScore = cards.stream()
                .map(card -> card.cardNumbers.stream()
                        .filter(card.winningNumbers::contains)
                        .count())
                .map(numWins -> numWins > 0 ? Math.pow(2, numWins - 1.0) : 0)
                .reduce(0.0, Double::sum);

        System.out.println(totalScore);
    }

    @Override public void part2() {
        var cards = parseInput(readLines("day4.txt"));

        var numCardsMap = cards.stream().collect(Collectors.toMap(key -> key.id, value -> 1));

        cards.forEach(card -> {
            final int numWins = (int) card.cardNumbers.stream()
                    .filter(card.winningNumbers::contains)
                    .count();

            IntStream.range(card.id + 1, card.id + 1 + numWins)
                    .forEach(num -> numCardsMap.computeIfPresent(num,
                            (key, oldValue) -> oldValue + numCardsMap.get(card.id)));
        });

        System.out.println(numCardsMap.values().stream().reduce(0, Integer::sum));
    }

    private List<Card> parseInput(List<String> lines) {
        final Pattern regex = Pattern.compile(CARD_REGEX);

        return lines.stream().map(line -> {
            final Matcher matcher = regex.matcher(line);
            if (!matcher.find()) {
                throw new IllegalStateException("Could not parse " + line);
            }

            var id = Integer.parseInt(matcher.group(1));

            var winningNumbers = Arrays.stream(matcher.group(2).split(" "))
                    .filter(str -> !str.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toUnmodifiableSet());

            var cardNumbers = Arrays.stream(matcher.group(3).split(" "))
                    .filter(str -> !str.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toUnmodifiableSet());

            return new Card(id, winningNumbers, cardNumbers);
        }).toList();
    }

}
