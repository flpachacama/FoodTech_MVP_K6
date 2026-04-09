import { check } from 'k6';
import http from 'k6/http';

export function resolveAuthToken(config, user) {
  if (!config.authEnabled) {
    return config.authToken;
  }

  if (!config.authUrl || !user) {
    return config.authToken;
  }

  const response = http.post(
    config.authUrl,
    JSON.stringify({ username: user.username, password: user.password }),
    {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      timeout: config.timeout,
      tags: { endpoint: 'auth_login' },
    }
  );

  const statusOk = check(response, {
    'auth status is 200 or 201': (res) => res.status === 200 || res.status === 201,
  });

  if (!statusOk) {
    return config.authToken;
  }

  try {
    const body = response.json();
    return body.accessToken || body.token || config.authToken;
  } catch (error) {
    return config.authToken;
  }
}
