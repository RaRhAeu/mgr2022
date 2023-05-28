import http from 'k6/http';
import { check } from 'k6';

export const options = {
  executor: "constant-vus",
  vus: 1,
  duration: "5m"
};

export default function () {
  const result = http.get('http://app-server:8000/s1');
  check(result, {
    'http response status code is 200': result.status === 200,
  });
}