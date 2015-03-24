package cn.com.janssen.dsr;

import cn.com.janssen.dsr.domain.Doctor;
import cn.com.janssen.dsr.domain.Hospital;
import cn.com.janssen.dsr.domain.Medicine;
import cn.com.janssen.dsr.domain.MedicineComment;
import cn.com.janssen.dsr.domain.Role;
import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.domain.VisitRecord;
import cn.com.janssen.dsr.repository.DoctorRepository;
import cn.com.janssen.dsr.repository.HospitalRepository;
import cn.com.janssen.dsr.repository.MedicineCommentRepository;
import cn.com.janssen.dsr.repository.MedicineRepository;
import cn.com.janssen.dsr.repository.UserRepository;
import cn.com.janssen.dsr.repository.VisitRecordRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("dev")
public class DevelopmentBootStrap {
    public static final String PEKING_UNIVERSITY_FIRST_HOSPITAL = "Peking University First Hospital";
    public static final String PEKING_UNION_MEDICAL_COLLEGE_HOSPITAL = "Peking Union Medical College Hospital";

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentBootStrap.class);

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VisitRecordRepository visitRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MedicineCommentRepository medicineCommentRepository;

    @PostConstruct
    public void init() {
        LOGGER.debug("Init the database for the development");
        initUsersAndAuthorities();
        initDoctorsAndVisitRecords();
    }

    private void initDoctorsAndVisitRecords() {
        Hospital hospital = new Hospital(PEKING_UNIVERSITY_FIRST_HOSPITAL);
        hospital.setSerialNumber("sn0001");
        hospitalRepository.save(hospital);

        hospital = new Hospital(PEKING_UNION_MEDICAL_COLLEGE_HOSPITAL);
        hospital.setSerialNumber("sn0002");
        hospitalRepository.save(hospital);

        Doctor doctor = new Doctor("John", hospital);
        doctor.setDepartment("Surgery");
        doctor.setSerialNumber("doctor_sn_0001");
        doctor.setEmail("doctor@example.com");
        doctorRepository.save(doctor);

        List<Medicine> medicineList = medicineRepository.findAll();
        User user = userRepository.findOne("user");
        for (int i = 1; i <= 30; i++) {
            VisitRecord visitRecord = new VisitRecord();
            visitRecord.setDsr(user);
            visitRecord.setDoctor(doctor);
            visitRecord.setVisitAt(new DateTime(2014, 4, 1, 10, 0).toDate());
            visitRecord = visitRecordRepository.save(visitRecord);

            medicineCommentRepository.save(new MedicineComment(visitRecord, medicineList.get(0), "Good"));
            medicineCommentRepository.save(new MedicineComment(visitRecord, medicineList.get(1), "Bad"));
        }
    }

    private void initUsersAndAuthorities() {
        User manager = new User("manager", "password");
        manager.setEmail("manager@example.com");
        manager.setRoles(Arrays.asList(Role.MANAGER));
        manager = userRepository.save(manager);

        User user = new User("user", "password");
        user.setDisplayName("Zhang San");
        user.setEmail("user@example.com");
        user.setManager(manager);
        user.setRoles(Arrays.asList(Role.DSR));
        userRepository.save(user);
    }
}
