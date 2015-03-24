package cn.com.janssen.dsr.web;

import cn.com.janssen.dsr.Application;
import cn.com.janssen.dsr.TestUtils;
import cn.com.janssen.dsr.domain.Role;
import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.repository.MedicineCommentRepository;
import cn.com.janssen.dsr.repository.UserRepository;
import cn.com.janssen.dsr.repository.VisitRecordRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static cn.com.janssen.dsr.TestUtils.toJSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = Application.class)
public class UserControllerTest {
    Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MedicineCommentRepository medicineCommentRepository;

    @Autowired
    VisitRecordRepository visitRecordRepository;

    @Autowired
    UserRepository userRepository;

    User manager;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        manager = new User("manager", "password");
        manager.setEmail("manager@example.com");
        manager = userRepository.save(manager);

    }

    @After
    public void tearDown() throws Exception {
        medicineCommentRepository.deleteAll();
        visitRecordRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void create_a_user_normally() throws Exception {
        TestUtils.loginAs(manager);
        User rep = new User("alice", "password");
        rep.setEmail("alice@example.com");
        rep.setRoles(Arrays.asList(Role.DSR));
        LOGGER.debug("User JSON content {}", toJSON(rep));

        ResultActions actions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJSON(rep)));

        actions.andExpect(status().isOk());
    }
}
