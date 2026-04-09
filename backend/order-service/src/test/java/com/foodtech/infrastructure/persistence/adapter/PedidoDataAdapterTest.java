package com.foodtech.infrastructure.persistence.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.domain.model.EstadoPedido;
import com.foodtech.domain.model.Pedido;
import com.foodtech.domain.model.ProductoPedido;
import com.foodtech.infrastructure.persistence.PedidoJpaRepository;
import com.foodtech.infrastructure.persistence.entity.PedidoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoDataAdapterTest {

    @Mock
    private PedidoJpaRepository pedidoJpaRepository;

    @InjectMocks
    private PedidoDataAdapter adapter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void debeGuardarYPasarARepository_cuandoPedidoValido() {
        ProductoPedido prod = new ProductoPedido(1L, "Pizza", null);
        Pedido pedido = new Pedido(100L, EstadoPedido.PENDIENTE, 10L, null, List.of(prod), 20L, "Cliente", 1.0, 2.0, 15);

        PedidoEntity returned = new PedidoEntity(100L, EstadoPedido.PENDIENTE, 10L, null, 20L, "Cliente", 1.0, 2.0, 15, "[{\"id\":1,\"nombre\":\"Pizza\",\"precio\":null}]");
        when(pedidoJpaRepository.save(any())).thenReturn(returned);

        Pedido saved = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).save(pedido);

        assertNotNull(saved);
        assertEquals(100L, saved.getId());
        assertEquals(1, saved.getProductos().size());
        verify(pedidoJpaRepository, times(1)).save(any());
    }

    @Test
    void debeSerializarProductosComoListaVacia_cuandoProductosNull() {
        Pedido pedido = new Pedido(101L, EstadoPedido.PENDIENTE, 11L, null, null, 21L, "Anon", null, null, 0);

        ArgumentCaptor<PedidoEntity> captor = ArgumentCaptor.forClass(PedidoEntity.class);
        when(pedidoJpaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        new PedidoDataAdapter(pedidoJpaRepository, objectMapper).save(pedido);

        verify(pedidoJpaRepository).save(captor.capture());
        PedidoEntity sent = captor.getValue();
        assertEquals("[]", sent.getProductos());
    }

    @Test
    void debeMapearDesdeRepository_cuandoExiste() {
        PedidoEntity entity = new PedidoEntity(200L, EstadoPedido.ASIGNADO, 30L, null, 40L, "C", 1.0, 2.0, 20, "[]");
        when(pedidoJpaRepository.findById(200L)).thenReturn(Optional.of(entity));

        Optional<Pedido> found = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findById(200L);

        assertTrue(found.isPresent());
        assertEquals(200L, found.get().getId());
    }

    @Test
    void debeMapearListaDesdeRepository_cuandoHayRegistros() {
        PedidoEntity e1 = new PedidoEntity(201L, EstadoPedido.PENDIENTE, 31L, null, 41L, "C2", null, null, 5, "[]");
        when(pedidoJpaRepository.findAll()).thenReturn(List.of(e1));

        List<Pedido> lista = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findAll();

        assertEquals(1, lista.size());
        assertEquals(201L, lista.get(0).getId());
    }

    @Test
    void debeLanzarIllegalStateException_cuandoSerializacionFalla_enSave() throws Exception {
        ProductoPedido prod = new ProductoPedido(2L, "Taco", null);
        Pedido pedido = new Pedido(102L, EstadoPedido.PENDIENTE, 12L, null, List.of(prod), 22L, "Cliente2", null, null, 10);

        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        when(mockMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("boom"){});

        PedidoDataAdapter adapterWithBadMapper = new PedidoDataAdapter(pedidoJpaRepository, mockMapper);

        assertThrows(IllegalStateException.class, () -> adapterWithBadMapper.save(pedido));
        verify(pedidoJpaRepository, never()).save(any());
    }

    @Test
    void debeLanzarIllegalStateException_cuandoDeserializacionFalla_enFindAll() throws Exception {
        PedidoEntity e1 = new PedidoEntity(300L, EstadoPedido.PENDIENTE, 31L, null, 41L, "C2", null, null, 5, "[{\"id\":1}]");
        when(pedidoJpaRepository.findAll()).thenReturn(List.of(e1));

        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        when(mockMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new JsonProcessingException("boom"){});

        PedidoDataAdapter adapterWithBadMapper = new PedidoDataAdapter(pedidoJpaRepository, mockMapper);

        assertThrows(IllegalStateException.class, adapterWithBadMapper::findAll);
    }

    @Test
    void findById_debeRetornarVacio_cuandoNoExiste() {
        when(pedidoJpaRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Pedido> r = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findById(999L);
        assertTrue(r.isEmpty());
    }

    @Test
    void deserialize_debeRetornarListaVacia_cuandoProductosBlank() {
        PedidoEntity e = new PedidoEntity(400L, EstadoPedido.PENDIENTE, 31L, null, 41L, "C2", null, null, 5, "   ");
        when(pedidoJpaRepository.findAll()).thenReturn(List.of(e));

        List<Pedido> lista = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findAll();
        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getProductos().isEmpty());
    }

    @Test
    void findPedidoActivoByRepartidorId_cuandoExiste_retornaPedido() {
        PedidoEntity entity = new PedidoEntity(500L, EstadoPedido.ASIGNADO, 10L, 7L, 20L, "Carlos", -74.06, 4.64, 20, "[]");
        when(pedidoJpaRepository.findFirstByRepartidorIdAndEstadoIn(
                eq(7L), anyList())).thenReturn(Optional.of(entity));

        Optional<Pedido> result = new PedidoDataAdapter(pedidoJpaRepository, objectMapper)
                .findPedidoActivoByRepartidorId(7L);

        assertTrue(result.isPresent());
        assertEquals(500L, result.get().getId());
        assertEquals(EstadoPedido.ASIGNADO, result.get().getEstado());
    }

    @Test
    void findPedidoActivoByRepartidorId_cuandoNoExiste_retornaVacio() {
        when(pedidoJpaRepository.findFirstByRepartidorIdAndEstadoIn(
                eq(99L), anyList())).thenReturn(Optional.empty());

        Optional<Pedido> result = new PedidoDataAdapter(pedidoJpaRepository, objectMapper)
                .findPedidoActivoByRepartidorId(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deserialize_debeRetornarListaVacia_cuandoProductosNull() {
        PedidoEntity e = new PedidoEntity(401L, EstadoPedido.PENDIENTE, 31L, null, 41L, "C3", null, null, 5, null);
        when(pedidoJpaRepository.findAll()).thenReturn(List.of(e));

        List<Pedido> lista = new PedidoDataAdapter(pedidoJpaRepository, objectMapper).findAll();
        assertEquals(1, lista.size());
        assertTrue(lista.get(0).getProductos().isEmpty());
    }

}
