def eb = vertx.eventBus
def amqpAddress = "amqp_bridge"
def address = "amqp.logs"

// Start up AMQP bus mod.
container.deployWorkerVerticle(
        "amqp-busmod#1.1.0",
        [uri: "amqp://localhost", address: amqpAddress, defaultContentType: "application/json" ],
        1) {

    def createMsg = [exchange: "amq.topic", routingKey: "logs.#", forward: address ]
    eb.send("${amqpAddress}.create-consumer", createMsg) { reply ->
        startHttpServer()
    }

}

// MongoDB bus mod for storing the messages.
def mongoAddress = "vertx.mongopersistor"
container.deployWorkerVerticle("mongo-persistor", [address: mongoAddress, db_name: 'log-app'], 1) {

    // Clear existing logs first.
    eb.send mongoAddress, [action: "delete", collection: "logs", matcher: [:]]

    // Save incoming log messages to 'logs' collection.
    eb.registerHandler("logs") { msg ->
        eb.send mongoAddress, [action: "save", collection: "logs", document: [msg: msg.body.text] ]
    }
}

eb.registerHandler(address) { msg ->
    print "[log] ${msg.body.body}"
    eb.send "logs", [text: msg.body.body]
}

def startHttpServer() {
    def server = vertx.createHttpServer()

    // Serve the static resources
    server.requestHandler { req ->
        if (req.uri == '/') req.response.sendFile('index.html')
        if (req.uri == '/vertxbus.js') req.response.sendFile('vertxbus.js')
    }

    vertx.createSockJSServer(server).bridge(prefix: '/eventbus', [[:]])

    server.listen 8181

    println "Server started at http://localhost:8181/"
}
