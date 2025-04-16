package co.ats.wepapp.service;

import co.ats.wepapp.domain.Folder;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.ats.wepapp.domain.Folder}.
 */
public interface FolderService {
    /**
     * Save a folder.
     *
     * @param folder the entity to save.
     * @return the persisted entity.
     */
    Folder save(Folder folder);

    /**
     * Updates a folder.
     *
     * @param folder the entity to update.
     * @return the persisted entity.
     */
    Folder update(Folder folder);

    /**
     * Partially updates a folder.
     *
     * @param folder the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Folder> partialUpdate(Folder folder);

    /**
     * Get all the folders.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Folder> findAll(Pageable pageable);

    /**
     * Get the "id" folder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Folder> findOne(Long id);

    /**
     * Delete the "id" folder.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
