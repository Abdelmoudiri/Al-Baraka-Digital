package com.Elbaraka.baraka.service.impl;

import com.Elbaraka.baraka.entity.Role;
import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.exception.BusinessException;
import com.Elbaraka.baraka.exception.EmailAlreadyExistsException;
import com.Elbaraka.baraka.exception.ResourceNotFoundException;
import com.Elbaraka.baraka.repository.RoleRepository;
import com.Elbaraka.baraka.repository.UserRepository;
import com.Elbaraka.baraka.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository u, RoleRepository roleRepository)
    {
        this.userRepository=u;
        this.roleRepository=roleRepository;
    }


    @Override
    public User createUser(User user) {
        if(userRepository.existsByEmail(user.getEmail()))
        {
            throw new EmailAlreadyExistsException("mail already exist");
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("the Id Not found"));
    }

    @Override
    public User getUserByEmail(String email) {
        if (!userRepository.existsByEmail(email))
        {
            throw new ResourceNotFoundException("email not found");
        }

        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("user not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User user) {
        User updated=getUserById(id);
        updated.setFullName(user.getFullName());
        updated.setEmail(user.getEmail());
        updated.setCreatedAt(user.getCreatedAt());
        updated.setActive(user.getActive());
        return userRepository.save(updated);
    }

    @Override
    public void deactivateUser(Long id) {

        User activated=getUserById(id);
        activated.setActive(false);
        userRepository.save(activated);
    }

    @Override
    public void changeUserRole(Long id, String roleName) {
        User updated = getUserById(id);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName));
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        updated.setRoles(roles);
        userRepository.save(updated);
    }
}
