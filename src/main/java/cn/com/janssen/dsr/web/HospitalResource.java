package cn.com.janssen.dsr.web;

import cn.com.janssen.dsr.domain.*;
import cn.com.janssen.dsr.repository.DoctorRepository;
import cn.com.janssen.dsr.repository.HospitalRepository;
import cn.com.janssen.dsr.security.*;
import cn.com.janssen.dsr.security.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalResource.class);

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SecurityManager securityManager;

    /**
     * If can find the current login DSR, only list hospitals which belong to the same province with this DSR.
     * If cannot find the current login DSR, list all hospitals.
     */
    @RequestMapping(method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public Iterable<Hospital> list() {
        User user = securityManager.currentUser();
        if (user.getRoles().contains(Role.DSR)) {
            return hospitalRepository.findByProvince(user.getProvince());
        }

        return hospitalRepository.findAll();
    }

    @RequestMapping(value = "{id}/doctors")
    @Transactional(readOnly = true)
    public Iterable<Doctor> listDoctors(@PathVariable("id") Long id) {
        LOGGER.debug("Hospital ID {}", id);
        Hospital hospital = hospitalRepository.findOne(id);
        LOGGER.debug("Try to query doctors belong to the hospital [{}]", hospital.getName());
        return doctorRepository.findAllByHospital(hospital);
    }
}
