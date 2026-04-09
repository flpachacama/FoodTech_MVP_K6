import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { getRuntimeConfig, spikeThresholds } from '../config/config.js';
import { loadJson, pickByIteration, buildOrderPayload } from '../utils/data-builders.js';
import { cancelOrderFlow, createFlowContext, createOrderJourney } from '../flows/business-flows.js';

const baseConfig = getRuntimeConfig();
const users = new SharedArray('users-spike', () => loadJson('../data/users.json'));
const orderTemplates = new SharedArray('orders-spike', () => loadJson('../data/order-payloads.json'));
const matrix = new SharedArray('matrix-spike', () => loadJson('../data/test-case-matrix.json'));

const flowCheckout = matrix.find((item) => item.flowId === 'checkout_assignment_happy');
const flowCancel = matrix.find((item) => item.flowId === 'cancel_before_assignment');

export const options = {
  scenarios: {
    spike_checkout_burst: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 3 },
        { duration: '30s', target: 50 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 3 },
      ],
      gracefulRampDown: '20s',
    },
  },
  thresholds: spikeThresholds(),
};

export default function () {
  const seed = __VU * 3000 + __ITER;
  const scenarioConfig = { ...baseConfig, thinkTimeSeconds: 0.3 };
  const user = pickByIteration(users, seed);
  const template = pickByIteration(orderTemplates, seed);
  const payload = buildOrderPayload(template, { suffix: `spike-${seed}`, climate: 'SOLEADO' });
  const ctx = createFlowContext(scenarioConfig, user, false);

  // USER_STORY: HU8, HU5
  // TEST_CASE: TC-024, TC-013
  // DESCRIPCION: Simular pico repentino en flujo principal de checkout.
  const created = createOrderJourney(ctx, payload, {
    hu: flowCheckout.userStory,
    tc: flowCheckout.testCases.join('-'),
    scenarioType: 'spike',
  }, false);

  // USER_STORY: HU9
  // TEST_CASE: TC-028
  // DESCRIPCION: Limpiar ordenes de pico para preservar consistencia del entorno.
  const canceled = cancelOrderFlow(ctx, created.orderId, created.repartidorId, {
    hu: flowCancel.userStory,
    tc: flowCancel.testCases.join('-'),
    scenarioType: 'spike',
  });

  check({ created, canceled }, {
    'spike checkout flow ok': (result) => result.created.ok,
    'spike cleanup flow ok': (result) => result.canceled.ok,
  });
}

export function handleSummary(data) {
  const reportDir = __ENV.REPORT_DIR || 'performance/reports';
  return {
    [`${reportDir}/spike-summary.json`]: JSON.stringify(data, null, 2),
  };
}
