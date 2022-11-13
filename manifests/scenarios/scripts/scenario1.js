import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 100 }, // simulate ramp-up of traffic from 1 to 100 users over 5 minutes.
    { duration: '2m', target: 100 }, // stay at 100 users for 10 minutes
    { duration: '1m', target: 0 }, // ramp-down to 0 users
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