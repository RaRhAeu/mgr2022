from gevent import monkey
from gevent.pywsgi import WSGIServer
monkey.patch_all()

from .app import app  # noqa F401

http_server = WSGIServer(('0.0.0.0', 8000), app)
http_server.serve_forever()
