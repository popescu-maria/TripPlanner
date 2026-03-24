package models.transportation;

public class TrainTransportation extends Transportation {
    private String trainNumber;
    private int wagonNumber;

    private TrainTransportation(Builder builder) {
        super(builder);
        this.trainNumber = builder.trainNumber;
        this.wagonNumber = builder.wagonNumber;
    }

    public static class Builder extends Transportation.Builder<Builder> {
        private String trainNumber;
        private int wagonNumber;

        public Builder(String provider) {
            super(provider);
        }

        public Builder trainNumber(String trainNumber) { this.trainNumber = trainNumber; return this; }
        public Builder wagonNumber(int wagonNumber) { this.wagonNumber = wagonNumber; return this; }

        @Override
        public TrainTransportation build() { return new TrainTransportation(this); }
    }

    @Override
    public String getTransportationType() { return "Train"; }

    @Override
    public String toString() {
        return super.toString() + " | Train: " + trainNumber + " | Wagon number: " + wagonNumber;
     }
}
