import { SharedArray } from 'k6/data';
import { check } from 'k6';
import { getRuntimeConfig, baseThresholds } from '../config/config.js';
import { runApiJourney } from '../utils/flows.js';

const config = getRuntimeConfig();
const users = new SharedArray('users-load', () => JSON.parse(open('../data/users.json')));
const payloads = new SharedArray('payloads-load', () => JSON.parse(open('../data/order-payloads.json')));

export const options = {
  scenarios: {
    load: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '2m', target: 10 },
        { duration: '4m', target: 10 },
        { duration: '2m', target: 0 },
      ],
      gracefulRampDown: '30s',
    },
  },
  thresholds: baseThresholds({
    http_req_duration: ['p(95)<1200', 'p(99)<2200'],
    http_req_failed: ['rate<0.02'],
  }),
};

function pick(array, index) {
  return array[index % array.length];
}

export default function () {
  const index = __VU + __ITER;
  const user = pick(users, index);
  const payload = pick(payloads, index);
  const journey = runApiJourney({ config, user, payload });

  check(journey, {
    'load journey completed': (result) => result.ok,
  });
}

export function handleSummary(data) {
  return {
    'performance/reports/load-summary.json': JSON.stringify(data, null, 2),
  };
}
