package com.n26.service;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.n26.dto.StatisticsDTO;
import com.n26.dto.TransactionDTO;
import com.n26.exceptions.OutdatedTransactionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsServiceTest {

	private static final int SLEEP_TIME = 1000;

	@Autowired
	private StatisticsService statisticsService;

	@Autowired
	private TransactionsService transactionsService;

	@Test
	public void test_Register_Transactions() throws OutdatedTransactionException {
		StatisticsDTO currentStats=statisticsService.getLatestStats();
		final double tmepSum=currentStats.getSum();
		double dummy = 10;
		transactionsService.registerTransaction(new TransactionDTO(dummy, System.currentTimeMillis()));
		sleep(SLEEP_TIME);
		assertEquals(tmepSum+10,statisticsService.getLatestStats().getSum(),0);
	}

	@Test
	public void validate_outDated_Transaction() {
		// StatisticsDTO stats = statisticsService.getLatestStats();
		try {
			transactionsService
					.registerTransaction(new TransactionDTO(Double.valueOf(20), System.currentTimeMillis() - 60000));
		} catch (OutdatedTransactionException e) {
			e.printStackTrace();
		}
		// assertEquals(null, statisticsService.getLatestStats());
	}

	/*
	 * @Test public void whenValidTimestamp_computeStatistics() { int threads = 10;
	 * StatisticsDTO temp=statisticsService.getLatestStats(); ExecutorService
	 * service = Executors.newFixedThreadPool(threads); for (int i = 0; i < threads;
	 * i++) { service.submit(() -> { try { transactionsService
	 * .registerTransaction(new TransactionDTO(Double.valueOf(5),
	 * System.currentTimeMillis() - 10000)); } catch (OutdatedTransactionException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } }); }
	 * StatisticsDTO stats = statisticsService.getLatestStats();
	 * assertEquals(temp.getCount()+10, stats.getCount());
	 * 
	 * assertEquals(summary.getCount(), 10l); assertEquals(summary.getMax(), 65.5,
	 * 0.0); assertEquals(summary.getMin(), 2.8, 0.0);
	 * assertEquals(summary.getAvg(), 151.3 / 10, 0.0); }
	 */

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	@Test
	public void computeStatsConcurrently() {
		TransactionDTO[] transactions = { new TransactionDTO(Double.valueOf(5), System.currentTimeMillis()),
				new TransactionDTO(Double.valueOf(20), System.currentTimeMillis() + 1),
				new TransactionDTO(Double.valueOf(40), System.currentTimeMillis() + 2),
				new TransactionDTO(Double.valueOf(-10), System.currentTimeMillis() + 3),
				new TransactionDTO(Double.valueOf(30), System.currentTimeMillis() + 4), };
		addTransactionsConncurrently(transactions);
		sleep(SLEEP_TIME);
		assertEquals(85, statisticsService.getLatestStats().getSum(), 0);
	}

	private ExecutorService addTransactionsConncurrently(final TransactionDTO[] transactions) {
		ExecutorService executor = Executors.newFixedThreadPool(3);
		for (final TransactionDTO transaction : transactions) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						transactionsService.registerTransaction(transaction);
					} catch (OutdatedTransactionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			executor.execute(thread);
		}
		return executor;
	}

}
