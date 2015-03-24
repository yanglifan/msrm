package cn.com.janssen.dsr.web;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.com.janssen.dsr.Application;
import cn.com.janssen.dsr.TestUtils;
import cn.com.janssen.dsr.domain.*;
import cn.com.janssen.dsr.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class VisitRecordControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisitRecordController.class);

    private static final String HOSPITAL_NAME = "Peking Union Medical College Hospital";

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
    private MedicineRepository medicineRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        medicineCommentRepository.deleteAll();
        visitRecordRepository.deleteAll();
        userRepository.deleteAll();
        doctorRepository.deleteAll();
        hospitalRepository.deleteAll();

        User manager = new User(MANAGER, "password");
        manager.setEmail(MANAGER + "@example.com");
        manager.setRoles(Arrays.asList(Role.DSR, Role.MANAGER));
        userRepository.save(manager);

        User rep1 = new User(REP1, "password");
        rep1.setEmail(REP1 + "@example.com");
        rep1.setManager(manager);
        rep1.setRoles(Arrays.asList(Role.DSR));
        userRepository.save(rep1);

        User rep2 = new User(REP2, "password");
        rep2.setEmail(REP2 + "@example.com");
        rep2.setManager(manager);
        rep2.setRoles(Arrays.asList(Role.DSR));
        userRepository.save(rep2);

        Hospital hospital = new Hospital("Peking Union Medical College Hospital");
        hospital = hospitalRepository.save(hospital);

        doctor = doctorRepository.save(new Doctor("John", hospital));
    }

    @Test
    public void create_record_with_two_medicine_comments() throws Exception {
        // Given
        TestUtils.loginAs(userRepository.findOne(REP1));

        VisitRecord visitRecord = createVisitRecord();

        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter jsonStringWriter = new StringWriter();
        objectMapper.writeValue(jsonStringWriter, visitRecord);

        // When
        mockMvc.perform(post("/api/records").contentType(MediaType.APPLICATION_JSON).content(jsonStringWriter.toString())).
                andExpect(status().isOk());

        // Then
        LOGGER.debug("Start execute findAllByDsr");
        Page<VisitRecord> records = visitRecordRepository.findAllByDsr(userRepository.findOne(REP1),
                new PageRequest(0, 10));
        assertNotNull(records.getContent().get(0).getDoctor());
        assertThat(records.getContent().get(0).getDoctor().getName(), is("John"));

        visitRecord = visitRecordRepository.findOne(records.getContent().get(0).getId());
        List<MedicineComment> comments = medicineCommentRepository.findAllByVisitRecord(visitRecord);
        assertThat(comments.size(), is(2));
    }

    private VisitRecord createVisitRecord() {
        VisitRecord visitRecord = new VisitRecord();
        visitRecord.setDoctor(doctorRepository.findByName("John"));
        visitRecord.setVisitAt(DateTime.now().toDate());

        Medicine medicine1 = medicineRepository.findByName("Sibelium");
        visitRecord.getMedicineComments().add(new MedicineComment(visitRecord, medicine1, "Medicine Comment XXX"));
        Medicine medicine2 = medicineRepository.findByName("Itraconazole");
        visitRecord.getMedicineComments().add(new MedicineComment(visitRecord, medicine2, "Medicine Comment YYY"));
        return visitRecord;
    }

    @Test
    public void list_records_as_manager() throws Exception {
        TestUtils.loginAs(userRepository.findOne(MANAGER));

        User rep1 = userRepository.findOne(REP1);
        saveVisitRecord(rep1);
        saveVisitRecord(rep1);

        User rep2 = userRepository.findOne(REP2);
        saveVisitRecord(rep2);

        mockMvc.perform(get("/api/records?page=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.numberOfElements", is(3)));
    }

    @Test
    public void list_records_as_rep() throws Exception {
        TestUtils.loginAs(userRepository.findOne(REP1));

        User rep1 = userRepository.findOne(REP1);
        saveVisitRecord(rep1);
        saveVisitRecord(rep1);

        User rep2 = userRepository.findOne(REP2);
        saveVisitRecord(rep2);

        mockMvc.perform(get("/api/records?page=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtils.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.numberOfElements", is(2)));
    }

    @Test
    public void test_edit() throws Exception {
        TestUtils.loginAs(userRepository.findOne(REP1));

        VisitRecord visitRecord = saveNewVisitRecordAndMedicineComments();

        Date now = new Date();
        visitRecord.setVisitAt(now);

        Medicine medicine = medicineRepository.findByName("Sibelium");
        visitRecord.getMedicineComments().add(new MedicineComment(visitRecord, medicine, "OK"));

        mockMvc.perform(post("/api/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.toJSON(visitRecord)))
                .andExpect(status().isOk());

        VisitRecord savedVisitRecord = visitRecordRepository.findOne(visitRecord.getId());

        List<MedicineComment> comments = medicineCommentRepository.findAllByVisitRecord(savedVisitRecord);
        assertThat(comments.size(), is(3));

        // Only need to accurate to the minute level
        assertThat(savedVisitRecord.getVisitAt().getTime() / 1000 / 60, is(now.getTime() / 1000 / 60));
    }

    private VisitRecord saveNewVisitRecordAndMedicineComments() {
        VisitRecord visitRecord = createVisitRecord();
        visitRecord = visitRecordRepository.save(visitRecord);
        visitRecord.getMedicineComments().forEach(comment -> medicineCommentRepository.save(comment));
        return visitRecord;
    }

    @Test
    public void test_delete() throws Exception {
        TestUtils.loginAs(userRepository.findOne(REP1));
        VisitRecord savedVisitRecord = saveNewVisitRecordAndMedicineComments();

        mockMvc.perform(delete("/api/records/" + savedVisitRecord.getId())).andExpect(status().isOk());

        List<MedicineComment> medicineCommentList = medicineCommentRepository.findAllByVisitRecord(savedVisitRecord);
        assertThat(medicineCommentList.size(), is(0));
    }

    private VisitRecord saveVisitRecord(User rep) {
        VisitRecord visitRecord = new VisitRecord();
        visitRecord.setDoctor(doctor);
        visitRecord.setVisitAt(DateTime.now().toDate());
        visitRecord.setDsr(rep);

        visitRecord = visitRecordRepository.save(visitRecord);
        medicineCommentRepository.save(new MedicineComment(visitRecord, "Medicine1", "Medicine Comment XXX"));

        return visitRecord;
    }
}
