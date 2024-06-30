package nl.tudelft.sp.modelchecker.controller;

import java.util.List;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import nl.tudelft.sp.modelchecker.dto.Dto;
import nl.tudelft.sp.modelchecker.entities.SuperEntity;
import nl.tudelft.sp.modelchecker.services.CrudService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public abstract class AbstractController<I, T extends CrudService<K, I, D>,
        K extends SuperEntity<I>, D extends Dto<I>> {

    protected final T service;
    protected final ModelMapper modelMapper;

    /**
     * Instantiate Abstract Controller.
     *
     * @param service     service
     * @param modelMapper modelMapper
     */
    public AbstractController(T service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }


    /**
     * Prepare a entity from dto.
     *
     * @param dto    dto
     * @param entity entity
     * @return D
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    private D getSendBackDto(D dto, K entity) {
        return (D) dto.getClass().getConstructor(entity.getClass()).newInstance(entity);
    }


    /**
     * Get specific item for the controller.
     *
     * @param dto to retrieve from
     * @return item
     * @throws NotFoundException not found Exception
     */
    public ResponseEntity<D> getSpecific(D dto) throws NotFoundException {
        K entity = service.findById(dto.getId());
        if (entity == null) throw new NotFoundException("Not Found");

        D sendBackDto = getSendBackDto(dto, entity);

        return new ResponseEntity<>(sendBackDto, HttpStatus.OK);
    }

    /**
     * Get a list of items.
     *
     * @param c c
     * @return List of items
     * @throws NotFoundException NotFoundException
     */
    public ResponseEntity<List<D>> list(Class<D> c) throws NotFoundException {
        List<D> listDtos = service.findAllDtos(c);
        return new ResponseEntity<>(listDtos, HttpStatus.OK);
    }

    /**
     * Update an item.
     *
     * @param dto dto
     * @return updated item
     * @throws NotFoundException NotFoundException
     */
    public ResponseEntity<D> update(D dto) throws NotFoundException {
        K newUpdated = service.update(dto, dto);
        D sendBackDto = getSendBackDto(dto, newUpdated);

        return new ResponseEntity<>(sendBackDto, HttpStatus.OK);
    }

    /**
     * Delete an item.
     *
     * @param dto dto
     * @return deleted dto
     * @throws NotFoundException NotFoundException
     */
    public ResponseEntity<D> delete(D dto) throws NotFoundException {
        service.delete(dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
