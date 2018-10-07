# Room Registry

An application that offers a REST Api for managing conference room bookings.

#### Usage

Start the server (default port is 9000) with: `sbt run`

#### REST endpoints

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

Booking format:
* time:      the start time in format "yyyy-MM-dd'T'HH-mm"
* duration:  the duration of the booking in ISO 8601 format

Example of a booking Json:

```
{"time":"2018-10-05T13:25","duration":"PT25M"}
```
Special error statuses:

* Http 404:   room not found
* Http 422:   booking invalid