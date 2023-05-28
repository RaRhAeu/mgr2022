import http from 'k6/http';
import { check } from 'k6';

export const options = {
  executor: "constant-vus",
  vus: 1,
  duration: "5m"
};
//
// export const options = {
//   stages: [
//     { duration: '30s', target: 10 },
//     { duration: '1m', target: 10 },
//     { duration: '30s', target: 50 },
//     { duration: '1m', target: 50 },
//     { duration: '30s', target: 100 },
//     { duration: '1m', target: 100 },
//     { duration: '30s', target: 0 },
//   ],
// };

export default function () {
  const result = http.get('http://app-server:8000/s2');
  check(result, {
    'http response status code is 200': result.status === 200,
  });
}