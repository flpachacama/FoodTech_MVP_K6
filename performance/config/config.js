import { getEnvironmentConfig, resolveEnvironmentName } from './environments.js';

function parseBoolean(value, fallback) {
  if (typeof value === 'undefined') {
    return fallback;
  }
  return String(value).toLowerCase() === 'true';
}

function buildHeaders(token) {
  const headers = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

export function getRuntimeConfig() {
  const envName = resolveEnvironmentName();
  const env = getEnvironmentConfig(envName);
  const authToken = __ENV.AUTH_TOKEN || '';

  return {
    envName,
    orderBaseUrl: env.orderBaseUrl,
    deliveryBaseUrl: env.deliveryBaseUrl,
    timeout: __ENV.HTTP_TIMEOUT || '30s',
    thinkTimeSeconds: Number(__ENV.SLEEP_SECONDS || 0.8),
    enableCleanup: parseBoolean(__ENV.ENABLE_CLEANUP, true),
    authEnabled: parseBoolean(__ENV.AUTH_ENABLED, false),
    authUrl: __ENV.AUTH_URL || '',
    authToken,
    headers: buildHeaders(authToken),
  };
}

export function smokeThresholds() {
  return {
    http_req_failed: ['rate<0.03'],
    http_req_duration: ['p(95)<1100', 'p(99)<1800'],
    checks: ['rate>0.96'],
    'http_req_duration{endpoint:orders_create}': ['p(95)<1300'],
  };
}

export function loadThresholds() {
  return {
    http_req_failed: ['rate<0.03'],
    http_req_duration: ['p(95)<1300', 'p(99)<2200'],
    checks: ['rate>0.95'],
    'http_req_duration{endpoint:orders_create}': ['p(95)<1700'],
    'http_req_duration{endpoint:orders_cancel}': ['p(95)<1000'],
  };
}

export function stressThresholds() {
  return {
    http_req_failed: ['rate<0.08'],
    http_req_duration: ['p(95)<3000', 'p(99)<5000'],
    checks: ['rate>0.83'],
    'http_req_duration{endpoint:orders_create}': ['p(95)<3200'],
  };
}

export function spikeThresholds() {
  return {
    http_req_failed: ['rate<0.1'],
    http_req_duration: ['p(95)<3000', 'p(99)<5000'],
    checks: ['rate>0.82'],
  };
}
