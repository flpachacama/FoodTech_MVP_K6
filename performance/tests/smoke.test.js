import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { getRuntimeConfig, smokeThresholds } from '../config/config.js';
import { loadJson, pickByIteration, buildOrderPayload } from '../utils/data-builders.js';
import { browseCatalogFlow, cancelOrderFlow, createFlowContext, createOrderJourney } from '../flows/business-flows.js';

const baseConfig = getRuntimeConfig();
const users = new SharedArray('users-smoke', () => loadJson('../data/users.json'));
const orderTemplates = new SharedArray('orders-smoke', () => loadJson('../data/order-payloads.json'));
const matrix = new SharedArray('matrix-smoke', () => loadJson('../data/test-case-matrix.json'));

const flowCatalog = matrix.find((item) => item.flowId === 'catalog_visibility');
const flowCheckout = matrix.find((item) => item.flowId === 'checkout_assignment_happy');
const flowCancel = matrix.find((item) => item.flowId === 'cancel_and_release_driver');

export const options = {
  scenarios: {
    smoke_catalog_visibility: {
      executor: 'shared-iterations',
      exec: 'smokeCatalogVisibility',
      vus: 1,
      iterations: 2,
      maxDuration: '1m',
    },
    smoke_checkout_assignment_cancel: {
      executor: 'shared-iterations',
      exec: 'smokeCheckoutAssignmentCancel',
      vus: 1,
      iterations: 2,
      maxDuration: '2m',
      startTime: '5s',
    },
  },
  thresholds: smokeThresholds(),
};

export function smokeCatalogVisibility() {
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 0.5 };
  const user = pickByIteration(users, __ITER);
  const ctx = createFlowContext(scenarioConfig, user, true);

  // USER_STORY: HU10
  // TEST_CASE: TC-031, TC-032
  // DESCRIPCION: Validar disponibilidad de catalogo (restaurantes + repartidores) en sanidad inicial.
  const catalog = browseCatalogFlow(ctx, {
    hu: flowCatalog.userStory,
    tc: flowCatalog.testCases.join('-'),
    scenarioType: 'smoke',
  });

  check(catalog, {
    'smoke catalog visibility flow ok': (result) => result.ok,
  });
}

export function smokeCheckoutAssignmentCancel() {
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 0.7 };
  const user = pickByIteration(users, __ITER);
  const template = pickByIteration(orderTemplates, __ITER);
  const payload = buildOrderPayload(template, { suffix: `smoke-happy-${__ITER}`, climate: 'SOLEADO' });
  const ctx = createFlowContext(scenarioConfig, user, true);

  // USER_STORY: HU8, HU5
  // TEST_CASE: TC-024, TC-013
  // DESCRIPCION: Confirmar pedido y validar asignacion automatica en flujo feliz del core.
  const created = createOrderJourney(ctx, payload, {
    hu: flowCheckout.userStory,
    tc: flowCheckout.testCases.join('-'),
    scenarioType: 'smoke',
  }, false);

  // USER_STORY: HU9, HU6
  // TEST_CASE: TC-029, TC-019
  // DESCRIPCION: Cancelar pedido asignado y validar liberacion de repartidor.
  const canceled = cancelOrderFlow(ctx, created.orderId, created.repartidorId, {
    hu: flowCancel.userStory,
    tc: flowCancel.testCases.join('-'),
    scenarioType: 'smoke',
  });

  check({ created, canceled }, {
    'smoke checkout assignment ok': (result) => result.created.ok,
    'smoke cancel and release ok': (result) => result.canceled.ok,
  });
}

export function handleSummary(data) {
  const reportDir = __ENV.REPORT_DIR || 'performance/reports';
  return {
    [`${reportDir}/smoke-summary.json`]: JSON.stringify(data, null, 2),
  };
}
