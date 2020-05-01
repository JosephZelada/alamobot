package com.alamobot.core;

import lombok.experimental.Delegate;

public class CoreARandom {
    public static final CoreARandom aRandom = new CoreARandom();

    @Delegate
    public CoreRandomBuilderSupport coreRandomBuilderSupport = new CoreRandomBuilderSupport();
    @Delegate
    private RandomGenerator randomGenerator = new RandomGenerator();
}
