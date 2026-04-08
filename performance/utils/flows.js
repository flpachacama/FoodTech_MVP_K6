import { check, group, sleep } from 'k6';
import { resolveAuthToken } from './auth.js';
import { requestJson } from './http-client.js';

function withAuthHeaders(baseHeaders, token) {
  if (!token) {
    return baseHeaders;
  }

  return {
    ...baseHeaders,
    Authorization: `Bearer ${token}`,
  };
}

export function runApiJourney({ config, user, payload }) {
  // End-to-end API journey that models a realistic consumer request lifecycle.
  let journeyOk = true;
  let orderId = null;
  let repartidorId = null;
  let headers = { ...config.headers };

  group('Login (opcional)', () => {
    const token = resolveAuthToken(config, user);
    headers = withAuthHeaders(headers, token);
  });

  group('Consultar restaurantes', () => {
    const result = requestJson({
      method: 'GET',
      url: `${config.orderBaseUrl}/restaurants`,
      headers,
      expectedStatus: 200,
      timeout: config.timeout,
      tags: { flow: 'catalog', endpoint: 'restaurants_list' },
    });

    const businessCheckOk = check(result.data, {
      'restaurants payload is an array': (data) => Array.isArray(data),
    });

    journeyOk = journeyOk && result.ok && businessCheckOk;
  });

  group('Consultar restaurante por ID', () => {
    const result = requestJson({
      method: 'GET',
      url: `${config.orderBaseUrl}/restaurants/${payload.restauranteId}`,
      headers,
      expectedStatus: 200,
      timeout: config.timeout,
      tags: { flow: 'catalog', endpoint: 'restaurants_get' },
    });

    const businessCheckOk = check(result.data, {
      'restaurant id is present': (data) => Boolean(data && data.id),
      'restaurant has menu': (data) => Boolean(data && Array.isArray(data.menu)),
    });

    journeyOk = journeyOk && result.ok && businessCheckOk;
  });

  group('Crear pedido', () => {
    const result = requestJson({
      method: 'POST',
      url: `${config.orderBaseUrl}/orders`,
      body: payload,
      headers,
      expectedStatus: [200, 201],
      timeout: config.timeout,
      tags: { flow: 'order', endpoint: 'orders_create' },
    });

    orderId = result.data && result.data.id;
    repartidorId = result.data && result.data.repartidorId;

    const businessCheckOk = check(result.data, {
      'order id exists': (data) => Boolean(data && data.id),
      'order status is ASIGNADO': (data) => Boolean(data && data.estado === 'ASIGNADO'),
    });

    journeyOk = journeyOk && result.ok && businessCheckOk;
  });

  group('Consultar pedido por repartidor', () => {
    if (!repartidorId) {
      journeyOk = false;
      return;
    }

    const result = requestJson({
      method: 'GET',
      url: `${config.orderBaseUrl}/orders/repartidor/${repartidorId}`,
      headers,
      expectedStatus: 200,
      timeout: config.timeout,
      tags: { flow: 'order', endpoint: 'orders_by_driver' },
    });

    const businessCheckOk = check(result.data, {
      'order linked to same repartidor': (data) => Boolean(data && data.repartidorId === repartidorId),
    });

    journeyOk = journeyOk && result.ok && businessCheckOk;
  });

  group('Consultar repartidores (delivery-service)', () => {
    const result = requestJson({
      method: 'GET',
      url: `${config.deliveryBaseUrl}/delivers`,
      headers,
      expectedStatus: 200,
      timeout: config.timeout,
      tags: { flow: 'delivery', endpoint: 'delivers_list' },
    });

    const businessCheckOk = check(result.data, {
      'delivers payload is an array': (data) => Array.isArray(data),
    });

    journeyOk = journeyOk && result.ok && businessCheckOk;
  });

  group('Cancelar pedido para limpieza', () => {
    if (!config.enableCleanup || !orderId) {
      return;
    }

    const result = requestJson({
      method: 'PUT',
      url: `${config.orderBaseUrl}/orders/${orderId}/cancel`,
      headers,
      expectedStatus: [200, 204],
      timeout: config.timeout,
      tags: { flow: 'cleanup', endpoint: 'orders_cancel' },
    });

    journeyOk = journeyOk && result.ok;
  });

  sleep(config.defaultSleepSeconds);

  return {
    ok: journeyOk,
    orderId,
    repartidorId,
  };
}
