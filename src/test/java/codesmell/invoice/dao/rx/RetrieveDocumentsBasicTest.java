package codesmell.invoice.dao.rx;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import codesmell.invoice.dao.InvoiceActor;
import codesmell.invoice.dao.InvoiceActorType;
import codesmell.invoice.dao.InvoiceDao;
import codesmell.invoice.dao.InvoiceMetaData;
import rx.Observable;

public class RetrieveDocumentsBasicTest {

	String expectedJson = "[{\"invoice\": {\"id\": \"1\",\"foo\": \"bar\"}},{\"invoice\": {\"id\": \"2\",\"foo\": \"fighters\"}}]";

	@Mock
	InvoiceDao dao;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_looping() {
		List<InvoiceMetaData> imdList = this.getMetaDataList(this::validReturn, this::jsonAnswer);

		// The standard way to find the meta data
		// and then iterate over the list to 
		// build a single JSON document
		StringBuilder sb = new StringBuilder("[");
		if (imdList != null && !imdList.isEmpty()) {
			for (InvoiceMetaData meta : imdList) {
				String id = meta.getInternalId();
				sb.append(dao.retrieveInvoiceDocumentByIdentifier(id));
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
		}

		System.out.println(sb.toString());
		assertEquals(expectedJson, sb.toString());
	}

	@Test
	public void test_java_streams() {
		List<InvoiceMetaData> imdList = this.getMetaDataList(this::validReturn, this::jsonAnswer);
		
		// Use Java8 streams to process the list 
		// and build a single JSON document
		String jsonResponse = imdList.stream()
				.map(InvoiceMetaData::getInternalId)
				.map(dao::retrieveInvoiceDocumentByIdentifier)
				.collect(Collectors.joining(",", "[", "]"));

		System.out.println(jsonResponse);
		assertEquals(expectedJson, jsonResponse);
	}
	
	@Test(expected = RuntimeException.class)
	public void test_java_streams_with_exception() {
		List<InvoiceMetaData> imdList = this.getMetaDataList(this::validReturn, this::jsonAnswerWithException);
		
		// Use Java8 streams to process the list 
		// and handle an error
		String jsonResponse = imdList.stream()
				.map(InvoiceMetaData::getInternalId)
				.map(dao::retrieveInvoiceDocumentByIdentifier) // exception occurs here
				.collect(Collectors.joining(",", "[", "]"));
		
		assertNull(jsonResponse);
	}
	
	@Test
	public void test_java_rx() {
		List<InvoiceMetaData> imdList = this.getMetaDataList(this::validReturn, this::jsonAnswer);
		
		// Use RxJava to process the list 
		// and build a single JSON document
		Observable<InvoiceMetaData> rxObservable = Observable.from(imdList);
		rxObservable.map(InvoiceMetaData::getInternalId)
				.map(dao::retrieveInvoiceDocumentByIdentifier)
				.reduce((total, next) -> total + "," + next)
				.map(item -> "[" + item + "]")
				.subscribe(this::onNext, this::onError);

	}
	
	@Test
	public void test_java_rx_with_exception() {
		List<InvoiceMetaData> imdList = this.getMetaDataList(this::validReturn, this::jsonAnswerWithException);

		// Use RxJava to process the list 
		// and handle an error
		Observable<InvoiceMetaData> rxObservable = Observable.from(imdList);
		rxObservable.map(InvoiceMetaData::getInternalId)
				.map(dao::retrieveInvoiceDocumentByIdentifier) // exception occurs here
				.reduce((total, next) -> total + "," + next)
				.map(item -> "[" + item + "]")
				.subscribe(this::onNext, this::onError);

	}
	
	void onNext(String jsonResponse) {
		System.out.println(jsonResponse);
		assertEquals(expectedJson, jsonResponse);
	}
	
	void onError(Throwable t) {
		System.out.println(t.getMessage());
		assertTrue(t instanceof RuntimeException);
	}
	

	List<InvoiceMetaData> getMetaDataList(Function<String, List<InvoiceMetaData>> doReturn, Supplier<Answer<String>> doAnswer) {
		// find the invoices for store 100
		// that are on trailer YT-1300
		InvoiceActor dest = InvoiceActor.builder()
				.named("100")
				.as(InvoiceActorType.STORE).build();

		String trailer = "YT-1300";

		Mockito.when(dao.findInvoiceByDestination(Mockito.any(InvoiceActor.class), Mockito.anyString()))
			.thenReturn(doReturn.apply(trailer));

		Mockito.when(dao.retrieveInvoiceDocumentByIdentifier(Mockito.anyString()))
			.thenAnswer(doAnswer.get());

		return dao.findInvoiceByDestination(dest, trailer);
	}


	List<InvoiceMetaData> validReturn(String trailer) {
		return Arrays.asList(InvoiceData.getMeta("1", "100", trailer), 
				InvoiceData.getMeta("2", "100", trailer));
	}

	Answer<String> jsonAnswer() {
		return new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				Object[] objArray = invocation.getArguments();
				String id = (String) objArray[0];
				return InvoiceData.getJsonDoc(id);
			}

		};
	}
	
	Answer<String> jsonAnswerWithException() {
		return new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				throw new RuntimeException("oops!");
			}

		};
	}
}
