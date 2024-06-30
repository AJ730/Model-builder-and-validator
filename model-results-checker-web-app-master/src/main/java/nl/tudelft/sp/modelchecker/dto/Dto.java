package nl.tudelft.sp.modelchecker.dto;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nl.tudelft.sp.modelchecker.entities.SuperEntity;


@NoArgsConstructor
@Setter
@EqualsAndHashCode
@ToString
public class Dto<K> {
    protected K id;

    /**
     * Make a new Dto.
     *
     * @param id id
     */
    public Dto(K id) {
        this.id = id;
    }

    /**
     * Construct Dto from superEntity.
     *
     * @param entity entity
     */
    public Dto(SuperEntity<K> entity) {
        if (entity != null) this.id = entity.getId();
    }

    /**
     * Get id from Dto.
     *
     * @return id
     */
    public K getId() {
        return id;
    }

}
