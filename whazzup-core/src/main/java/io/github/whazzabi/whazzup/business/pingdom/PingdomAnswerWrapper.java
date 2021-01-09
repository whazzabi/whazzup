package io.github.whazzabi.whazzup.business.pingdom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PingdomAnswerWrapper {

    public List<PingdomAnswer> checks = new ArrayList<>();
}
