package factory;

import models.transportation.BusTransportation;
import models.transportation.FlightTransportation;
import models.transportation.TrainTransportation;
import models.transportation.Transportation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransportationFactory {

    public static Transportation fromResultSet(ResultSet rs) throws SQLException {
        String type = rs.getString("transport_type");

        String provider = rs.getString("provider");
        String origin = rs.getString("origin");
        String destination = rs.getString("destination");
        String departure = rs.getString("departure_time");
        String arrival = rs.getString("arrival_time");
        double price = rs.getDouble("price");

        Transportation t;

        switch (type) {
            case "Flight" -> t = new FlightTransportation.Builder(provider)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(departure)
                    .arrivalTime(arrival)
                    .price(price)
                    .flightNumber(rs.getString("flight_number"))
                    .airlineClass(rs.getString("airline_class"))
                    .build();

            case "Train" -> t = new TrainTransportation.Builder(provider)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(departure)
                    .arrivalTime(arrival)
                    .price(price)
                    .trainNumber(rs.getString("train_number"))
                    .wagonNumber(rs.getInt("wagon_number"))
                    .build();

            case "Bus" -> t = new BusTransportation.Builder(provider)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(departure)
                    .arrivalTime(arrival)
                    .price(price)
                    .busNumber(rs.getString("bus_number"))
                    .build();

            default -> throw new SQLException("Unknown transport_type: " + type);
        }

        t.setId(rs.getInt("id"));
        t.setAvailable(rs.getBoolean("available"));
        return t;
    }
}