package io.github.whazzabi.whazzup.business.jenkins.joblist;

import io.github.whazzabi.whazzup.business.jenkins.domain.JenkinsElement;
import org.junit.Test;

import static io.github.whazzabi.whazzup.business.jenkins.joblist.JenkinsJobListCheckProviderJobNameMapper.checkResultName;
import static org.junit.Assert.assertEquals;

public class JenkinsJobListCheckProviderJobNameMapperTest {

    @Test
    public void checkResultNameWithUrlUsage() throws Exception {
        assertEquals("foo/bar", checkResultName(job(), null, true));
    }

    @Test
    public void checkResultNameWithNameUsage() throws Exception {
        assertEquals("FooName", checkResultName(job(), null, false));
    }

    private JenkinsElement job() {
        final JenkinsElement element = new JenkinsElement();
        element.setName("FooName");
        element.setUrl("http://FooUrl/job/foo/job/bar/");
        return element;
    }
}