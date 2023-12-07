package com.deku;

import static com.deku.Util.readLines;
import static java.util.Map.entry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day7
        implements Puzzle {

    static final int FIVE_OF_A_KIND = 6;
    static final int FOUR_OF_A_KIND = 5;
    static final int FULL_HOUSE = 4;
    static final int THREE_OF_A_KIND = 3;
    static final int TWO_PAIR = 2;
    static final int PAIR = 1;
    static final int HIGH_CARD = 0;

    static final Map<Character, Integer> cardMap = Map.ofEntries(
            entry('1', 1),
            entry('2', 2),
            entry('3', 3),
            entry('4', 4),
            entry('5', 5),
            entry('6', 6),
            entry('7', 7),
            entry('8', 8),
            entry('9', 9),
            entry('T', 10),
            entry('J', 11),
            entry('Q', 12),
            entry('K', 13),
            entry('A', 14)
    );

    private record Hand(int[] cards, int type, int bid, boolean jacksAreWild)
            implements Comparable<Hand> {

        @Override public int compareTo(Hand o) {
            final int typeDifference = this.type - o.type;

            if (typeDifference != 0) {
                return typeDifference;
            }

            for (int i = 0; i < 5; i++) {
                var myCard = this.cards[i];
                var theirCard = o.cards[i];

                if (myCard == 11 && this.jacksAreWild) {
                    myCard = 0;
                }

                if (theirCard == 11 && o.jacksAreWild) {
                    theirCard = 0;
                }

                if (myCard > theirCard) {
                    return 1;
                }
                else if (theirCard > myCard) {
                    return -1;
                }
            }

            // They're exactly equal.
            return 0;
        }
    }

    @Override public void part1() {
        final List<Hand> hands = parseInput(readLines("day7.txt"), false);
        final int[] array = hands.stream().sorted().mapToInt(hand -> hand.bid).toArray();

        final int result = IntStream.range(0, array.length)
                .map(i -> array[i] * (i + 1))
                .reduce(0, Integer::sum);

        System.out.println(result);
    }

    @Override public void part2() {
        final List<Hand> hands = parseInput(readLines("day7.txt"), true);
        final int[] array = hands.stream().sorted().mapToInt(hand -> hand.bid).toArray();

        final int result = IntStream.range(0, array.length)
                .map(i -> array[i] * (i + 1))
                .reduce(0, Integer::sum);

        System.out.println(result);
    }

    private List<Hand> parseInput(List<String> lines, boolean jacksAreWild) {
        return lines.stream().map(line -> {
            final String[] round = line.split(" ");
            final int[] hand = round[0].chars().map(card -> cardMap.get((char) card)).toArray();
            return new Hand(hand, jacksAreWild ? getHandType(hand, 11) : getHandType(hand, -1),
                    Integer.parseInt(round[1]), jacksAreWild);
        }).toList();
    }

    static int getHandType(int[] cards, int wildCard) {
        final int[] nonWildCards = Arrays.stream(cards).filter(card -> card != wildCard).toArray();
        final int numJokers = cards.length - nonWildCards.length;
        final int[] distinct = Arrays.stream(nonWildCards).distinct().toArray();
        final int countDistinct = distinct.length;

        // countDistinct is only less than 1 if we have all jokers
        if (countDistinct <= 1) {
            return FIVE_OF_A_KIND;
        }
        else if (countDistinct == 2) {
            // Either four of a kind or full house
            var countOfEachCard = Arrays.stream(distinct)
                    .map(card -> (int) Arrays.stream(cards).filter(a -> a == card).count() + numJokers)
                    .sorted()
                    .toArray();

            if (countOfEachCard[1] == 4) {
                return FOUR_OF_A_KIND;
            }
            else if (countOfEachCard[1] == 3) {
                return FULL_HOUSE;
            }
            else if (countOfEachCard[1] == 2) {
                return THREE_OF_A_KIND;
            }
            else {
                throw new IllegalArgumentException("Couldn't figure out hand type!");
            }
        }
        else if (countDistinct == 3) {
            // Either two pair or three of a kind
            var countOfEachCard = Arrays.stream(distinct)
                    .map(card -> (int) Arrays.stream(cards).filter(a -> a == card).count() + numJokers)
                    .sorted()
                    .toArray();

            if (countOfEachCard[2] == 3) {
                return THREE_OF_A_KIND;
            }
            else if (countOfEachCard[2] == 2) {
                return TWO_PAIR;
            }
            else {
                throw new IllegalArgumentException("Couldn't figure out hand type!");
            }
        }
        else if (countDistinct == 4) {
            return PAIR;
        }
        else if (countDistinct == 5) {
            return HIGH_CARD;
        }
        else {
            throw new IllegalArgumentException("Not a valid 5 card hand!");
        }
    }
}
