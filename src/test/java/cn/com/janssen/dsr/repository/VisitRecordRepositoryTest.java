package cn.com.janssen.dsr.repository;

import cn.com.janssen.dsr.Application;
import cn.com.janssen.dsr.DevelopmentBootStrap;
import cn.com.janssen.dsr.domain.Doctor;
import cn.com.janssen.dsr.domain.Hospital;
import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.domain.VisitRecord;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
@ActiveProfiles("dev")
public class VisitRecordRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitRecordRepositoryTest.class);

    @Autowired
    private VisitRecordRepository visitRecordRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineCommentRepository medicineCommentRepository;

    @Before
    public void setUp() throws Exception {
        medicineCommentRepository.deleteAll();
        visitRecordRepository.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        medicineCommentRepository.deleteAll();
        visitRecordRepository.deleteAll();
    }

    @Test
    public void testFindAllByManager() throws Exception {
        // Given
        Hospital hospital = new Hospital("Peking Union Medical College Hospital");
        hospital = hospitalRepository.save(hospital);

        Doctor doctor = new Doctor("John", hospital);
        doctorRepository.save(doctor);

        User mgr = createUser("mgr", "password");
        mgr = userRepository.save(mgr);
        User rep1 = createUser("rep1", "password");
        rep1.setManager(mgr);
        rep1 = userRepository.save(rep1);
        User rep2 = createUser("rep2", "password");
        rep2.setManager(mgr);
        rep2 = userRepository.save(rep2);
        User rep3 = createUser("rep3", "password");
        rep3 = userRepository.save(rep3);

        addVisitTenRecordsFor(doctor, rep1); // Belong to mgr
        addVisitTenRecordsFor(doctor, rep2); // Belong to mgr
        addVisitTenRecordsFor(doctor, rep3); // Not belong to mgr

        // When
        LOGGER.info("Execute VisitRecordRepository.findAllByManager");
        Page<VisitRecord> result = visitRecordRepository.findAllByManager(mgr, new PageRequest(0, 10));

        // Then
        assertThat(result.getSize(), is(10));
        assertThat(result.getTotalElements(), is(20L));
        assertThat(visitRecordRepository.count(), is(30L));
    }

    private void addVisitTenRecordsFor(Doctor doctor, User user) {
        for (int i = 0; i < 10; i++) {
            VisitRecord visitRecord = new VisitRecord();
            visitRecord.setDsr(user);
            visitRecord.setDoctor(doctor);
            visitRecord.setVisitAt(DateTime.now().toDate());
            visitRecordRepository.save(visitRecord);
        }
    }

    private User createUser(String username, String password) {
        User user = new User(username, password);
        user.setEmail(username + "@example.com");
        return user;
    }
}
