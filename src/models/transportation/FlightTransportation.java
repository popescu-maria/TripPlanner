package models.transportation;

public class FlightTransportation extends Transportation {
    private String flightNumber;
    private String airlineClass;

    private FlightTransportation(Builder builder) {
        super(builder);
        this.flightNumber = builder.flightNumber;
        this.airlineClass = builder.airlineClass;
    }

    public static class Builder extends Transportation.Builder<Builder> {
        private String flightNumber;
        private String airlineClass = "economy";

        public Builder(String provider) {
            super(provider);
        }

        public Builder flightNumber(String flightNumber) { this.flightNumber = flightNumber; return this; }
        public Builder airlineClass(String airlineClass) { this.airlineClass = airlineClass; return this; }

        @Override
        public FlightTransportation build() { return new FlightTransportation(this); }
    }

    @Override
    public String getTransportationType() { return "Flight"; }

    @Override
    public String toString() {
        return super.toString() + " | Flight: " + flightNumber + " | Class: " + airlineClass;
    }
}