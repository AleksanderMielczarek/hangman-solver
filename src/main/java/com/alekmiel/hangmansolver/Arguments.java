package com.alekmiel.hangmansolver;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.util.List;

/**
 * Created by Aleksander on 15.07.2016.
 */
public class Arguments {

    @Parameter(names = {"-dictionaries", "-d"}, descriptionKey = "path to *.dic dictionaries", required = true, variableArity = true)
    private List<File> dictionaries;

    @Parameter(names = {"-patterns", "-p"}, descriptionKey = "list of patterns, _ for empty, e.g. JAVA will be J_V_, Java is great will be J_v_a __ g__at", variableArity = true, required = true)
    private List<String> patterns;

    public List<File> getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(List<File> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }
}
