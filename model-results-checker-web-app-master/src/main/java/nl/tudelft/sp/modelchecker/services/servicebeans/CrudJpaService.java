package nl.tudelft.sp.modelchecker.services.servicebeans;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import nl.tudelft.sp.modelchecker.dto.Dto;
import nl.tudelft.sp.modelchecker.entities.SuperEntity;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract class that forms the foundation of the strategy pattern.
 *
 * @param <T> Type T
 * @param <K> PrimaryKey type K
 * @param <D> Dto type D
 */
public abstract class CrudJpaService<T extends SuperEntity<K>, K, D extends Dto<K>>
        implements CrudService<T, K, D> {

    private final JpaRepository<T, K> repository;

    /**
     * Initialize a CrudJpaRepository.
     *
     * @param repository repository
     */
    public CrudJpaService(JpaRepository<T, K> repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all entities of this type from the database.
     *
     * @return a list of these entities
     */
    @Override
    public List<T> findAll() {
        return repository.findAll();
    }


    /**
     * save list of entities.
     *
     * @param entities entities
     */
    public void saveAll(List<T> entities) {
        repository.saveAll(entities);
    }


    /**
     * Retrieves the entity with this id.
     *
     * @param id of the entity looked for
     * @return the entity if found or else null
     */
    @Override
    public T findById(K id) {
        assert (id != null);
        return repository.findById(id).orElse(null);
    }


    /**
     * Looks if this type based on the equals method exists in the database.
     *
     * @param type to look for
     * @return true if one identical type was found
     */
    @Override
    public boolean exists(T type) {
        if (type.getId() == null) return false;
        return findById(type.getId()) != null;
    }

    /**
     * Tests if an entity with the same id as the dto already exists.
     *
     * @param dto of the entity with the id looked for
     * @return true if found
     */
    @Override
    public boolean exists(D dto) {
        if (dto.getId() == null) return false;
        return findById(dto.getId()) != null;
    }


    /**
     * Adds a new Entity to the database.
     *
     * @param type entity to add to the database
     * @return the newly created entity
     * @throws ExistsException throws when the entity already exist.
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public T register(T type) throws ExistsException {

        if (exists(type)) throw new ExistsException("Entity already exists");

        return save(type);
    }

    /**
     * Created/updates type.
     *
     * @param type to save
     * @return the newly created/updated type
     */
    protected T save(T type) {
        return repository.save(type);
    }


    /**
     * Updates entry in database using a new dto.
     * Implementation should be provided in the child classes
     *
     * @param oldDto dto with the id of the old entity
     * @param newDto dto with the parameters to change, if some are null ignore those
     * @return the created/updated enitiy
     * @throws NotFoundException if the entity wasn't found
     */
    @Override
    public abstract T update(D oldDto, D newDto) throws NotFoundException;


    /**
     * Deletes entry from database using it's id.
     *
     * @param id to delete
     * @throws NotFoundException if this entry wasn't found
     */
    @Override
    @Transactional(rollbackFor = {NotFoundException.class}, propagation = Propagation.REQUIRED)
    public void deleteById(K id) throws NotFoundException {
        T type = repository.findById(id).orElse(null);

        if (type == null) throw new NotFoundException("Entity not Found");

        repository.deleteById(id);
        assert (findById(id) == null);
    }

    /**
     * Deletes entry from database using it's id inside a dto.
     *
     * @param dto which contains the id
     * @throws NotFoundException if this entry wasn't found
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public void delete(D dto) throws NotFoundException {
        if (dto == null) throw new NotFoundException("Dto not Found");
        if (dto.getId() == null) throw new NotFoundException("Dto id not Found");

        deleteById(dto.getId());

        assert (findById(dto.getId()) == null);
    }


    /**
     * Entry counter.
     *
     * @return number of elements in the database
     */
    @Override
    public int count() {
        return (int) repository.count();
    }


    /**
     * Retreive all dtos from the database of class clazz.
     *
     * @param clazz class
     * @return list of dtos
     * @throws NotFoundException NotFoundException
     */
    @Override
    public List<D> findAllDtos(Class<D> clazz) throws NotFoundException {
        List<T> list = findAll();
        if (list == null) throw new NotFoundException("Not Found");
        return list.stream().map(n -> getInstance(clazz, n)).collect(Collectors.toList());
    }

    /**
     * Get instance from class.
     *
     * @param clazz clazz
     * @param n     n
     * @return Dto d
     */
    @SneakyThrows
    private D getInstance(Class<D> clazz, T n) {
        Constructor<D> cons = clazz.getConstructor(n.getClass());
        return cons.newInstance(n);
    }

    /**
     * Delete all entities from list.
     *
     * @param entities entities
     */
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

}
