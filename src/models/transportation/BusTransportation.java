package models.transportation;

public class BusTransportation extends Transportation{
    private String busNumber;

    private BusTransportation(Builder builder) {
        super(builder);
        this.busNumber = builder.busNumber;
    }

    public static class Builder extends Transportation.Builder<Builder> {
        private String busNumber;

        public Builder(String provider) {
            super(provider);
        }

        public Builder busNumber(String busNumber) { this.busNumber = busNumber; return this; }

        @Override
        public BusTransportation build() { return new BusTransportation(this); }
    }

    @Override
    public String getTransportationType() { return "Bus"; }

    @Override
    public String toString() {
        return super.toString() + " | Bus: " + busNumber;
    }
}
