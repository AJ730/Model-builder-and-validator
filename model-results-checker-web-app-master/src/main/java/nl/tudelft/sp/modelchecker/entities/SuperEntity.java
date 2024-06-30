package nl.tudelft.sp.modelchecker.entities;

public interface SuperEntity<K> {
    /**
     * Get ID of the implemented interface.
     *
     * @return id
     */
    K getId();
}
