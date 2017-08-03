package codesmell.invoice.dao;

public class Pack {
    private String packId;
    private String shipmentNumber;
    private String trailerNumber;
    private String sourceNumber;
    private InvoiceActor sourceActor;
    private InvoiceActor destinationActor;
    private String packNumber;

    public static Builder builder() {
        return new Pack.Builder();
    }

    public static class Builder {
        private Pack managedInstance = new Pack();

        public Builder identifiedBy(String id) {
            managedInstance.packId = id;
            return this;
        }

        public Builder shipmentGroupingId(String shipmentNumber) {
            managedInstance.shipmentNumber = shipmentNumber;
            return this;
        }

        public Builder beingTransportedOn(String trailerNumber) {
            managedInstance.trailerNumber = trailerNumber;
            return this;
        }

        public Builder packNumber(String id) {
            managedInstance.packNumber = id;
            return this;
        }

        public Builder suppliedBy(String sourceId, String sourceType) {
            managedInstance.sourceActor = InvoiceActor.builder().named(sourceId).as(sourceType).build();
            return this;
        }

        public Builder receivedBy(String destId, String destType) {
            managedInstance.destinationActor = InvoiceActor.builder().named(destId).as(destType).build();
            return this;
        }

        public Pack build() {
            return managedInstance;
        }
    }

    public String getPackId() {
        return packId;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public String getSourceNumber() {
        return sourceNumber;
    }

    public InvoiceActor getSourceActor() {
        return sourceActor;
    }

    public InvoiceActor getDestinationActor() {
        return destinationActor;
    }

    public String getPackNumber() {
        return packNumber;
    }

}
