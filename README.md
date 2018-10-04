An application that offers a REST Api for managing conference room bookings.

Start the server with: sbt run

REST endpoints:

GET  host:9000/rooms             gives a list of all rooms with their current availability
GET  host:9000/rooms/{name}      gives the booking details of a given room
POST host:9000/rooms/{name}      adds a booking (in Json) to a given room


TODO example of booking Json