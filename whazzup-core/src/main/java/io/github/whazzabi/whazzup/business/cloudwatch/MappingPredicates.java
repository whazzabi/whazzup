package io.github.whazzabi.whazzup.business.cloudwatch;

import java.util.List;

import static java.util.Collections.emptyList;

public class MappingPredicates {

    private List<String> includes;
    private List<String> excludes;

    public List<String> getIncludes() {
        return includes != null ? includes : emptyList();
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return excludes != null ? excludes : emptyList();
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}