package com.aj.searchapi.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UQLTest {

    @Test
    void buildUQL(){
        UQL uql = new UQL.Builder()
                .keyword("apple")
                .build();
        assertInstanceOf(UQL.class, uql);
    }

    @Test
    void query(){
        UQL uql = new UQL.Builder()
                .keyword("apple")
                .build();

        assertTrue(uql.getKeyword().equals("apple"));
    }

}