package io.github.whazzabi.whazzup.business.stash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StashPullRequest {

    private final String id;
    private final StashRepo stashRepo;
    private final List<StashUser> reviewers;
    private Long ageInDays;

    public StashPullRequest(String id, StashRepo stashRepo) {
        this.id = id;
        this.stashRepo = stashRepo;
        this.reviewers = new ArrayList<>();
    }

    public String id() {
        return id;
    }

    public StashPullRequest addReviewer(StashUser stashUser) {
        reviewers.add(stashUser);
        return this;
    }

    public List<StashUser> reviewers() {
        return Collections.unmodifiableList(reviewers);
    }

    public StashRepo repo() {
        return stashRepo;
    }

    public Long getAgeInDays() {
        return ageInDays;
    }

    public void addCreatedDate(Long createdDateAsTimestampInMs) {
        if(createdDateAsTimestampInMs != null) {
            this.ageInDays = (System.currentTimeMillis() - createdDateAsTimestampInMs) / 1000 / 60 / 60 / 24;
        }
    }
}
