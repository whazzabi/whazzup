package io.github.whazzabi.whazzup.business.github.common.api;

import org.junit.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class GithubBranchDetailsTest {

    @Test
    public void ageInDays_should_be_correct() {
        assertThat(branchWithDate("2021-05-07T13:25:49Z").getAgeInDays()).isEqualTo(3);
        assertThat(branchWithDate("2021-05-09T13:26:49Z").getAgeInDays()).isEqualTo(0);
    }

    private GithubBranchDetails branchWithDate(String date) {
        GithubBranchDetails branch = spy(new GithubBranchDetails());
        doReturn(OffsetDateTime.parse("2021-05-10T13:25:49Z")).when(branch).now();
        branch.commit = new GithubCommit();
        branch.commit.commit = new GithubCommitDetail();
        branch.commit.commit.author = new GithubCommitAuthor();
        branch.commit.commit.author.date = date;
        return branch;
    }
}