package guide.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.Objects;

@Entity
@Table(name = "guide_sync_job")
public class SyncJob {
    @Id
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String status;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "connector_id", nullable = false)
    private Connector connector;

    protected SyncJob() {}

    public SyncJob(Long id, String tenantId, Connector connector) {
        this.id = Objects.requireNonNull(id);
        this.tenantId = Objects.requireNonNull(tenantId);
        this.connector = Objects.requireNonNull(connector);
        if (!tenantId.equals(connector.getTenantId())) {
            throw new IllegalArgumentException("Tenant mismatch");
        }
        this.status = "QUEUED";
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public Long getVersion() { return version; }
    public Connector getConnector() { return connector; }
    public void setStatus(String status) { this.status = Objects.requireNonNull(status); }
}