package guide.testing;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Optional;

public final class JobService {
    public enum Status { ACCEPTED, RUNNING, CANCELLED }
    public record Job(String tenant, String id, long version, Status status) {
        public Job {
            Objects.requireNonNull(tenant); Objects.requireNonNull(id); Objects.requireNonNull(status);
            if (version < 0) throw new IllegalArgumentException("negative version");
        }
    }
    public interface Repository {
        Optional<Job> find(String tenant, String id);
        boolean replace(Job expected, Job replacement);
    }
    private final Repository repository;
    public JobService(Repository repository) { this.repository = Objects.requireNonNull(repository); }

    public Job cancel(String authorizedTenant, String requestedTenant, String id) {
        if (authorizedTenant == null || !authorizedTenant.equals(requestedTenant)) throw new SecurityException("tenant denied");
        Job current = repository.find(requestedTenant, id).orElseThrow(() -> new IllegalArgumentException("job not found"));
        if (!current.tenant().equals(requestedTenant) || !current.id().equals(id)) throw new SecurityException("repository scope mismatch");
        if (current.status() != Status.ACCEPTED) throw new IllegalStateException("only unstarted jobs can be cancelled by this operation");
        Job next = new Job(current.tenant(), current.id(), Math.incrementExact(current.version()), Status.CANCELLED);
        if (!repository.replace(current, next)) throw new ConcurrentModificationException("job changed");
        return next;
    }
}