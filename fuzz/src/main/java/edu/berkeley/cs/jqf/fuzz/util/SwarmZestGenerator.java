package edu.berkeley.cs.jqf.fuzz.util;

import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public abstract class SwarmZestGenerator<T> extends Generator<T> {

    protected SwarmZestGenerator(Class<T> type) {
        super(type);
    }



    public abstract void setConfig(Config config);

    public abstract Config getConfig();

    public abstract void updateFeatureSets(Random random, int number);

    public abstract void setFeatureSets();

    public abstract void prepareFeatureSets(Random random);


    }
