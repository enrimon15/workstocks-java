package it.workstocks.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import it.workstocks.entity.user.admin.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
