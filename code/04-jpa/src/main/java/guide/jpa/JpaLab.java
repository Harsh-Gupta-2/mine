package guide.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;

public final class JpaLab {
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static void close(EntityManager manager) {
        try {
            if (manager.getTransaction().isActive()) manager.getTransaction().rollback();
        } finally {
            manager.close();
        }
    }

    static void seed(EntityManagerFactory factory) {
        var manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            for (long index = 1; index <= 3; index++) {
                var connector = new Connector(index, "tenant-a", "Connector " + index);
                manager.persist(connector);
                manager.persist(new SyncJob(index, "tenant-a", connector));
            }
            manager.getTransaction().commit();
        } finally {
            close(manager);
        }
    }

    static void identityFlushAndRollback(EntityManagerFactory factory) {
        var manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            var first = manager.find(SyncJob.class, 1L);
            var second = manager.find(SyncJob.class, 1L);
            check(first == second, "One managed instance for this identity");
            first.setStatus("RUNNING");
            manager.flush();
            var databaseStatus = manager.createNativeQuery("select status from guide_sync_job where id = 1").getSingleResult();
            check("RUNNING".equals(databaseStatus), "Flush reached SQL in this transaction");
            manager.getTransaction().rollback();
        } finally {
            close(manager);
        }
        var observer = factory.createEntityManager();
        try {
            check("QUEUED".equals(observer.find(SyncJob.class, 1L).getStatus()), "Flush did not commit");
        } finally {
            close(observer);
        }
    }

    static void mergeDetached(EntityManagerFactory factory) {
        SyncJob detached;
        var reader = factory.createEntityManager();
        try {
            detached = reader.find(SyncJob.class, 1L);
        } finally {
            close(reader);
        }
        detached.setStatus("STAGED");
        var writer = factory.createEntityManager();
        try {
            writer.getTransaction().begin();
            var managed = writer.merge(detached);
            check(managed != detached, "Merge returns a different managed copy for detached input");
            check(writer.contains(managed) && !writer.contains(detached), "Only returned copy is managed");
            writer.getTransaction().commit();
        } finally {
            close(writer);
        }
    }

    static void optimisticConflict(EntityManagerFactory factory) {
        var first = factory.createEntityManager();
        var second = factory.createEntityManager();
        try {
            first.getTransaction().begin();
            second.getTransaction().begin();
            var firstCopy = first.find(SyncJob.class, 2L);
            var staleCopy = second.find(SyncJob.class, 2L);
            firstCopy.setStatus("RUNNING");
            first.getTransaction().commit();
            staleCopy.setStatus("FAILED");
            boolean conflict = false;
            try {
                second.getTransaction().commit();
            } catch (RuntimeException failure) {
                Throwable cause = failure;
                while (cause != null) {
                    if (cause instanceof OptimisticLockException) { conflict = true; break; }
                    cause = cause.getCause();
                }
                if (!conflict) throw failure;
            }
            check(conflict, "Stale version must fail instead of silently overwriting");
        } finally {
            try { close(second); } finally { close(first); }
        }
    }

    static long readConnectorNames(EntityManagerFactory factory, boolean fetchJoin) {
        var statistics = factory.unwrap(SessionFactory.class).getStatistics();
        var manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            statistics.clear();
            String query = fetchJoin
                    ? "select job from SyncJob job join fetch job.connector where job.tenantId = :tenant order by job.id"
                    : "select job from SyncJob job where job.tenantId = :tenant order by job.id";
            var jobs = manager.createQuery(query, SyncJob.class).setParameter("tenant", "tenant-a").getResultList();
            check(jobs.size() == 3, "Three fixture jobs");
            for (var job : jobs) check(job.getConnector().getDisplayName().startsWith("Connector "), "Association accessible");
            long statements = statistics.getPrepareStatementCount();
            manager.getTransaction().rollback();
            return statements;
        } finally {
            close(manager);
        }
    }

    public static void main(String[] args) {
        var factory = Persistence.createEntityManagerFactory("guide-jpa");
        try {
            seed(factory);
            identityFlushAndRollback(factory);
            mergeDetached(factory);
            optimisticConflict(factory);
            long lazyStatements = readConnectorNames(factory, false);
            long fetchStatements = readConnectorNames(factory, true);
            check(fetchStatements == 1 && lazyStatements > fetchStatements, "Fetch plan removes fixture secondary selects");
            System.out.println("JpaLab: all checks passed; lazy=" + lazyStatements + ", fetch=" + fetchStatements);
        } finally {
            factory.close();
        }
    }
}