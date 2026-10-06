package guide.spring;

import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class ContainerTransactionLab {
    public static final class LifecycleProbe {
        private boolean opened;
        private boolean closed;

        public void open() {
            opened = true;
        }

        public void close() {
            closed = true;
        }
    }

    public static class JobService {
        private final JdbcTemplate jdbc;

        JobService(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Transactional
        public void accept(String jobId) {
            check(TransactionSynchronizationManager.isActualTransactionActive(), "Transaction active");
            jdbc.update("insert into sync_job (job_id) values (?)", jobId);
        }

        @Transactional
        public void insertThenFail(String jobId) {
            jdbc.update("insert into sync_job (job_id) values (?)", jobId);
            throw new IllegalStateException("Deliberate rollback fixture");
        }

        public void selfInvokeThenFail(String jobId) {
            insertThenFail(jobId);
        }
    }

    public static class BatchFacade {
        private final JobService jobs;

        BatchFacade(JobService jobs) {
            this.jobs = jobs;
        }

        @Transactional
        public void catchParticipatingFailure() {
            try {
                jobs.insertThenFail("joined-failure");
            } catch (IllegalStateException expected) {
                check(TransactionSynchronizationManager.isActualTransactionActive(), "Outer transaction remains bound");
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement(proxyTargetClass = true)
    public static class LabConfiguration {
        @Bean(initMethod = "open", destroyMethod = "close")
        LifecycleProbe lifecycleProbe() {
            return new LifecycleProbe();
        }

        @Bean
        DataSource dataSource() {
            var source = new JdbcDataSource();
            source.setURL("jdbc:h2:mem:guide03;DB_CLOSE_DELAY=-1");
            return source;
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            var jdbc = new JdbcTemplate(dataSource);
            jdbc.execute("create table sync_job (job_id varchar(80) primary key)");
            return jdbc;
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        JobService jobService(JdbcTemplate jdbcTemplate) {
            return new JobService(jdbcTemplate);
        }

        @Bean
        BatchFacade batchFacade(JobService jobService) {
            return new BatchFacade(jobService);
        }
    }

    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    static void expect(Class<? extends RuntimeException> type, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException failure) {
            check(type.isInstance(failure), "Unexpected exception: " + failure);
            return;
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    public static void main(String[] args) {
        LifecycleProbe lifecycle;
        try (var context = new AnnotationConfigApplicationContext(LabConfiguration.class)) {
            lifecycle = context.getBean(LifecycleProbe.class);
            check(lifecycle.opened, "Init method invoked");
            check(context.getBean(LifecycleProbe.class) == lifecycle, "Singleton per context");
            var jobs = context.getBean(JobService.class);
            check(AopUtils.isCglibProxy(jobs), "Explicit class-based transaction proxy");
            var jdbc = context.getBean(JdbcTemplate.class);
            jobs.accept("committed");
            expect(IllegalStateException.class, () -> jobs.insertThenFail("rolled-back"));
            check(jdbc.queryForObject("select count(*) from sync_job", Integer.class) == 1, "External call rolled back");
            expect(IllegalStateException.class, () -> jobs.selfInvokeThenFail("self-call"));
            check(jdbc.queryForObject("select count(*) from sync_job", Integer.class) == 2, "Self-call bypass left auto-committed insert");
            var facade = context.getBean(BatchFacade.class);
            expect(UnexpectedRollbackException.class, facade::catchParticipatingFailure);
            check(jdbc.queryForObject("select count(*) from sync_job", Integer.class) == 2, "Joined transaction was rolled back");
        }
        check(lifecycle.closed, "Destroy method invoked on context close");
        System.out.println("ContainerTransactionLab: all checks passed");
    }
}