package edu.berkeley.cs.jqf.fuzz.util;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.Map;
import java.util.function.Function;

public class ConfigJavascriptGenerator extends Config{

    public Map<Function<SourceOfRandomness, String>,Boolean> BUTCFPIA_;
    public Map<Function<SourceOfRandomness, String>,Boolean> EBCRTVE_;
    public Map<Function<SourceOfRandomness, String>,Boolean> IFWNSTB_;
    public Map<String,Boolean> BIN_;
    public Map<String,Boolean> UNA_;


    public ConfigJavascriptGenerator(Map<Function<SourceOfRandomness, String>,Boolean> BUTCFPIA,Map<Function<SourceOfRandomness, String>,Boolean> EBCRTVE
                                     ,Map<Function<SourceOfRandomness, String>,Boolean> IFWNSTB,  Map<String,Boolean> BIN,Map<String,Boolean> UNA ){
        super();
        this.BIN_ =BIN;
        this.UNA_ = UNA;
        this.BUTCFPIA_ = BUTCFPIA;
        this.EBCRTVE_ = EBCRTVE;
        this.IFWNSTB_ = IFWNSTB;
    }

}
