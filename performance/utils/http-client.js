import http from 'k6/http';
import { check } from 'k6';

function normalizeStatus(expectedStatus) {
  return Array.isArray(expectedStatus) ? expectedStatus : [expectedStatus];
}

function safeJson(response) {
  if (!response || !response.body) {
    return null;
  }

  try {
    return response.json();
  } catch (error) {
    return null;
  }
}

export function requestJson(params) {
  const {
    method,
    url,
    body,
    headers,
    timeout,
    expectedStatus,
    tags,
    trace,
    debug,
  } = params;

  const expected = normalizeStatus(expectedStatus);
  const payload = typeof body === 'undefined' ? null : JSON.stringify(body);
  const requestTags = {
    ...tags,
    hu_id: trace.hu,
    tc_id: trace.tc,
    scenario_type: trace.scenarioType,
  };

  try {
    const response = http.request(method, url, payload, {
      headers,
      timeout,
      tags: requestTags,
    });

    const statusOk = expected.indexOf(response.status) >= 0;

    check(response, {
      [`status in [${expected.join(',')}]`]: () => statusOk,
      'response time < 3000ms': (res) => res.timings.duration < 3000,
    });

    if (debug && !statusOk) {
      console.log(`[DEBUG] ${method} ${url} status=${response.status} body=${String(response.body).slice(0, 220)}`);
    }

    return {
      ok: statusOk,
      status: response.status,
      data: safeJson(response),
      response,
    };
  } catch (error) {
    console.error(`[ERROR] ${method} ${url} -> ${error.message}`);
    return {
      ok: false,
      status: 0,
      data: null,
      response: null,
      error,
    };
  }
}
