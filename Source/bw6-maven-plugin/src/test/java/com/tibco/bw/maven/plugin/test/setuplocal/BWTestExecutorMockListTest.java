package com.tibco.bw.maven.plugin.test.setuplocal;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BWTestExecutor#setMockActivityList.
 *
 * BWTestExecutor.execute() parses every .bwt / .bwm file of every module, appends the
 * skip-init / mock entries of that file to its own cumulative list and then passes the whole
 * cumulative list to BWTestExecutor.INSTANCE.setMockActivityList(...). EngineLaunchConfigurator
 * later turns every entry of INSTANCE's list into a -D argument of the engine command line.
 *
 * With a plain addAll the INSTANCE list grew quadratically with the number of test files
 * (37 .bwt files -> 1,332 arguments, ~72,000 characters), and on Windows the engine launch failed
 * with "CreateProcess error=206, The filename or extension is too long".
 */
public class BWTestExecutorMockListTest {

    /** Reproduces the exact calling pattern of BWTestExecutor.execute(). */
    @Test
    public void cumulativeCallsDoNotDuplicateEntries() {
        BWTestExecutor instance = new BWTestExecutor();   // stands in for BWTestExecutor.INSTANCE
        List<String> cumulative = new ArrayList<String>(); // the executor's own mockActivity list

        int files = 37;
        for (int i = 0; i < files; i++) {
            cumulative.add("-DTestProcess" + i + "=true");
            cumulative.add("-DProcess" + i + "Activity=true");
            instance.setMockActivityList(cumulative);
        }

        List<String> result = instance.getMockActivityList();
        assertEquals(files * 2, result.size(), "each argument must be passed to the engine exactly once");
        assertEquals(new HashSet<String>(result).size(), result.size(), "no duplicates expected");
    }

    @Test
    public void preservesFirstSeenOrder() {
        BWTestExecutor instance = new BWTestExecutor();
        instance.setMockActivityList(Arrays.asList("-Da=true", "-Db=true"));
        instance.setMockActivityList(Arrays.asList("-Db=true", "-Dc=true", "-Da=true"));

        assertEquals(Arrays.asList("-Da=true", "-Db=true", "-Dc=true"), instance.getMockActivityList());
    }

    @Test
    public void passingOwnListIsSafe() {
        BWTestExecutor instance = new BWTestExecutor();
        instance.setMockActivityList(Arrays.asList("-Da=true", "-Db=true"));

        instance.setMockActivityList(instance.getMockActivityList());

        assertEquals(Arrays.asList("-Da=true", "-Db=true"), instance.getMockActivityList());
    }

    @Test
    public void listIsReusableAfterEngineLaunchClearsIt() {
        // EngineLaunchConfigurator copies the list into the launch arguments and then clears it.
        BWTestExecutor instance = new BWTestExecutor();
        instance.setMockActivityList(Arrays.asList("-Da=true"));
        instance.getMockActivityList().clear();

        instance.setMockActivityList(Arrays.asList("-Da=true", "-Db=true"));

        assertEquals(Arrays.asList("-Da=true", "-Db=true"), instance.getMockActivityList());
    }
}
