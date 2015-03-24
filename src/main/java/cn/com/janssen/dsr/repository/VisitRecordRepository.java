package cn.com.janssen.dsr.repository;

import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.domain.VisitRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VisitRecordRepository extends PagingAndSortingRepository<VisitRecord, Long> {
    Page<VisitRecord> findAllByDsr(User rep, Pageable pageable);

    @Query("select vr from VisitRecord vr where vr.dsr.manager = ?1")
    Page<VisitRecord> findAllByManager(User manager, Pageable pageable);

    @Query("select new VisitRecord(vr, d, h) from VisitRecord vr left join vr.doctor d left join d.hospital h " +
            "where vr.dsr.manager = ?1 and vr.visitAt between ?2 and ?3")
    List<VisitRecord> findAllByManagerAndVisitAtBetween(User manager, Date start, Date end);
}
