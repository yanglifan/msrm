package cn.com.janssen.dsr.web;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import cn.com.janssen.dsr.domain.MedicineComment;
import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.domain.VisitRecord;
import cn.com.janssen.dsr.exception.ResourceNotFoundException;
import cn.com.janssen.dsr.repository.DoctorRepository;
import cn.com.janssen.dsr.repository.MedicineCommentRepository;
import cn.com.janssen.dsr.repository.VisitRecordRepository;
import cn.com.janssen.dsr.security.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
public class VisitRecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitRecordController.class);

    private static final int DEFAULT_SIZE = 10;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VisitRecordRepository visitRecordRepository;

    @Autowired
    private MedicineCommentRepository medicineCommentRepository;

    @Autowired
    private SecurityManager securityManager;

    /**
     * @param page Start from 1
     */
    @RequestMapping(method = RequestMethod.GET)
    public Page<VisitRecord> list(@RequestParam("page") int page) {
        Pageable pageable = new PageRequest(page - 1, DEFAULT_SIZE, Sort.Direction.DESC, "visitAt");

        User loginUser = securityManager.currentUser();

        if (loginUser.isManager()) {
            LOGGER.debug("Query visit records as a manager");
            return query(() -> visitRecordRepository.findAllByManager(loginUser, pageable));
        }

        LOGGER.debug("Query visit records as a representative");
        return query(() -> visitRecordRepository.findAllByDsr(loginUser, pageable));
    }

    private Page<VisitRecord> query(Supplier<Page<VisitRecord>> supplier) {
        Page<VisitRecord> records = supplier.get();
        // Disable the cyclic reference between VisitRecord and MedicineComments
        records.getContent().forEach(visitRecord -> visitRecord.setMedicineComments(null));
        return records;
    }

    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public VisitRecord saveOrUpdate(@RequestBody VisitRecord visitRecord) {
        if (visitRecord.getId() != null) {
            LOGGER.debug("Edit the visit record {}", visitRecord.getId());
            VisitRecord existedVisitRecord = visitRecordRepository.findOne(visitRecord.getId());

            removeComments(existedVisitRecord.getMedicineComments(), visitRecord.getMedicineComments());

            existedVisitRecord.setVisitAt(visitRecord.getVisitAt());
            existedVisitRecord.setMedicineComments(visitRecord.getMedicineComments());

            visitRecord = existedVisitRecord;
        } else {
            LOGGER.debug("Create a new visit record");
        }

        visitRecord.setDsr(securityManager.currentUser());
        visitRecord.setDoctor(doctorRepository.findByName(visitRecord.getDoctor().getName()));

        List<MedicineComment> commentList = new ArrayList<>(visitRecord.getMedicineComments());
        VisitRecord saved = visitRecordRepository.save(visitRecord);

        commentList.forEach(comment -> {
            comment.setVisitRecord(saved);
            LOGGER.debug("Save a medicine comment {}, {}", comment.getId(), comment.getMedicine().getName());
            medicineCommentRepository.save(comment);
        });

        LOGGER.debug("VisitRecord {} has been saved successfully", visitRecord.getId());

        return visitRecord;
    }

    private void removeComments(List<MedicineComment> oldCommentList, List<MedicineComment> newCommentList) {
        oldCommentList.forEach(oldComment -> {
            LOGGER.debug("Query whether the old comment {} is existed in the new comment list", oldComment.getId());
            if (!newCommentList.contains(oldComment)) {
                LOGGER.debug("The old comment {} is not existed in the new comment list, so remove it",
                        oldComment.getId());
                medicineCommentRepository.delete(oldComment);
            }
        });
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public VisitRecord get(@PathVariable("id") Long id) {
        VisitRecord visitRecord = visitRecordRepository.findOne(id);
        if (visitRecord == null) {
            throw new RuntimeException("Record cannot found");
        }

        return visitRecord;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@PathVariable("id") Long id) {
        if (!visitRecordRepository.exists(id)) {
            throw new ResourceNotFoundException("Visit Record " + id + " cannot be found.");
        }

        LOGGER.debug("Try to delete all medicine comments which belong to the visit record {}", id);
        medicineCommentRepository.deleteAllByVisitRecord(visitRecordRepository.findOne(id));

        LOGGER.info("Try to delete the visit record {}", id);
        visitRecordRepository.delete(id);

        LOGGER.info("Delete the visit record {} successfully", id);
    }
}
