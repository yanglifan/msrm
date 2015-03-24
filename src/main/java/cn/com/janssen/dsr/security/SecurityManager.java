package cn.com.janssen.dsr.security;

import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityManager {
    @Autowired
    private UserRepository userRepository;

    public cn.com.janssen.dsr.domain.User currentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findOne(user.getUsername());
    }
}
