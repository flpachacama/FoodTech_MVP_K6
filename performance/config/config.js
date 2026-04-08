import { getEnvironmentConfig, resolveEnvironmentName } from './environments.js';

function parseBoolean(value, fallback = false) {
  if (typeof value === 'undefined') {
    return fallback;
  }

  return String(value).toLowerCase() === 'true';
}

function buildHeaders(token = '') {
  const headers = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

export function baseThresholds(overrides = {}) {
  // Central baseline thresholds; each scenario overrides what it needs.
  return {
    http_req_failed: ['rate<0.02'],
    http_req_duration: ['p(95)<1200', 'p(99)<2000'],
    checks: ['rate>0.98'],
    ...overrides,
  };
}

export function getRuntimeConfig() {
  const envName = resolveEnvironmentName();
  const environment = getEnvironmentConfig(envName);

  return {
    envName,
    orderBaseUrl: environment.orderBaseUrl,
    deliveryBaseUrl: environment.deliveryBaseUrl,
    authEnabled: parseBoolean(__ENV.AUTH_ENABLED, false),
    authUrl: __ENV.AUTH_URL || '',
    authToken: __ENV.AUTH_TOKEN || '',
    timeout: __ENV.HTTP_TIMEOUT || '30s',
    defaultSleepSeconds: Number(__ENV.SLEEP_SECONDS || 1),
    enableCleanup: parseBoolean(__ENV.ENABLE_CLEANUP, true),
    headers: buildHeaders(__ENV.AUTH_TOKEN || ''),
  };
}
