import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '1m', target: 10 },
    { duration: '30s', target: 100 },
    { duration: '1m', target: 100 },
    { duration: '30s', target: 1000 },
    { duration: '1m', target: 1000 },
    { duration: '30s', target: 0 },
  ],
};

export default function () {
    const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  const result = http.get('http://app-server:8000/s1', params);
  check(result, {
    'http response status code is 200': result.status === 200,
  });
}