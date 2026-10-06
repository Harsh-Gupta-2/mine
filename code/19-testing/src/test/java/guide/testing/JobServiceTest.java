package guide.testing;

import java.util.ConcurrentModificationException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

final class JobServiceTest {
    private final JobService.Repository repository = mock(JobService.Repository.class);
    private final JobService service = new JobService(repository);

    @Test
    void tenantDenialDoesNotTouchRepository() {
        assertThrows(SecurityException.class, () -> service.cancel("tenant-a", "tenant-b", "job-1"));
        verifyNoInteractions(repository);
    }

    @Test
    void acceptedJobTransitionsWithExpectedVersion() {
        var current = new JobService.Job("tenant-a", "job-1", 4, JobService.Status.ACCEPTED);
        var next = new JobService.Job("tenant-a", "job-1", 5, JobService.Status.CANCELLED);
        when(repository.find("tenant-a", "job-1")).thenReturn(Optional.of(current));
        when(repository.replace(current, next)).thenReturn(true);
        assertEquals(next, service.cancel("tenant-a", "tenant-a", "job-1"));
        verify(repository).replace(current, next);
    }

    @Test
    void optimisticConflictIsNotReportedAsSuccess() {
        var current = new JobService.Job("tenant-a", "job-1", 4, JobService.Status.ACCEPTED);
        when(repository.find("tenant-a", "job-1")).thenReturn(Optional.of(current));
        when(repository.replace(any(), any())).thenReturn(false);
        assertThrows(ConcurrentModificationException.class, () -> service.cancel("tenant-a", "tenant-a", "job-1"));
    }

    @ParameterizedTest
    @EnumSource(value = JobService.Status.class, names = {"RUNNING", "CANCELLED"})
    void nonAcceptedJobsDoNotWrite(JobService.Status status) {
        when(repository.find("tenant-a", "job-1")).thenReturn(Optional.of(new JobService.Job("tenant-a", "job-1", 4, status)));
        assertThrows(IllegalStateException.class, () -> service.cancel("tenant-a", "tenant-a", "job-1"));
        verify(repository, never()).replace(any(), any());
    }
}