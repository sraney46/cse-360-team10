package testing;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Immutable result object that stores the outcome of one student's
 * participation analysis.
 */
public class ParticipationAnalysisResult {

    private final String studentUsername;
    private final Set<String> uniqueStudentsAnswered;
    private final int skippedInvalidRecords;
    private final boolean participationRequirementSatisfied;

    /**
     * Creates a new participation analysis result.
     *
     * @param studentUsername the student that was evaluated
     * @param uniqueStudentsAnswered the unique classmates the student replied to
     * @param skippedInvalidRecords number of malformed/null records ignored
     * @param participationRequirementSatisfied true if requirement is met
     */
    public ParticipationAnalysisResult(
            String studentUsername,
            Set<String> uniqueStudentsAnswered,
            int skippedInvalidRecords,
            boolean participationRequirementSatisfied) {

        this.studentUsername = studentUsername;
        this.uniqueStudentsAnswered = Collections.unmodifiableSet(
                new LinkedHashSet<>(uniqueStudentsAnswered));
        this.skippedInvalidRecords = skippedInvalidRecords;
        this.participationRequirementSatisfied = participationRequirementSatisfied;
    }

    /**
     * Returns the username of the student evaluated.
     *
     * @return student username
     */
    public String getStudentUsername() {
        return studentUsername;
    }

    /**
     * Returns the unique classmates the student answered.
     *
     * @return unmodifiable set of unique classmate usernames
     */
    public Set<String> getUniqueStudentsAnswered() {
        return uniqueStudentsAnswered;
    }

    /**
     * Returns the number of unique classmates answered.
     *
     * @return unique classmate count
     */
    public int getUniqueStudentCount() {
        return uniqueStudentsAnswered.size();
    }

    /**
     * Returns how many invalid records were skipped.
     *
     * @return skipped invalid record count
     */
    public int getSkippedInvalidRecords() {
        return skippedInvalidRecords;
    }

    /**
     * Indicates whether the student satisfied the participation requirement.
     *
     * @return true if satisfied, otherwise false
     */
    public boolean isParticipationRequirementSatisfied() {
        return participationRequirementSatisfied;
    }
}