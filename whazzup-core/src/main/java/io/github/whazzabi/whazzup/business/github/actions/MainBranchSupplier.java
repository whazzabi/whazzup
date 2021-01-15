package io.github.whazzabi.whazzup.business.github.actions;

import java.util.Collections;
import java.util.List;

public class MainBranchSupplier implements BranchesOfRepositorySupplier {

    @Override
    public List<String> getBranchesOfOrganization(String repositoryName) {
        return Collections.singletonList("main");
    }
}
