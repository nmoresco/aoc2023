package com.deku;

import static com.deku.Util.readLines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day5
        implements Puzzle {

    private static final int DEST = 0;
    private static final int SOURCE = 1;
    private static final int RANGE = 2;

    private record SeedsP2(Long seed, Long range) {}

    private record Maps(Set<Long> seeds,
                // Seeds in part 2 are treated differently
                List<SeedsP2> seedsP2,
                List<List<Long>> seedSoilMap,
                List<List<Long>> soilFertilizerMap,
                List<List<Long>> fertilizerWaterMap,
                List<List<Long>> waterLightMap,
                List<List<Long>> lightTempMap,
                List<List<Long>> tempHumidMap,
                List<List<Long>> humidLocationMap) {
    }

    @Override public void part1() {
        var maps = parseInput(readLines("day5.txt"));

        final Set<Long> soil = transform(maps.seeds, maps.seedSoilMap);
        final Set<Long> fertilizer = transform(soil, maps.soilFertilizerMap);
        final Set<Long> water = transform(fertilizer, maps.fertilizerWaterMap);
        final Set<Long> light = transform(water, maps.waterLightMap);
        final Set<Long> temp = transform(light, maps.lightTempMap);
        final Set<Long> humid = transform(temp, maps.tempHumidMap);
        final Set<Long> location = transform(humid, maps.humidLocationMap);

        System.out.println(location.stream().mapToLong(i -> i).min().orElseThrow());
    }

    @Override public void part2() {
        var maps = parseInput(readLines("day5.txt"));

        // Start with locations and work backwards to see if we land on a seed.
        final long highestLocation = maps.humidLocationMap.stream()
                .mapToLong(mapping -> mapping.get(DEST) + mapping.get(RANGE)).max().orElse(0);

        long min = 0;
        for (long i = 0; i < highestLocation; i++) {
            final Long humid = inverseTransform(i, maps.humidLocationMap);
            final Long temp = inverseTransform(humid, maps.tempHumidMap);
            final Long light = inverseTransform(temp, maps.lightTempMap);
            final Long water = inverseTransform(light, maps.waterLightMap);
            final Long fertilizer = inverseTransform(water, maps.fertilizerWaterMap);
            final Long soil = inverseTransform(fertilizer, maps.soilFertilizerMap);
            final Long seed = inverseTransform(soil, maps.seedSoilMap);

            if (checkSeeds(seed, maps.seedsP2) > 0) {
                min = i;
                break;
            }
        }

        System.out.println(min);
    }

    private static Set<Long> transform(Set<Long> current, List<List<Long>> maps) {
        return current.stream().map(seed -> maps.stream()
                .filter(mapping -> mapping.get(SOURCE) <= seed && (mapping.get(SOURCE) + mapping.get(RANGE) > seed))
                .map(mapping -> mapping.get(DEST) + (seed - mapping.get(SOURCE)))
                .findFirst()
                .orElse(seed)).collect(Collectors.toSet());
    }

    private static Long inverseTransform(Long current, List<List<Long>> maps) {
        return maps.stream()
                .filter(mapping -> mapping.get(DEST) <= current && (mapping.get(DEST) + mapping.get(RANGE) > current))
                .map(mapping -> mapping.get(SOURCE) + (current - mapping.get(DEST)))
                .findFirst()
                .orElse(current);
    }

    private static Long checkSeeds(Long current, List<SeedsP2> seeds) {
        return seeds.stream()
                .filter(seed -> seed.seed <= current && (seed.seed + seed.range > current))
                .mapToLong(seed -> seed.seed + (current - seed.seed))
                .findFirst()
                .orElse(-1L);
    }

    private Maps parseInput(final List<String> lines) {
        final Set<Long> seeds = Arrays.stream(lines.get(0).split(" "))
                .skip(1)
                .map(Long::parseLong).collect(Collectors.toSet());

        final long[] seedNums = Arrays.stream(lines.get(0).split(" "))
                .skip(1)
                .mapToLong(Long::parseLong).toArray();

        final List<SeedsP2> seedsP2 = new ArrayList<>(seedNums.length / 2);
        for (int i = 0; i < seedNums.length; i += 2) {
            seedsP2.add(new SeedsP2(seedNums[i], seedNums[i + 1]));
        }

        final var seedSoil = getMapping(lines, "seed-to-soil map:");
        final var soilFertilizer = getMapping(lines, "soil-to-fertilizer map:");
        final var fertilizerWater = getMapping(lines, "fertilizer-to-water map:");
        final var waterLight = getMapping(lines, "water-to-light map:");
        final var lightTemp = getMapping(lines, "light-to-temperature map:");
        final var tempHumid = getMapping(lines, "temperature-to-humidity map:");
        final var humidLocation = getMapping(lines, "humidity-to-location map:");

        return new Maps(seeds, seedsP2, seedSoil, soilFertilizer, fertilizerWater, waterLight, lightTemp, tempHumid, humidLocation);
    }

    private List<List<Long>> getMapping(List<String> lines, String mapLabel) {

        return lines.stream().dropWhile(line -> !line.equals(mapLabel))
                .skip(1)
                .takeWhile(line -> !line.isEmpty())
                .map(line -> Arrays.stream(line.split(" ")).map(Long::parseLong).toList()).toList();
    }

}
