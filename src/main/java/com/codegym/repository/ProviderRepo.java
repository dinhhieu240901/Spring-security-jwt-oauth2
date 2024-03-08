package com.codegym.repository;

import com.codegym.model.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepo extends JpaRepository<Provider, Integer> {
}
