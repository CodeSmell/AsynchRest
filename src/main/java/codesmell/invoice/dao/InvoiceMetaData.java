package codesmell.invoice.dao;

import java.util.function.Consumer;

public final class InvoiceMetaData {

	private String internalId;

	private InvoiceActor invoiceSupplier;
	private InvoiceActor invoiceDestination;

	private String supplierDocumentId;

	private String trailerNumber;

	/**
	 * static factory method for builder
	 */
	public static Builder builder() {
		return new InvoiceMetaData.Builder();
	}

	/**
	 * forces use of the Builder
	 */
	private InvoiceMetaData() {
	}

	public static class Builder {
		private InvoiceMetaData managedInstance = new InvoiceMetaData();

		public Builder() {
		}

		/**
		 * build
		 * 
		 * @return
		 */
		public InvoiceMetaData build() {
			return managedInstance;
		}

		public Builder withInternalId(String id) {
			managedInstance.internalId = id;
			return this;
		}

		public Builder shippingOnTrailer(String trailerNum) {
			managedInstance.trailerNumber = trailerNum;
			return this;
		}

		public Builder withDocumentNumber(String docNumber) {
			managedInstance.supplierDocumentId = docNumber;
			return this;
		}

		public InvoiceActor.Builder suppliedBy() {
			Consumer<InvoiceActor> f = obj -> {
				managedInstance.invoiceSupplier = obj;
			};
			return new InvoiceActor.Builder(this, f);
		}

		public InvoiceActor.Builder beingSentTo() {
			Consumer<InvoiceActor> f = obj -> {
				managedInstance.invoiceDestination = obj;
			};
			return new InvoiceActor.Builder(this, f);
		}

	}

	public String getInternalId() {
		return internalId;
	}

	public InvoiceActor getInvoiceSupplier() {
		return invoiceSupplier;
	}

	public InvoiceActor getInvoiceDestination() {
		return invoiceDestination;
	}

	public String getSupplierDocumentId() {
		return supplierDocumentId;
	}

	public String getTrailerNumber() {
		return trailerNumber;
	}

}
