package cn.com.janssen.dsr.repository;

import cn.com.janssen.dsr.domain.Hospital;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends CrudRepository<Hospital, Long> {
    Hospital findByName(String name);

    List<Hospital> findByProvince(String province);
}
