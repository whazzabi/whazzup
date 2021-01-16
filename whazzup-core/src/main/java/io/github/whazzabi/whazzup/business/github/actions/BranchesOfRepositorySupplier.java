package io.github.whazzabi.whazzup.business.github.actions;

import java.util.List;

public interface BranchesOfRepositorySupplier {

    List<String> get(String repositoryName);
}
