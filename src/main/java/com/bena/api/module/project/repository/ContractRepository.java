package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Contract;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {

    Optional<Contract> findByProject(Project project);

    List<Contract> findByClient(User client);

    List<Contract> findByProvider(User provider);

    List<Contract> findByStatus(Contract.ContractStatus status);

    @Query("SELECT c FROM Contract c " +
            "JOIN FETCH c.project p " +
            "JOIN FETCH c.client cl " +
            "JOIN FETCH c.provider pr " +
            "WHERE (cl = :user OR pr = :user)")
    List<Contract> findByClientOrProvider(@Param("user") User user);

    @Query("SELECT c FROM Contract c WHERE (c.client = :user OR c.provider = :user) AND c.status = :status")
    List<Contract> findByClientOrProviderAndStatus(@Param("user") User user, @Param("status") Contract.ContractStatus status);

    Long countByStatus(Contract.ContractStatus status);

    @Query("SELECT COUNT(c) FROM Contract c WHERE c.clientSigned = true AND c.providerSigned = true")
    Long countFullySigned();
}
