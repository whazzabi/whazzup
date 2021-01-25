package io.github.whazzabi.whazzup.presentation.controller;

import io.github.whazzabi.whazzup.business.check.checkresult.CheckResultComment;
import io.github.whazzabi.whazzup.business.check.checkresult.CheckResultCommentRepository;
import io.github.whazzabi.whazzup.business.customization.Team;
import io.github.whazzabi.whazzup.business.customization.TeamProvider;
import io.github.whazzabi.whazzup.presentation.UiInfo;
import io.github.whazzabi.whazzup.presentation.UiInfoService;
import io.github.whazzabi.whazzup.presentation.UiStateSummary;
import io.github.whazzabi.whazzup.presentation.UiTeams;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest")
public class DashController {

    @Autowired
    private UiInfoService uiInfoService;

    @Autowired(required = false)
    private TeamProvider teamProvider;

    @Autowired
    private UiConfigPropertries uiConfigPropertries;

    @Autowired
    private CheckResultCommentRepository commentRepository;

    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public UiConfigPropertries config() {
        return uiConfigPropertries;
    }

    @RequestMapping(value = "/infos", method = RequestMethod.GET)
    public UiInfo infos() throws IOException, AuthenticationException, ExecutionException, InterruptedException, URISyntaxException {
        return uiInfoService.infos();
    }

    @RequestMapping(value = "/teams", method = RequestMethod.GET)
    public UiTeams teams() {
        if (teamProvider == null) {
            return UiTeams.ALL;
        }
        List<String> teams = teamProvider.getTeams().stream().map(Team::getTeamName).collect(Collectors.toList());
        return new UiTeams(teams);
    }

    @RequestMapping(value = "/info-summary", method = RequestMethod.GET)
    public UiStateSummary infoSummary(){
        return UiStateSummary.from(uiInfoService.infos());
    }

    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    public List<CheckResultComment> comments() {
        return commentRepository.comments();
    }

    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    public void storeComment(@RequestBody ArrayList<CheckResultComment> comments) {
        commentRepository.addComments(comments);
    }
}
