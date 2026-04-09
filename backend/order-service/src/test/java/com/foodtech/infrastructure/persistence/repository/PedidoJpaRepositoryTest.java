package com.foodtech.infrastructure.persistence.repository;

import com.foodtech.infrastructure.persistence.PedidoJpaRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoJpaRepositoryTest {

    @Test
    void debeSerInterfazQueExtiendeJpaRepository() {
        assertTrue(PedidoJpaRepository.class.isInterface());
        boolean extendsJpa = false;
        for (Class<?> iface : PedidoJpaRepository.class.getInterfaces()) {
            if (iface.getSimpleName().equals("JpaRepository")) {
                extendsJpa = true;
                break;
            }
        }
        assertTrue(extendsJpa, "PedidoJpaRepository debe extender JpaRepository");
    }

}
