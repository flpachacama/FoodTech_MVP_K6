import { check } from 'k6';

export function assertRestaurantsList(data) {
  return check(data, {
    'restaurants is array': (d) => Array.isArray(d),
    'restaurants has at least one row': (d) => Array.isArray(d) && d.length > 0,
    'restaurant row has menu': (d) => !Array.isArray(d) || d.length === 0 || Array.isArray(d[0].menu),
  });
}

export function assertDriversList(data) {
  return check(data, {
    'drivers is array': (d) => Array.isArray(d),
    'drivers has at least one row': (d) => Array.isArray(d) && d.length > 0,
    'driver row has estado and vehiculo': (d) => {
      if (!Array.isArray(d) || d.length === 0) {
        return true;
      }
      return Boolean(d[0].estado && d[0].vehiculo);
    },
  });
}

export function assertOrderAssigned(data) {
  return check(data, {
    'order id exists': (d) => Boolean(d && d.id),
    'order estado is ASIGNADO': (d) => Boolean(d && d.estado === 'ASIGNADO'),
    'order has repartidorId': (d) => Boolean(d && d.repartidorId),
  });
}

export function assertOrderPending(data) {
  return check(data, {
    'order id exists': (d) => Boolean(d && d.id),
    'order estado is PENDIENTE': (d) => Boolean(d && d.estado === 'PENDIENTE'),
  });
}

export function assertOrderForDriver(data, repartidorId) {
  return check(data, {
    'driver active order found': (d) => Boolean(d && d.id),
    'order belongs to requested repartidor': (d) => Boolean(d && d.repartidorId === repartidorId),
  });
}

export function assertOrderCancelled(data) {
  return check(data, {
    'cancel endpoint returns object or empty body': (d) => d === null || typeof d === 'object',
  });
}

export function assertDriverActive(data) {
  return check(data, {
    'driver state is ACTIVO after cancellation': (d) => Boolean(d && d.estado === 'ACTIVO'),
  });
}
