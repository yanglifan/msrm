package cn.com.janssen.dsr.web;

import cn.com.janssen.dsr.Configuration;
import cn.com.janssen.dsr.domain.Role;
import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.repository.UserRepository;
import cn.com.janssen.dsr.security.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private UserRepository userRepository;

    @Secured("MANAGER")
    @Transactional
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public User create(@RequestBody User user) {
        LOGGER.debug("Try to create a user {}", user.getUsername());
        User manager = securityManager.currentUser();
        user.setManager(manager);
        user.setRoles(Arrays.asList(Role.DSR));
        user = userRepository.save(user);
        LOGGER.debug("User {} has been created success", user.getUsername());
        return user;
    }

    @Secured("MANAGER")
//    @PreAuthorize("hasRole('MANAGER')")
    @Transactional(readOnly = true)
    @RequestMapping(method = RequestMethod.GET)
    public Page<User> list(int page) {
        User manager = securityManager.currentUser();
        LOGGER.debug("Try to list all representatives managed by {}", manager.getUsername());
        return userRepository.findAllByManager(manager, new PageRequest(page - 1, Configuration.defaultPageSize));
    }
}
