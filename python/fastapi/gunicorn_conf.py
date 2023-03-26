import multiprocessing
workers = multiprocessing.cpu_count()

bind = "0.0.0.0:8000"
keepalive = 120
errorlog = '-'
pidfile = '/tmp/python.pid'
loglevel = 'info'