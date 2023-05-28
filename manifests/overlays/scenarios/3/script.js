import http from 'k6/http';
import { check } from 'k6';

export const options = {
  executor: "constant-vus",
  vus: 1,
  duration: "5m"
};

export default function () {
  const url = 'http://app-server:8000/s3';
  const payload = JSON.stringify({
    password: 'test-password'
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const result = http.post(url, payload, params);
    check(result, {
    'http response status code is 200': result.status === 200,
  });
}