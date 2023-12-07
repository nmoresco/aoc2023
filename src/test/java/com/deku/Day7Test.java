package com.deku;

import static com.deku.Day7.FIVE_OF_A_KIND;
import static com.deku.Day7.FOUR_OF_A_KIND;
import static com.deku.Day7.PAIR;
import static com.deku.Day7.THREE_OF_A_KIND;
import static com.deku.Day7.TWO_PAIR;
import static com.deku.Day7.cardMap;
import static com.deku.Day7.getHandType;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class Day7Test {

    @Test
    void oopsAllJokers() {
        assertEquals(FIVE_OF_A_KIND, getHandType(mapStringToCards("JJJJJ"), 11));
        assertEquals(FIVE_OF_A_KIND, getHandType(mapStringToCards("JJJJK"), 11));
        assertEquals(FIVE_OF_A_KIND, getHandType(mapStringToCards("JJJKK"), 11));
        assertEquals(FIVE_OF_A_KIND, getHandType(mapStringToCards("JJKKK"), 11));
        assertEquals(FIVE_OF_A_KIND, getHandType(mapStringToCards("JKKKK"), 11));
    }

    @Test
    void bunchaJokers() {
        assertEquals(FOUR_OF_A_KIND, getHandType(mapStringToCards("JJJKQ"), 11));
        assertEquals(THREE_OF_A_KIND, getHandType(mapStringToCards("JJKQT"), 11));
        assertEquals(PAIR, getHandType(mapStringToCards("JKQT9"), 11));
    }

    @Test
    void sampleInputWithWilds() {
        assertEquals(PAIR, getHandType(mapStringToCards("32T3K"), 11));
        assertEquals(FOUR_OF_A_KIND, getHandType(mapStringToCards("T55J5"), 11));
        assertEquals(TWO_PAIR, getHandType(mapStringToCards("KK677"), 11));
        assertEquals(FOUR_OF_A_KIND, getHandType(mapStringToCards("KTJJT"), 11));
        assertEquals(FOUR_OF_A_KIND, getHandType(mapStringToCards("QQQJA"), 11));
    }

    @Test
    void sampleInputWithNoWilds() {
        assertEquals(PAIR, getHandType(mapStringToCards("32T3K"), -1));
        assertEquals(THREE_OF_A_KIND, getHandType(mapStringToCards("T55J5"), -1));
        assertEquals(TWO_PAIR, getHandType(mapStringToCards("KK677"), -1));
        assertEquals(TWO_PAIR, getHandType(mapStringToCards("KTJJT"), -1));
        assertEquals(THREE_OF_A_KIND, getHandType(mapStringToCards("QQQJA"), -1));
    }

    private int[] mapStringToCards(String str) {
        return str.chars().map(card -> cardMap.get((char) card)).toArray();
    }
}
