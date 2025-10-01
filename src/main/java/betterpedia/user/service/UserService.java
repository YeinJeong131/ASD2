package betterpedia.user.service;

import betterpedia.user.entity.Role;
import betterpedia.user.entity.User;
import betterpedia.user.repository.RoleRepository;
import betterpedia.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {
    private final UserRepository users;
    private final RoleRepository roles;

    public UserService(UserRepository users, RoleRepository roles) {
        this.users = users;
        this.roles = roles;
    }

    public User getById(Long id) {
        return users.findById(id).orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    // ===== Account (self-service) =====
    @Transactional
    public User updateProfile(Long userId, String email) {
        User u = getById(userId);
        if (!u.getEmail().equalsIgnoreCase(email) && users.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        u.setEmail(email);
        return users.save(u);
    }

    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User u = getById(userId);
        u.setPassword(newPassword); // hash later when enabling security
        users.save(u);
    }

    // ===== Admin ops =====
    public List<User> listAll() {
        return users.findAll();
    }

    @Transactional
    public User createUser(String email, String password, Collection<String> roleNames, boolean enabled) {
        if (users.existsByEmail(email)) throw new IllegalArgumentException("Email already in use");
        User u = new User();
        u.setEmail(email);
        u.setPassword(password); // hash later
        u.setEnabled(enabled);
        u.setRoles(resolveRoles(roleNames));
        return users.save(u);
    }

    @Transactional
    public User setEnabled(Long userId, boolean enabled) {
        User u = getById(userId);
        u.setEnabled(enabled);
        return users.save(u);
    }

    @Transactional
    public User updateRoles(Long userId, Collection<String> roleNames) {
        User u = getById(userId);
        u.setRoles(resolveRoles(roleNames));
        return users.save(u);
    }

    @Transactional
    public void deleteUser(Long userId) {
        users.deleteById(userId);
    }

    @Transactional
    public boolean toggleEnabled(Long userId) {
        User u = getById(userId);
        boolean newVal = !u.isEnabled();
        u.setEnabled(newVal);
        users.save(u);
        return newVal;
    }

    // ===== Helpers =====
    private Set<Role> resolveRoles(Collection<String> roleNames) {
        Set<Role> out = new HashSet<>();
        if (roleNames == null) return out;
        for (String raw : roleNames) {
            String name = normalizeRole(raw);
            Role r = roles.findByName(name).orElseGet(() -> {
                Role nr = new Role();
                nr.setName(name);
                return roles.save(nr);
            });
            out.add(r);
        }
        return out;
    }

    private String normalizeRole(String s) {
        if (s == null) return "ROLE_USER";
        String n = s.trim().toUpperCase(Locale.ROOT);
        if (!n.startsWith("ROLE_")) n = "ROLE_" + n;
        return n;
    }
}