package cn.com.janssen.dsr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
public class User {

    @Id
    private String username;

    @Column
    private String displayName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "roles")
    private String roles;

    @ManyToOne(fetch = FetchType.EAGER)
    private User manager;

    @Column
    private String province;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public List<Role> getRoles() {
        List<Role> roleList = new ArrayList<>();
        if (roles != null) {
            for (String roleStr : roles.split(",")) {
                roleList.add(Role.valueOf(roleStr));
            }
        }
        return roleList;
    }

    public void setRoles(List<Role> roles) {
        StringBuilder rolesBuilder = new StringBuilder();
        roles.forEach(role -> rolesBuilder.append(role.name()).append(","));
        String value = rolesBuilder.toString();

        this.roles = value.substring(0, value.lastIndexOf(","));
    }

    public boolean isManager() {
        return getRoles() != null && getRoles().contains(Role.MANAGER);
    }

    @JsonIgnore
    public boolean isRepresentative() {
        return getRoles() != null && getRoles().contains(Role.DSR);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
