package com.alan.whiskey_store_java_spring_boot_backend.repository;

import com.alan.whiskey_store_java_spring_boot_backend.entity.CustomerOrder;
import com.alan.whiskey_store_java_spring_boot_backend.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUserOrderByCreatedAtDesc(UserAccount user);
}
