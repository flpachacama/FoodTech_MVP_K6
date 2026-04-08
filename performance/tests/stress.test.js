import { SharedArray } from 'k6/data';
import { check } from 'k6';
import { getRuntimeConfig, baseThresholds } from '../config/config.js';
import { runApiJourney } from '../utils/flows.js';

const config = getRuntimeConfig();
const users = new SharedArray('users-stress', () => JSON.parse(open('../data/users.json')));
const payloads = new SharedArray('payloads-stress', () => JSON.parse(open('../data/order-payloads.json')));

export const options = {
  scenarios: {
    stress: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        { duration: '2m', target: 15 },
        { duration: '3m', target: 30 },
        { duration: '3m', target: 45 },
        { duration: '2m', target: 0 },
      ],
      gracefulRampDown: '45s',
    },
  },
  thresholds: baseThresholds({
    http_req_duration: ['p(95)<2500', 'p(99)<4000'],
    http_req_failed: ['rate<0.05'],
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
    'stress journey completed': (result) => result.ok,
  });
}

export function handleSummary(data) {
  return {
    'performance/reports/stress-summary.json': JSON.stringify(data, null, 2),
  };
}
