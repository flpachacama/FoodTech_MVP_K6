import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { getRuntimeConfig, loadThresholds } from '../config/config.js';
import { loadJson, pickByIteration, buildOrderPayload } from '../utils/data-builders.js';
import { browseCatalogFlow, cancelOrderFlow, createFlowContext, createOrderJourney } from '../flows/business-flows.js';

const baseConfig = getRuntimeConfig();
const users = new SharedArray('users-load', () => loadJson('../data/users.json'));
const orderTemplates = new SharedArray('orders-load', () => loadJson('../data/order-payloads.json'));
const matrix = new SharedArray('matrix-load', () => loadJson('../data/test-case-matrix.json'));

const flowCatalog = matrix.find((item) => item.flowId === 'catalog_visibility');
const flowCheckout = matrix.find((item) => item.flowId === 'checkout_assignment_happy');
const flowPending = matrix.find((item) => item.flowId === 'checkout_pending_rain_hard');
const flowCancel = matrix.find((item) => item.flowId === 'cancel_and_release_driver');

export const options = {
  scenarios: {
    load_checkout_assignment: {
      executor: 'ramping-vus',
      exec: 'loadCheckoutAssignment',
      startVUs: 2,
      stages: [
        { duration: '2m', target: 8 },
        { duration: '4m', target: 14 },
        { duration: '2m', target: 0 },
      ],
      gracefulRampDown: '30s',
    },
    load_checkout_pending_climate: {
      executor: 'constant-vus',
      exec: 'loadCheckoutPendingClimate',
      vus: 3,
      duration: '8m',
    },
    load_browse_map: {
      executor: 'constant-vus',
      exec: 'loadBrowseMap',
      vus: 4,
      duration: '8m',
    },
  },
  thresholds: loadThresholds(),
};

export function loadCheckoutAssignment() {
  const seed = __VU * 1000 + __ITER;
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 0.9 };
  const user = pickByIteration(users, seed);
  const template = pickByIteration(orderTemplates, seed);
  const payload = buildOrderPayload(template, { suffix: `load-happy-${seed}`, climate: 'SOLEADO' });
  const ctx = createFlowContext(scenarioConfig, user, false);

  // USER_STORY: HU8, HU5
  // TEST_CASE: TC-024, TC-013
  // DESCRIPCION: Flujo principal del negocio bajo carga nominal: confirmar y asignar pedido.
  const created = createOrderJourney(ctx, payload, {
    hu: flowCheckout.userStory,
    tc: flowCheckout.testCases.join('-'),
    scenarioType: 'load',
  }, false);

  // USER_STORY: HU9, HU6
  // TEST_CASE: TC-029, TC-019
  // DESCRIPCION: Cancelacion concurrente posterior a asignacion para validar liberacion del recurso.
  const canceled = cancelOrderFlow(ctx, created.orderId, created.repartidorId, {
    hu: flowCancel.userStory,
    tc: flowCancel.testCases.join('-'),
    scenarioType: 'load',
  });

  check({ created, canceled }, {
    'load checkout happy ok': (result) => result.created.ok,
    'load checkout cancel ok': (result) => result.canceled.ok,
  });
}

export function loadCheckoutPendingClimate() {
  const seed = __VU * 700 + __ITER;
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 0.7 };
  const user = pickByIteration(users, seed);
  const template = pickByIteration(orderTemplates, seed);
  const payload = buildOrderPayload(template, { suffix: `load-rain-${seed}`, climate: 'LLUVIA_FUERTE' });
  const ctx = createFlowContext(scenarioConfig, user, false);

  // USER_STORY: HU3, HU5, HU8
  // TEST_CASE: TC-007, TC-016, TC-025
  // DESCRIPCION: Flujo de clima adverso con expectativa de asignacion condicionada (ASIGNADO o PENDIENTE).
  const created = createOrderJourney(ctx, payload, {
    hu: flowPending.userStory,
    tc: flowPending.testCases.join('-'),
    scenarioType: 'load',
  }, 'either');

  check(created, {
    'load pending-climate flow ok': (result) => result.ok,
  });
}

export function loadBrowseMap() {
  const seed = __VU * 500 + __ITER;
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 1.1 };
  const user = pickByIteration(users, seed);
  const ctx = createFlowContext(scenarioConfig, user, false);

  // USER_STORY: HU10
  // TEST_CASE: TC-031, TC-032
  // DESCRIPCION: Exploracion de restaurantes y repartidores en mapa durante carga esperada.
  const catalog = browseCatalogFlow(ctx, {
    hu: flowCatalog.userStory,
    tc: flowCatalog.testCases.join('-'),
    scenarioType: 'load',
  });

  check(catalog, {
    'load browse-map flow ok': (result) => result.ok,
  });
}

export function handleSummary(data) {
  const reportDir = __ENV.REPORT_DIR || 'performance/reports';
  return {
    [`${reportDir}/load-summary.json`]: JSON.stringify(data, null, 2),
  };
}
