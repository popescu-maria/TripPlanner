# Trip Planner

## Business context

Trip Planner is an application that helps travelers organize their trips.
A traveler signs in, creates trips, and attaches everything a trip needs:
destinations, transport (flights, trains, buses), accommodation, and activities.
Each trip has a budget, and every booking is deducted from it, so the traveler
always knows how much they have left. Cancelling a booking refunds the budget.

Data is stored in a PostgreSQL database through a JDBC data access layer, the
operations are exposed by service classes, and the app is used through a JavaFX
interface. Every action is recorded in an audit log.

## Objects in the system

- **Traveler** — a person using the app (name, email, phone). Trips belong to a traveler.
- **Trip** — the central object: a named journey with dates, a traveler, a budget,
  destinations, transport, accommodation, activities and bookings.
- **Budget** — the money allocated to a trip; tracks total and spent amounts.
- **Destination** — a city/country a trip can visit.
- **Transportation** — an abstract bookable, specialized into **Flight**, **Train**
  and **Bus**.
- **Accommodation** — a place to stay (type, price per night, number of nights).
- **Activity** — something to do at a destination (price, participant capacity).
- **Booking** — links a trip to a bookable item (transport, accommodation or activity).

Transportation, Accommodation and Activity implement a common **Bookable** interface.

## What you can do

**Travelers** — register, log in by email, list, delete.

**Destinations** — add, list, delete.

**Trips** — create a trip for the logged-in traveler (with budget and destinations),
list your trips (sorted by start date), view a full trip summary, delete.

**Bookings** — book a flight, train, bus, accommodation or activity for a trip
(cost deducted from the budget), list bookings, cancel a booking (budget refunded).

## Architecture

The project is layered: **models** (domain objects), **interfaces** (`Bookable`),
**exceptions** (custom exceptions), **dao** (CRUD per entity over a generic DAO
contract and a singleton connection), **service** (system operations and business
rules), **audit** (action logging), and **ui** (the JavaFX screens).

## Design patterns used

- **Singleton** — database connection manager and audit service.
- **Builder** — trips, accommodations, activities, and the transportation hierarchy.
- **Factory** — rebuilds the correct transportation subclass from a database row.
