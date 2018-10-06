#Room Registry

An application that offers a REST Api for managing conference room bookings.

####Usage

Start the server (default port is 9000) with: `sbt run`

####REST endpoints

```
GET  /rooms             gives a list of all rooms with their current availability
```

```
GET  /rooms/{name}      gives the booking details of a given room
```

Special error statuses:

* Http 404:   room not found

```
POST /rooms/{name}      adds a booking (in Json) to a given room
```

Special error statuses:

* Http 404:   room not found

* Http 422:   booking invalid

Example of a booking Json:

```
{"time":"2018-10-05T13:26:44.0","duration":"PT25M"}
```