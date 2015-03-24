package cn.com.janssen.dsr.repository;

import cn.com.janssen.dsr.domain.Doctor;
import cn.com.janssen.dsr.domain.Hospital;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DoctorRepository extends PagingAndSortingRepository<Doctor, Long> {
    Doctor findByName(String name);

    Collection<Doctor> findAllByHospital(Hospital hospital);
}
