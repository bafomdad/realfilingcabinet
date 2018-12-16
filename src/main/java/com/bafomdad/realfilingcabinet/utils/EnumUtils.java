package com.bafomdad.realfilingcabinet.utils;

/**
 * Created by bafomdad on 12/14/2018.
 */
public class EnumUtils {

    public static <E extends Enum> void getLogic(E enumThing) {

        enumThing.ordinal();
    }
}
