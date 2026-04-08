package com.foodtech.infrastructure.persistence.repository;

import com.foodtech.infrastructure.persistence.RestauranteJpaRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RestauranteJpaRepositoryTest {

    @Test
    void debeSerInterfazQueExtiendeJpaRepository() {
        assertTrue(RestauranteJpaRepository.class.isInterface());
        boolean extendsJpa = false;
        for (Class<?> iface : RestauranteJpaRepository.class.getInterfaces()) {
            if (iface.getSimpleName().equals("JpaRepository")) {
                extendsJpa = true;
                break;
            }
        }
        assertTrue(extendsJpa, "RestauranteJpaRepository debe extender JpaRepository");
    }

}
