package guide.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "guide_connector")
public class Connector {
    @Id
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    protected Connector() {}

    public Connector(Long id, String tenantId, String displayName) {
        this.id = id;
        this.tenantId = tenantId;
        this.displayName = displayName;
    }

    public String getTenantId() { return tenantId; }
    public String getDisplayName() { return displayName; }
}