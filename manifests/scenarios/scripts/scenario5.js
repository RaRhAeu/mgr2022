import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 50 },
    { duration: '30s', target: 50 },
    { duration: '10s', target: 0 },
  ],
};

export default function () {
  const result = http.get('http://app-server:8000/s5');
  check(result, {
    'http response status code is 200': result.status === 200,
  });
}