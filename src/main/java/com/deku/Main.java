package com.deku;

public class Main {
    public static void main(String[] args) {
        var puzzle = new Day2();

        if (args.length == 0 || !args[0].equals("p2")) {
            puzzle.part1();
        }
        else {
            puzzle.part2();
        }
    }
}
