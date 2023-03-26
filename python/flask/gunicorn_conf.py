import multiprocessing
workers =  2 * multiprocessing.cpu_count() + 1

bind = "0.0.0.0:8000"
keepalive = 120
errorlog = '-'
pidfile = '/tmp/python.pid'
loglevel = 'info'
threads = 10