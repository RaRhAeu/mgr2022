import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 5},
    { duration: '1m', target: 5 },
    { duration: '30s', target: 10 },
    { duration: '1m', target: 10 },
    { duration: '30s', target: 0 },
  ],
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