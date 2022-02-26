### Websockets in play with Akka event bus

This project doesn't use the `PlayPlugin` because it is not ready for Scala 3.
Instead we use `sbt-revolver` plugin, to run the project,
```
sbt:hello-stream> reStart
sbt:hello-stream> reStop
```

### Sample requests
Connect to `ws://localhost:9000/socket`
And send message,
```
{"body": {"test": 1}}
```

Also one can try the rest endpoints with curl like,
```
$ curl localhost:9000/ping
pong%
```

```
curl -XPOST localhost:9000/send-message \
-H 'Content-Type: application/json' \
-d '{"message": "Hello World From Curl"}'
Hello World From Curl%
```

