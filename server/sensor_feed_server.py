import cherrypy
from ws4py.server.cherrypyserver import WebSocketPlugin, WebSocketTool
from ws4py.websocket import WebSocket

cherrypy.server.socket_host = '192.168.0.17'
cherrypy.config.update({'server.socket_port': 9000})
WebSocketPlugin(cherrypy.engine).subscribe()
cherrypy.tools.websocket = WebSocketTool()


class FeedPrinter(WebSocket):
    def received_message(self, message):
        """Prints the values received from client."""
        values = str(message).split(',')
        print(values[0])
        print("X Axis -> ", values[1])
        print("Y Axis -> ", values[2])
        print("Z Axis -> ", values[3])
        print("\n")


class Root(object):
    @cherrypy.expose
    def index(self):
        return 'some HTML with a websocket javascript connection'

    @cherrypy.expose
    def ws(self):
        # you can access the class instance through
        handler = cherrypy.request.ws_handler


cherrypy.quickstart(Root(), '/', config={'/ws': {'tools.websocket.on': True,
                                                 'tools.websocket.handler_cls': FeedPrinter}})
