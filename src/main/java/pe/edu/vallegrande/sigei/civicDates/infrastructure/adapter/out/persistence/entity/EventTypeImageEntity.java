package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_type_image")
public class EventTypeImageEntity implements Persistable<String> {

    @Id
    @Column("event_type")
    private String eventType;

    @Column("image_url")
    private String imageUrl;

    @Transient
    @Builder.Default
    private boolean isNewFlag = false;

    @Override
    public String getId() {
        return eventType;
    }

    @Override
    public boolean isNew() {
        return isNewFlag;
    }
    
    public void setNew(boolean isNew) {
        this.isNewFlag = isNew;
    }
}
