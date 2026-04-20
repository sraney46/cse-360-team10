package testing;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Prototype service that determines whether a student replied to at least
 * three different students in the discussion system.
 * <p>
 * This class is intentionally isolated from the database and GUI layers so it
 * can be developed with test-driven development before being integrated into
 * the team's TP3 grading workflow.
 */
public class DiscussionParticipationAnalyzer {

    /** Minimum number of different classmates a student must answer. */
    public static final int REQUIRED_UNIQUE_STUDENTS = 3;

    /**
     * Evaluates one student's reply activity against the participation rule.
     *
     * @param studentUsername the student being checked
     * @param replies all available reply relationships for the discussion period
     * @return a result object containing counts and pass/fail status
     * @throws IllegalArgumentException if the student username is null or blank
     */
    public ParticipationAnalysisResult analyzeStudentParticipation(
            String studentUsername, List<ReplyRecord> replies) {

        if (isBlank(studentUsername)) {
            throw new IllegalArgumentException("Student username is required");
        }

        Set<String> uniqueStudentsAnswered = new LinkedHashSet<>();
        int skippedInvalidRecords = 0;

        if (replies == null) {
            return new ParticipationAnalysisResult(
                    studentUsername.trim(),
                    uniqueStudentsAnswered,
                    0,
                    false);
        }

        for (ReplyRecord reply : replies) {
            // The prototype deliberately ignores malformed data rather than
            // crashing because graders still need a stable screen even when
            // discussion records are incomplete or corrupted.
            if (reply == null) {
                skippedInvalidRecords++;
                continue;
            }

            String replier = normalize(reply.getReplierUsername());
            String target = normalize(reply.getTargetUsername());

            if (replier == null || target == null) {
                skippedInvalidRecords++;
                continue;
            }

            // Only replies written by the student currently being checked
            // should affect that student's participation result.
            if (!studentUsername.trim().equalsIgnoreCase(replier)) {
                continue;
            }

            // Self-replies should not count toward the three-student rule
            // because the requirement is specifically about answering peers.
            if (studentUsername.trim().equalsIgnoreCase(target)) {
                continue;
            }

            // A student may answer the same classmate multiple times. The
            // grading rule counts unique classmates, not total reply volume.
            uniqueStudentsAnswered.add(target);
        }

        boolean satisfied = uniqueStudentsAnswered.size() >= REQUIRED_UNIQUE_STUDENTS;

        return new ParticipationAnalysisResult(
                studentUsername.trim(),
                uniqueStudentsAnswered,
                skippedInvalidRecords,
                satisfied);
    }

    /**
     * Normalizes a string for comparison.
     *
     * @param value input string
     * @return trimmed string or null if blank
     */
    private String normalize(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    /**
     * Checks whether a string is null or blank.
     *
     * @param value input string
     * @return true if null or blank, otherwise false
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}