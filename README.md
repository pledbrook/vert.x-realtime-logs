Vert.x log viewer application
=============================

This is a sample [Vert.x](http://vertx.io/) application, written in Groovy, that accepts messages via AMQP and then both stores them in a MongoDB store and pushes them to the browser via SockJS.

To run it, you need to have Vert.x installed. You will also need to install the AMQP busmod (version 1.1.0). I typically unpack the [busmod zip](https://github.com/downloads/blalor/vert.x-busmod-amqp/amqp-busmod-1.1.0-mod.tar.gz) into the directory ~/.vertx/mods/ and set the `VERTX_MODS` environment variable to that directory.

You will also need to have RabbitMQ and MongoDB running locally. Once those are up, run

    vertx run app.groovy

from the application directory. Don't forget to use Java 7!

Once the app is running, point your browser at the URL that's printed to the terminal and then simply send messages to the amq.topic exchange with a routing key of "logs.dummy" containing just plain text. For now you'll have to write your own client, but this could easily be added. 
