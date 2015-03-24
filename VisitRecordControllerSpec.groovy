package cn.com.janssen.dsr.web

import cn.com.janssen.dsr.Application
import cn.com.janssen.dsr.TestUtils
import cn.com.janssen.dsr.domain.Doctor
import cn.com.janssen.dsr.domain.MedicineComment
import cn.com.janssen.dsr.domain.User
import cn.com.janssen.dsr.domain.VisitRecord
import cn.com.janssen.dsr.repository.DoctorRepository
import cn.com.janssen.dsr.repository.MedicineCommentRepository
import cn.com.janssen.dsr.repository.UserRepository
import cn.com.janssen.dsr.repository.VisitRecordRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
class VisitRecordControllerSpec extends Specification {
    MockMvc mockMvc

    @Autowired
    WebApplicationContext webApplicationContext

    private static final String MANAGER = "mgr_for_test";
    private static final String REP1 = "rep1_for_test";
    private static final String REP2 = "rep2_for_test";

    private Doctor doctor;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VisitRecordRepository visitRecordRepository;

    @Autowired
    private MedicineCommentRepository medicineCommentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()

        medicineCommentRepository.deleteAll();
        visitRecordRepository.deleteAll();
        userRepository.deleteAll();
        doctorRepository.deleteAll();

        User manager = new User(MANAGER, "password");
        manager.setEmail(MANAGER + "@example.com");
        manager.setRoles(Arrays.asList(User.Role.REPRESENTATIVE, User.Role.MANAGER));
        userRepository.save(manager);

        User rep1 = new User(REP1, "password");
        rep1.setEmail(REP1 + "@example.com");
        rep1.setManager(manager);
        rep1.setRoles(Arrays.asList(User.Role.REPRESENTATIVE));
        userRepository.save(rep1);

        User rep2 = new User(REP2, "password");
        rep2.setEmail(REP2 + "@example.com");
        rep2.setManager(manager);
        rep2.setRoles(Arrays.asList(User.Role.REPRESENTATIVE));
        userRepository.save(rep2);

        doctor = doctorRepository.save(new Doctor("John", "MASH"));
    }

    def "try"() {
        given:
        TestUtils.loginAs(userRepository.findOne(MANAGER))

        User rep1 = userRepository.findOne(REP1);
        saveVisitRecord(rep1);
        saveVisitRecord(rep1);

        User rep2 = userRepository.findOne(REP2);
        saveVisitRecord(rep2);

        when:
        ResultActions actions = mockMvc.perform(get("/api/records?page=1"))

        then:
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath('$.numberOfElements', is(3)));
    }

    VisitRecord saveVisitRecord(User rep) {
        VisitRecord visitRecord = new VisitRecord();
        visitRecord.setDoctor(doctor);
        visitRecord.setVisitAt(new Date());
        visitRecord.setDsr(rep);

        visitRecord = visitRecordRepository.save(visitRecord);
        medicineCommentRepository.save(new MedicineComment(visitRecord, "Medicine1", "Medicine Comment XXX"));

        return visitRecord;
    }
}
