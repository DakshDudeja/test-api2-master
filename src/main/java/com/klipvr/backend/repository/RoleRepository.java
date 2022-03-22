package com.klipvr.backend.repository;

import java.util.Optional;

import com.klipvr.backend.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klipvr.backend.models.ERole;
import org.springframework.web.bind.annotation.GetMapping;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
  Optional<Role> findByName(ERole name);

    @GetMapping("/all")
    default String allAccess()
    {
      Role role = new Role();
      role.setId(1);
      role.setName(ERole.ROLE_USER);
      save(role);
      Role role2 = new Role();
      role2.setId(2);
      role2.setName(ERole.ROLE_MODERATOR);
      save(role2);
      Role role3 = new Role();
      role3.setId(3);
      role3.setName(ERole.ROLE_ADMIN);
      save(role3);
      //
      return "Public Content.";
    }
}
