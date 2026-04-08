import http from 'k6/http';
import { check } from 'k6';

function expectedStatusLabel(expectedStatus) {
  return Array.isArray(expectedStatus)
    ? `status in [${expectedStatus.join(', ')}]`
    : `status is ${expectedStatus}`;
}

export function safeJson(response) {
  if (!response || !response.body) {
    return null;
  }

  try {
    return response.json();
  } catch (error) {
    return null;
  }
}

export function requestJson({ method, url, body, headers, expectedStatus = 200, timeout = '30s', tags = {} }) {
  try {
    const params = { headers, timeout, tags };
    const payload = typeof body === 'undefined' ? null : JSON.stringify(body);
    const response = http.request(method, url, payload, params);

    const statusOk = Array.isArray(expectedStatus)
      ? expectedStatus.includes(response.status)
      : response.status === expectedStatus;

    const checksOk = check(response, {
      [expectedStatusLabel(expectedStatus)]: () => statusOk,
      'response time < 2000ms': (res) => res.timings.duration < 2000,
    });

    return {
      ok: statusOk && checksOk,
      response,
      data: safeJson(response),
    };
  } catch (error) {
    return {
      ok: false,
      response: null,
      data: null,
      error,
    };
  }
}
