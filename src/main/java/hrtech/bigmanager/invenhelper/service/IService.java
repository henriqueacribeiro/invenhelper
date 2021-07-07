package hrtech.bigmanager.invenhelper.service;

import hrtech.bigmanager.invenhelper.model.Domain;
import hrtech.bigmanager.invenhelper.model.DomainKey;

import java.util.Optional;

/**
 * Interface that defines default services behaviour
 *
 * @param <E> Entity to be managed
 * @param <K> Key of the entity to be managed
 */
public interface IService<E extends Domain<E, K>, K extends DomainKey<K>> {

    /**
     * Method that creates a new entity
     *
     * @param objectToSave entity to be created
     * @return entity created
     */
    boolean insert(E objectToSave);

    /**
     * Method that updates an entity
     *
     * @param objectToSave entity to be updated
     * @return entity updated
     */
    boolean save(E objectToSave);

    /**
     * Method that, given a key object, returns the corresponding Entity on an Optional
     *
     * @param keyToSearch valid object to search the entity
     * @return Option that contains the entity, if such ID exists on the database
     */
    Optional<E> findById(Object keyToSearch);


}
