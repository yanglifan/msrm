package cn.com.janssen.dsr.repository;

import java.util.List;

import cn.com.janssen.dsr.domain.MedicineComment;
import cn.com.janssen.dsr.domain.VisitRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineCommentRepository extends CrudRepository<MedicineComment, Long> {
    List<MedicineComment> findAllByVisitRecord(VisitRecord visitRecord);

    void deleteAllByVisitRecord(VisitRecord visitRecord);
}
