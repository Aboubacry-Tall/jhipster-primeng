package co.ats.wepapp.web.rest;

import co.ats.wepapp.domain.Folder;
import co.ats.wepapp.repository.FolderRepository;
import co.ats.wepapp.service.FolderService;
import co.ats.wepapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.ats.wepapp.domain.Folder}.
 */
@RestController
@RequestMapping("/api/folders")
public class FolderResource {

    private static final Logger LOG = LoggerFactory.getLogger(FolderResource.class);

    private static final String ENTITY_NAME = "folder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FolderService folderService;

    private final FolderRepository folderRepository;

    public FolderResource(FolderService folderService, FolderRepository folderRepository) {
        this.folderService = folderService;
        this.folderRepository = folderRepository;
    }

    /**
     * {@code POST  /folders} : Create a new folder.
     *
     * @param folder the folder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new folder, or with status {@code 400 (Bad Request)} if the folder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Folder> createFolder(@Valid @RequestBody Folder folder) throws URISyntaxException {
        LOG.debug("REST request to save Folder : {}", folder);
        if (folder.getId() != null) {
            throw new BadRequestAlertException("A new folder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        folder = folderService.save(folder);
        return ResponseEntity.created(new URI("/api/folders/" + folder.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, folder.getId().toString()))
            .body(folder);
    }

    /**
     * {@code PUT  /folders/:id} : Updates an existing folder.
     *
     * @param id the id of the folder to save.
     * @param folder the folder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated folder,
     * or with status {@code 400 (Bad Request)} if the folder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the folder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Folder> updateFolder(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Folder folder
    ) throws URISyntaxException {
        LOG.debug("REST request to update Folder : {}, {}", id, folder);
        if (folder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, folder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!folderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        folder = folderService.update(folder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, folder.getId().toString()))
            .body(folder);
    }

    /**
     * {@code PATCH  /folders/:id} : Partial updates given fields of an existing folder, field will ignore if it is null
     *
     * @param id the id of the folder to save.
     * @param folder the folder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated folder,
     * or with status {@code 400 (Bad Request)} if the folder is not valid,
     * or with status {@code 404 (Not Found)} if the folder is not found,
     * or with status {@code 500 (Internal Server Error)} if the folder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Folder> partialUpdateFolder(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Folder folder
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Folder partially : {}, {}", id, folder);
        if (folder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, folder.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!folderRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Folder> result = folderService.partialUpdate(folder);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, folder.getId().toString())
        );
    }

    /**
     * {@code GET  /folders} : get all the folders.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of folders in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Folder>> getAllFolders(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Folders");
        Page<Folder> page = folderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /folders/:id} : get the "id" folder.
     *
     * @param id the id of the folder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the folder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Folder> getFolder(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Folder : {}", id);
        Optional<Folder> folder = folderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(folder);
    }

    /**
     * {@code DELETE  /folders/:id} : delete the "id" folder.
     *
     * @param id the id of the folder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Folder : {}", id);
        folderService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
