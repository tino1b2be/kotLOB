# kotLOB
##Limit Order Book in Kotlin


Built using openjdk16 and gradle. Easiest way to build and test the web API is to build a Docker image using the `Dockerfile` :

```
#// download Dockerfile and build the image

$ docker build -t kotlob .`

#// Run the docker image and the API will be listening on port 8080

$ docker run -d --network=host kotlob

#// test the API

$ curl -d "type=bid&price=108&qty=2" localhost:8080/v1/orderbook/limit
$ curl -d "type=ask&price=108&qty=3" localhost:8080/v1/orderbook/limit
$ curl localhost:8080/v1/orderbook/tradehistory
$ curl localhost:8080/v1/book/orderbook/orders
```

You can run build, test and run it locally using gradle as follows:

$ git clone https://github.com/tino1b2be/kotLOB.git && cd kotLOB
$ bash gradlew build
$ bash gradlew test
$ bash gradle run

The web API was built using vert.x and supports three functions:

### 1. Create a new limit order:

HTTP POST - `/v1/orderbook/limit` with the following POST parameters:

```
price = int
qty = int
type = "bid" or "ask"
```

Example using cURL:

```
$ curl -d "type=bid&price=108&qty=2" localhost:8080/v1/orderbook/limit
$ curl -d "type=ask&price=108&qty=3" localhost:8080/v1/orderbook/limit
```
### 2. List recent trades:

HTTP GET - `/v1/orderbook/tradehistory`

Example using cURL:

`$ curl localhost:8080/v1/orderbook/tradehistory`

### 3. List orders in the order book

HTTP GET - `/v1/book/orderbook/orders`

`$ curl localhost:8080/v1/book/orderbook/orders`

---

There is also a CLI tool which you can build and run as follows:

1. Replace `KotWebKt` with `KotCLIKt` inside the `build.gradle.kts` file

```
application {
    mainClass.set("KotWebKt")
}
```

2. Build the project and run it to start the CLI :

```
$ bash gradlew clean build run
```

---

Easiest way to build and test the web API is to build a Docker image using the `Dockerfile` :

```
// download Dockerfile and build the image

$ docker build -t kotlob .`

// Run the docker image and the API will be listening on port 8080

$ docker run -d --network=host kotlob

```