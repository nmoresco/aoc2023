package com.deku;

import static com.deku.Util.readLines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day6
        implements Puzzle {

    record Race(long time, long recordDistance) { }

    @Override public void part1() {
        var races = parseInputP1(readLines("day6.txt"));

        final long results = races.stream()
                .map(race -> LongStream.range(1, race.time - 1)
                        .mapToObj(speed -> (race.time - speed) * speed)
                        .filter(distance -> distance > race.recordDistance)
                        .count())
                .reduce(1L, (a, b) -> a * b);

        System.out.println(results);
    }

    @Override public void part2() {
        var race = parseInputP2(readLines("day6.txt"));

        final long results = LongStream.range(1, race.time - 1)
                .mapToObj(speed -> (race.time - speed) * speed)
                .filter(distance -> distance > race.recordDistance)
                .count();

        System.out.println(results);
    }

    private List<Race> parseInputP1(List<String> lines) {
        var times = Arrays.stream(lines.get(0).split("\\s+"))
                .skip(1)
                .map(Integer::parseInt).toList();

        var recordDistance = Arrays.stream(lines.get(1).split("\\s+"))
                .skip(1)
                .map(Integer::parseInt).toList();

        List<Race> races = new ArrayList<>(times.size());
        IntStream.range(0, times.size()).forEach(i -> races.add(new Race(times.get(i), recordDistance.get(i))));

        return races;
    }

    private Race parseInputP2(List<String> lines) {
        final long time = Long.parseLong(lines.get(0)
                .replace("Time:", "")
                .replaceAll("\\s+", ""));

        final long recordDistance = Long.parseLong(lines.get(1)
                .replace("Distance:", "")
                .replaceAll("\\s+", ""));

        return new Race(time, recordDistance);
    }
}
