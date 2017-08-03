package codesmell.invoice.dao;

import javax.annotation.concurrent.Immutable;

import java.util.function.Consumer;

@Immutable
public final class InvoiceActor {

	private String actorName;
	private String actorType;

	/**
	 * static factory method for builder
	 */
	public static Builder builder() {
		return new InvoiceActor.Builder();
	}

	/**
	 * forces use of the Builder
	 */
	private InvoiceActor() {
	}

	public String getActorName() {
		return actorName;
	}

	public String getActorType() {
		return actorType;
	}

	public static class Builder {
		private InvoiceActor managedInstance = new InvoiceActor();
		private InvoiceMetaData.Builder parentBuilder;
		private Consumer<InvoiceActor> callback;

		public Builder() {
		}

		public Builder(InvoiceMetaData.Builder b, Consumer<InvoiceActor> f) {
			this.parentBuilder = b;
			this.callback = f;
		}

		public Builder named(String name) {
			managedInstance.actorName = name;
			return this;
		}

		public Builder as(String type) {
			managedInstance.actorType = type;
			return this;
		}

		public InvoiceActor build() {
			return managedInstance;
		}

		public InvoiceMetaData.Builder end() {
			callback.accept(managedInstance);
			return parentBuilder;
		}

	}
}
