package com.tibco.bw.maven.plugin.lifecycle;

import java.util.Date;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.BuildFailure;
import org.apache.maven.execution.BuildSuccess;
import org.apache.maven.execution.BuildSummary;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs execution events to logger, eventually user-supplied.
 *
 * @author Benjamin Bentmann
 */
public class BWExecutionListener extends AbstractExecutionListener {
    private final Logger logger;
    private static final int LINE_LENGTH = 72;

    public BWExecutionListener() {
        logger = LoggerFactory.getLogger(BWExecutionListener.class);
    }

    // TODO should we deprecate?
    public BWExecutionListener(Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger missing");
        }
        this.logger = logger;
    }

    private static String chars(char c, int count) {
        StringBuilder buffer = new StringBuilder(count);
        for (int i = count; i > 0; i--) {
            buffer.append(c);
        }
        return buffer.toString();
    }

    private static String getFormattedTime(long time) {
        // NOTE: DateFormat is not suitable to format timespans of 24h+
        long h = time / (60 * 60 * 1000);
        long m = (time - h * 60 * 60 * 1000) / (60 * 1000);
        long s = (time - h * 60 * 60 * 1000 - m * 60 * 1000) / 1000;
        long ms = time % 1000;
        String format;
        if (h > 0) {
            format = "%1$d:%2$02d:%3$02d.%4$03ds";
        } else if (m > 0) {
            format = "%2$d:%3$02d.%4$03ds";
        } else {
            format = "%3$d.%4$03ds";
        }
        return String.format(format, h, m, s, ms);
    }

    @Override
    public void projectDiscoveryStarted(ExecutionEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info("Scanning for projects...");
        }
    }

    @Override
    public void sessionStarted(ExecutionEvent event) {
        if (logger.isInfoEnabled() && event.getSession().getProjects().size() > 1) {
            logger.info(chars('-', LINE_LENGTH));
            logger.info("Reactor Build Order:");
            logger.info("");
            for (MavenProject project : event.getSession().getProjects()) {
                logger.info(project.getName());
            }
        }
    }

    @Override
    public void sessionEnded(ExecutionEvent event) {
        if (logger.isInfoEnabled()) {
            if (event.getSession().getProjects().size() > 1) {
                logReactorSummary(event.getSession());
            }
            logResult(event.getSession());
            logStats(event.getSession());
            logger.info(chars('-', LINE_LENGTH));
        }
    }

    private void logReactorSummary(MavenSession session) {
        logger.info(chars('-', LINE_LENGTH));
        logger.info("Reactor Summary:");
        logger.info("");
        MavenExecutionResult result = session.getResult();
        for (MavenProject project : session.getProjects()) {
            StringBuilder buffer = new StringBuilder(128);
            buffer.append(project.getName());
            buffer.append(' ');
            while (buffer.length() < LINE_LENGTH - 21) {
                buffer.append('.');
            }
            buffer.append(' ');
            BuildSummary buildSummary = result.getBuildSummary(project);
            if (buildSummary == null) {
                buffer.append("SKIPPED");
            } else if (buildSummary instanceof BuildSuccess) {
                buffer.append("SUCCESS [");
                buffer.append(getFormattedTime(buildSummary.getTime()));
                buffer.append("]");
            } else if (buildSummary instanceof BuildFailure) {
                buffer.append("FAILURE [");
                buffer.append(getFormattedTime(buildSummary.getTime()));
                buffer.append("]");
            }
            logger.info(buffer.toString());
        }
    }

    private void logResult(MavenSession session) {
        logger.info(chars('-', LINE_LENGTH));
        if (session.getResult().hasExceptions()) {
            logger.info("BUILD FAILURE");
        } else {
            logger.info("BUILD SUCCESS");
        }
    }

    private void logStats(MavenSession session) {
        logger.info(chars('-', LINE_LENGTH));
        Date finish = new Date();
        long time = finish.getTime() - session.getRequest().getStartTime().getTime();
        String wallClock = "";
        logger.info("Total time: " + getFormattedTime(time) + wallClock);
        logger.info("Finished at: " + finish);
        System.gc();
        Runtime r = Runtime.getRuntime();
        long MB = 1024 * 1024;
        logger.info("Final Memory: " + (r.totalMemory() - r.freeMemory()) / MB + "M/" + r.totalMemory() / MB + "M");
    }

    @Override
    public void projectSkipped(ExecutionEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info(chars(' ', LINE_LENGTH));
            logger.info(chars('-', LINE_LENGTH));
            logger.info("Skipping " + event.getProject().getName());
            logger.info("This project has been banned from the build due to previous failures.");
            logger.info(chars('-', LINE_LENGTH));
        }
    }

    @Override
    public void projectStarted(ExecutionEvent event) {
        if (logger.isInfoEnabled()) {
            logger.info(chars(' ', LINE_LENGTH));
            logger.info(chars('-', LINE_LENGTH));
            logger.info("Building " + event.getProject().getName() + " " + event.getProject().getVersion());
            logger.info(chars('-', LINE_LENGTH));
        }
    }

    @Override
    public void mojoSkipped(ExecutionEvent event) {
        if (logger.isWarnEnabled()) {
            logger.warn("Goal " + event.getMojoExecution().getGoal() + " requires online mode for execution but Maven is currently offline, skipping");
        }
    }

    /**
     * <pre>--- mojo-artifactId:version:goal (mojo-executionId) @ project-artifactId ---</pre>
     */
    @Override
    public void mojoStarted(ExecutionEvent event) {
        if (logger.isInfoEnabled()) {
            StringBuilder buffer = new StringBuilder(128);
            buffer.append("--- ");
            append(buffer, event.getMojoExecution());
            append(buffer, event.getProject());
            buffer.append(" ---");
            logger.info("");
            logger.info(buffer.toString());
        }
    }

    /**
     * <pre>>>> mojo-artifactId:version:goal (mojo-executionId) @ project-artifactId >>></pre>
     */
    @Override
    public void forkStarted(ExecutionEvent event) {
        if (logger.isInfoEnabled()) {
            StringBuilder buffer = new StringBuilder(128);
            buffer.append(">>> ");
            append(buffer, event.getMojoExecution());
            append(buffer, event.getProject());
            buffer.append(" >>>");
            logger.info("");
            logger.info(buffer.toString());
        }
    }

    /**
     * <pre>&lt;&lt;&lt; mojo-artifactId:version:goal (mojo-executionId) @ project-artifactId &lt;&lt;&lt;</pre>
     */
    @Override
    public void forkSucceeded(ExecutionEvent event) {
        if (logger.isInfoEnabled()) {
            StringBuilder buffer = new StringBuilder(128);
            buffer.append("<<< ");
            append(buffer, event.getMojoExecution());
            append(buffer, event.getProject());
            buffer.append(" <<<");
            logger.info("");
            logger.info(buffer.toString());
        }
    }

    private void append(StringBuilder buffer, MojoExecution me) {
        buffer.append(me.getArtifactId()).append(':').append(me.getVersion());
        buffer.append(':').append(me.getGoal());
        if (me.getExecutionId() != null) {
            buffer.append(" (").append(me.getExecutionId()).append(')');
        }
    }

    private void append(StringBuilder buffer, MavenProject project) {
        buffer.append(" @ ").append(project.getArtifactId());
    }

    @Override
    public void forkedProjectStarted(ExecutionEvent event) {
        if (logger.isInfoEnabled() && event.getMojoExecution().getForkedExecutions().size() > 1) {
            logger.info(chars(' ', LINE_LENGTH));
            logger.info(chars('>', LINE_LENGTH));
            logger.info("Forking " + event.getProject().getName() + " " + event.getProject().getVersion());
            logger.info(chars('>', LINE_LENGTH));
        }
    }
}
