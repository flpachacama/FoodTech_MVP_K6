import { group, sleep } from 'k6';
import { resolveAuthToken } from '../utils/auth.js';
import { requestJson } from '../utils/http-client.js';
import {
  assertDriversList,
  assertDriverActive,
  assertOrderAssigned,
  assertOrderCancelled,
  assertOrderForDriver,
  assertOrderPending,
  assertRestaurantsList,
} from '../utils/assertions.js';

export function createFlowContext(config, user, debug) {
  const token = resolveAuthToken(config, user);
  const headers = token
    ? { ...config.headers, Authorization: `Bearer ${token}` }
    : { ...config.headers };

  return {
    config,
    headers,
    debug,
  };
}

export function browseCatalogFlow(ctx, trace) {
  let ok = true;

  group('HU10 - consultar catalogo', () => {
    const restaurants = requestJson({
      method: 'GET',
      url: `${ctx.config.orderBaseUrl}/restaurants`,
      headers: ctx.headers,
      timeout: ctx.config.timeout,
      expectedStatus: 200,
      tags: { endpoint: 'restaurants_list' },
      trace,
      debug: ctx.debug,
    });

    const drivers = requestJson({
      method: 'GET',
      url: `${ctx.config.deliveryBaseUrl}/delivers`,
      headers: ctx.headers,
      timeout: ctx.config.timeout,
      expectedStatus: 200,
      tags: { endpoint: 'delivers_list' },
      trace,
      debug: ctx.debug,
    });

    ok = restaurants.ok && drivers.ok && assertRestaurantsList(restaurants.data) && assertDriversList(drivers.data);
  });

  const thinkTime = Number(ctx.config.thinkTimeSeconds || 0);
  if (thinkTime > 0) {
    sleep(thinkTime);
  }
  return { ok };
}

export function createOrderJourney(ctx, payload, trace, expectPending) {
  let ok = true;
  let orderId = null;
  let repartidorId = null;

  group('HU8/HU5 - confirmar y asignar pedido', () => {
    const created = requestJson({
      method: 'POST',
      url: `${ctx.config.orderBaseUrl}/orders`,
      body: payload,
      headers: ctx.headers,
      timeout: ctx.config.timeout,
      expectedStatus: [200, 201],
      tags: { endpoint: 'orders_create' },
      trace,
      debug: ctx.debug,
    });

    orderId = created.data && created.data.id;
    repartidorId = created.data && created.data.repartidorId;

    const expectedMode = expectPending === true ? 'pending' : expectPending === 'either' ? 'either' : 'assigned';

    if (expectedMode === 'pending') {
      ok = created.ok && assertOrderPending(created.data);
    } else if (expectedMode === 'either') {
      const estado = created.data && created.data.estado;
      if (estado === 'ASIGNADO') {
        ok = created.ok && assertOrderAssigned(created.data);
      } else if (estado === 'PENDIENTE') {
        ok = created.ok && assertOrderPending(created.data);
      } else {
        ok = false;
      }
    } else {
      ok = created.ok && assertOrderAssigned(created.data);
    }
  });

  const shouldValidateDriverOrder = expectPending !== true;

  if (shouldValidateDriverOrder && repartidorId) {
    group('HU8 - consultar pedido por repartidor', () => {
      const activeOrder = requestJson({
        method: 'GET',
        url: `${ctx.config.orderBaseUrl}/orders/repartidor/${repartidorId}`,
        headers: ctx.headers,
        timeout: ctx.config.timeout,
        expectedStatus: 200,
        tags: { endpoint: 'orders_by_driver' },
        trace,
        debug: ctx.debug,
      });

      ok = ok && activeOrder.ok && assertOrderForDriver(activeOrder.data, repartidorId);
    });
  }

  const thinkTime = Number(ctx.config.thinkTimeSeconds || 0);
  if (thinkTime > 0) {
    sleep(thinkTime);
  }
  return { ok, orderId, repartidorId };
}

export function cancelOrderFlow(ctx, orderId, repartidorId, trace) {
  let ok = true;

  if (!orderId) {
    return { ok: false };
  }

  group('HU9/HU6 - cancelar pedido y liberar repartidor', () => {
    const canceled = requestJson({
      method: 'PUT',
      url: `${ctx.config.orderBaseUrl}/orders/${orderId}/cancel`,
      headers: ctx.headers,
      timeout: ctx.config.timeout,
      expectedStatus: [200, 204],
      tags: { endpoint: 'orders_cancel' },
      trace,
      debug: ctx.debug,
    });

    ok = canceled.ok && assertOrderCancelled(canceled.data);

    if (repartidorId) {
      const driver = requestJson({
        method: 'GET',
        url: `${ctx.config.deliveryBaseUrl}/delivers/${repartidorId}`,
        headers: ctx.headers,
        timeout: ctx.config.timeout,
        expectedStatus: 200,
        tags: { endpoint: 'delivers_get' },
        trace,
        debug: ctx.debug,
      });

      ok = ok && driver.ok && assertDriverActive(driver.data);
    }
  });

  const thinkTime = Number(ctx.config.thinkTimeSeconds || 0);
  if (thinkTime > 0) {
    sleep(thinkTime);
  }
  return { ok };
}
