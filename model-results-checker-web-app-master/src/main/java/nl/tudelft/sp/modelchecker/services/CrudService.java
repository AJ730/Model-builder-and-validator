package nl.tudelft.sp.modelchecker.services;

import java.util.List;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.dto.Dto;
import nl.tudelft.sp.modelchecker.entities.SuperEntity;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;

public interface CrudService<T extends SuperEntity<K>, K, D extends Dto<K>> {

    /**
     * Find by an id.
     *
     * @param id id
     * @return new Type T
     */
    T findById(K id);

    /**
     * Find all types.
     *
     * @return List of Type T
     */
    List<T> findAll();

    /**
     * Method to check if a dto exists.
     *
     * @param dto dto
     * @return boolean
     */
    boolean exists(D dto);

    /**
     * Method to check if a type exists.
     *
     * @param type type
     * @return boolean
     */
    boolean exists(T type);

    /**
     * save list of entities.
     *
     * @param entities entities
     */
    void saveAll(List<T> entities);

    /**
     * Method to register a type.
     *
     * @param type type
     * @return type
     * @throws ExistsException ExistsException
     */
    T register(T type) throws ExistsException;

    /**
     * Method to update a oldDto to newDto.
     *
     * @param oldDto oldDto
     * @param newDto newDto
     * @return type T
     * @throws NotFoundException NotFoundException
     */
    T update(D oldDto, D newDto) throws NotFoundException;

    /**
     * Method to deleteById.
     *
     * @param id id
     * @throws NotFoundException NotFoundException
     */
    void deleteById(K id) throws NotFoundException;

    /**
     * Method to delete Dto.
     *
     * @param dto dto
     * @throws NotFoundException NotFoundException
     */
    void delete(D dto) throws NotFoundException;

    /**
     * Method to count number of instances in repo.
     *
     * @return int
     */
    int count();

    /**
     * Get List of dtos.
     *
     * @param clazz class
     * @return List of dtos
     * @throws NotFoundException NotFoundException
     */
    List<D> findAllDtos(Class<D> clazz) throws NotFoundException;

    /**
     * Delete all entities from list.
     *
     * @param entities entities
     */
    void deleteAll(Iterable<? extends T> entities);

}
