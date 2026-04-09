const DEFAULT_ENV = 'dev';

const ENVIRONMENTS = {
  dev: {
    orderBaseUrl: 'http://localhost:8081',
    deliveryBaseUrl: 'http://localhost:8080',
  },
  qa: {
    orderBaseUrl: 'https://qa-order.example.com',
    deliveryBaseUrl: 'https://qa-delivery.example.com',
  },
  prod: {
    orderBaseUrl: 'https://order.example.com',
    deliveryBaseUrl: 'https://delivery.example.com',
  },
};

function normalizeEnvName(rawEnvName) {
  return (rawEnvName || DEFAULT_ENV).toLowerCase();
}

export function resolveEnvironmentName() {
  return normalizeEnvName(__ENV.TEST_ENV || __ENV.ENV);
}

export function getEnvironmentConfig(envName) {
  const selectedEnv = ENVIRONMENTS[normalizeEnvName(envName)] || ENVIRONMENTS[DEFAULT_ENV];

  return {
    orderBaseUrl: __ENV.ORDER_BASE_URL || selectedEnv.orderBaseUrl,
    deliveryBaseUrl: __ENV.DELIVERY_BASE_URL || selectedEnv.deliveryBaseUrl,
  };
}

export { DEFAULT_ENV, ENVIRONMENTS };
