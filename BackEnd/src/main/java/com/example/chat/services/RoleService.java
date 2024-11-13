package com.example.chat.services;

import org.springframework.stereotype.Service;

import com.example.chat.models.Role;
import com.example.chat.repositories.RoleRepository;

@Service

public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public Role getByRole(String role) {
        return roleRepository.findByRole(role).orElse(null);
    }

}
