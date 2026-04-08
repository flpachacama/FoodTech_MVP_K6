import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { getRuntimeConfig, stressThresholds } from '../config/config.js';
import { loadJson, pickByIteration, buildOrderPayload, pickClimateForStress } from '../utils/data-builders.js';
import { cancelOrderFlow, createFlowContext, createOrderJourney } from '../flows/business-flows.js';

const baseConfig = getRuntimeConfig();
const users = new SharedArray('users-stress', () => loadJson('../data/users.json'));
const orderTemplates = new SharedArray('orders-stress', () => loadJson('../data/order-payloads.json'));
const matrix = new SharedArray('matrix-stress', () => loadJson('../data/test-case-matrix.json'));

const flowCheckout = matrix.find((item) => item.flowId === 'checkout_assignment_happy');
const flowPending = matrix.find((item) => item.flowId === 'checkout_pending_rain_hard');
const flowCancel = matrix.find((item) => item.flowId === 'cancel_and_release_driver');

export const options = {
  scenarios: {
    stress_checkout_happy: {
      executor: 'ramping-vus',
      exec: 'stressCheckoutHappy',
      startVUs: 1,
      stages: [
        { duration: '2m', target: 12 },
        { duration: '3m', target: 24 },
        { duration: '3m', target: 36 },
        { duration: '2m', target: 0 },
      ],
      gracefulRampDown: '45s',
    },
    stress_checkout_climate_hard: {
      executor: 'ramping-vus',
      exec: 'stressCheckoutClimateHard',
      startVUs: 1,
      stages: [
        { duration: '2m', target: 8 },
        { duration: '3m', target: 16 },
        { duration: '3m', target: 24 },
        { duration: '2m', target: 0 },
      ],
      gracefulRampDown: '45s',
    },
  },
  thresholds: stressThresholds(),
};

export function stressCheckoutHappy() {
  const seed = __VU * 2000 + __ITER;
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 0.6 };
  const user = pickByIteration(users, seed);
  const template = pickByIteration(orderTemplates, seed);
  const payload = buildOrderPayload(template, { suffix: `stress-happy-${seed}`, climate: 'SOLEADO' });
  const ctx = createFlowContext(scenarioConfig, user, false);

  // USER_STORY: HU8, HU5
  // TEST_CASE: TC-024, TC-013
  // DESCRIPCION: Flujo principal de confirmacion y asignacion en condiciones extremas.
  const created = createOrderJourney(ctx, payload, {
    hu: flowCheckout.userStory,
    tc: flowCheckout.testCases.join('-'),
    scenarioType: 'stress',
  }, false);

  let canceled = { ok: true };
  if (created.orderId && created.repartidorId) {
    // USER_STORY: HU9, HU6
    // TEST_CASE: TC-029, TC-019
    // DESCRIPCION: Validar liberacion de repartidor tras cancelacion durante stress.
    canceled = cancelOrderFlow(ctx, created.orderId, created.repartidorId, {
      hu: flowCancel.userStory,
      tc: flowCancel.testCases.join('-'),
      scenarioType: 'stress',
    });
  }

  check({ created, canceled }, {
    'stress happy flow ok': (result) => result.created.ok,
    'stress happy cancel flow ok': (result) => result.canceled.ok,
  });
}

export function stressCheckoutClimateHard() {
  const seed = __VU * 2400 + __ITER;
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 0.5 };
  const user = pickByIteration(users, seed);
  const template = pickByIteration(orderTemplates, seed);
  const climate = pickClimateForStress() === 'SOLEADO' ? 'LLUVIA_FUERTE' : pickClimateForStress();
  const payload = buildOrderPayload(template, { suffix: `stress-rain-${seed}`, climate });
  const ctx = createFlowContext(scenarioConfig, user, false);

  // USER_STORY: HU3, HU5, HU8
  // TEST_CASE: TC-007, TC-016, TC-025
  // DESCRIPCION: Presionar algoritmo en clima adverso para validar degradacion controlada.
  const created = createOrderJourney(ctx, payload, {
    hu: flowPending.userStory,
    tc: flowPending.testCases.join('-'),
    scenarioType: 'stress',
  }, 'either');

  check(created, {
    'stress hard-climate flow ok': (result) => result.ok,
  });
}

export function handleSummary(data) {
  const reportDir = __ENV.REPORT_DIR || 'performance/reports';
  return {
    [`${reportDir}/stress-summary.json`]: JSON.stringify(data, null, 2),
  };
}
