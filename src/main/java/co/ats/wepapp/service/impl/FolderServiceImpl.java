package co.ats.wepapp.service.impl;

import co.ats.wepapp.domain.Folder;
import co.ats.wepapp.repository.FolderRepository;
import co.ats.wepapp.service.FolderService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.ats.wepapp.domain.Folder}.
 */
@Service
@Transactional
public class FolderServiceImpl implements FolderService {

    private static final Logger LOG = LoggerFactory.getLogger(FolderServiceImpl.class);

    private final FolderRepository folderRepository;

    public FolderServiceImpl(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    public Folder save(Folder folder) {
        LOG.debug("Request to save Folder : {}", folder);
        return folderRepository.save(folder);
    }

    @Override
    public Folder update(Folder folder) {
        LOG.debug("Request to update Folder : {}", folder);
        return folderRepository.save(folder);
    }

    @Override
    public Optional<Folder> partialUpdate(Folder folder) {
        LOG.debug("Request to partially update Folder : {}", folder);

        return folderRepository
            .findById(folder.getId())
            .map(existingFolder -> {
                if (folder.getName() != null) {
                    existingFolder.setName(folder.getName());
                }
                if (folder.getOrder() != null) {
                    existingFolder.setOrder(folder.getOrder());
                }

                return existingFolder;
            })
            .map(folderRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Folder> findAll(Pageable pageable) {
        LOG.debug("Request to get all Folders");
        return folderRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Folder> findOne(Long id) {
        LOG.debug("Request to get Folder : {}", id);
        return folderRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Folder : {}", id);
        folderRepository.deleteById(id);
    }
}
