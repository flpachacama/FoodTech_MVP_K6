import { SharedArray } from 'k6/data';
import { check } from 'k6';
import { getRuntimeConfig, baseThresholds } from '../config/config.js';
import { runApiJourney } from '../utils/flows.js';

const config = getRuntimeConfig();
const users = new SharedArray('users-smoke', () => JSON.parse(open('../data/users.json')));
const payloads = new SharedArray('payloads-smoke', () => JSON.parse(open('../data/order-payloads.json')));

export const options = {
  scenarios: {
    smoke: {
      executor: 'shared-iterations',
      vus: 1,
      iterations: 3,
      maxDuration: '2m',
    },
  },
  thresholds: baseThresholds({
    http_req_duration: ['p(95)<1000', 'p(99)<1800'],
    http_req_failed: ['rate<0.01'],
  }),
};

function pick(array, index) {
  return array[index % array.length];
}

export default function () {
  const user = pick(users, __ITER);
  const payload = pick(payloads, __ITER);
  const journey = runApiJourney({ config, user, payload });

  check(journey, {
    'smoke journey completed': (result) => result.ok,
  });
}

export function handleSummary(data) {
  return {
    'performance/reports/smoke-summary.json': JSON.stringify(data, null, 2),
  };
}
