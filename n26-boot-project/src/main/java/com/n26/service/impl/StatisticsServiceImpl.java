package com.n26.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.n26.dto.StatisticsDTO;
import com.n26.dto.TransactionDTO;
import com.n26.service.StatisticsService;

import lombok.Getter;

/**
 * The Class StatisticsServiceImpl. This class holds the transactions and also
 * has a scheduler built in which removes the old Statistics
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

	/** The log. */
	private static org.slf4j.Logger LOG = LoggerFactory.getLogger(StatisticsServiceImpl.class);

	/** The Constant POLLING_INTERVAL_RATE_MILLIS. */
	private static final int POLLING_INTERVAL_RATE_MILLIS = 1;

	/**
	 * The stats map. Stores the statistics Data
	 */
	private final Map<Long, StatisticsDTO> statsMap;

	/** The Constant VALID_TIME. */
	public static final int VALID_TIME_INTERVAL = 60 * 1000;

	/**
	 * The locker. Prevents concurrent writes
	 */
	private final Object locker;

	/**
	 * The temporary DTO which holds the Data when locking is acquired this data is
	 * assigned to finalStats.
	 */
	private StatisticsDTO transientDTO;

	/** The final Statistics DTO. */
	@Getter
	private StatisticsDTO finalStats;

	/**
	 * Instantiates a new statistics service impl.
	 */
	public StatisticsServiceImpl() {
		this.statsMap = new ConcurrentHashMap<Long, StatisticsDTO>();
		this.locker = new Object();
		this.transientDTO = new StatisticsDTO();
		this.finalStats = new StatisticsDTO();
	}

	/**
	 * Gets the stats.
	 *
	 * @return the stats
	 */
	/* private StatisticsDTO stats; */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.n26.service.StatisticsService#addTransaction(com.n26.dto.TransactionDTO)
	 */
	public void addTransaction(TransactionDTO transaction) {
		cleanUpOldData();
		long timestampInSeconds = roundDownToSeconds(transaction.getTimestamp());
		StatisticsDTO existingStats = statsMap.get(timestampInSeconds);
		if (existingStats == null) {
			existingStats = new StatisticsDTO();
			statsMap.put(timestampInSeconds, existingStats);
		}
		constructStatistics(existingStats, transaction.getAmount());
		/**
		 * Made the below code thread safe . What ever thread acquires lock on the
		 * 'locker' obj Can only be able to commit the changes to main list
		 */

		synchronized (locker) {
			constructStatistics(transientDTO, transaction.getAmount());
			// Does the actual update to the main Map
			commitStats();
		}

		// updateStatistics();

	}

	/**
	 * Clean up old data. This is a scheduler which runs for ever 1 mls. We do a
	 * sort of time stamps and we remove the obj corresponding to that time stamp,
	 * if it exceeds valid time interval
	 */
	@Scheduled(fixedRate = POLLING_INTERVAL_RATE_MILLIS)
	private void cleanUpOldData() {
		TreeMap<Long, StatisticsDTO> tree = new TreeMap(statsMap);
		List<Long> timeStampsList = new ArrayList<>(tree.keySet());
		Collections.sort(timeStampsList);
		if (!CollectionUtils.isEmpty(timeStampsList)) {
			Long timeStamp = timeStampsList.get(0);
			if (getElapsedDuration(timeStamp) > VALID_TIME_INTERVAL) {
				removeElementFromMap(timeStamp);
			}
		}
	}

	/**
	 * Gets the comparator.
	 *
	 * @return the comparator
	 */
	/*
	 * private void updateStatistics() { stats= new
	 * StatisticsDTO(validTransactions); }
	 */
	public static Comparator<TransactionDTO> getComparator() {
		return Comparator.comparing(TransactionDTO::getTimestamp);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.n26.service.StatisticsService#getLatestStats()
	 */
	@Override
	public StatisticsDTO getLatestStats() {
		return getFinalStats();
	}

	/**
	 * Round down the timestamp in seconds (drop the mills-seconds value).
	 *
	 * @param timestamp
	 *            given timestamp
	 * @return rounded timestamp
	 */
	public static long roundDownToSeconds(long timestamp) {
		return 1000 * (timestamp / 1000);
	}

	/**
	 * Construct statistics.
	 *
	 * @param statsData
	 *            the stats data
	 * @param amount
	 *            the amount
	 */
	private void constructStatistics(StatisticsDTO statsData, double amount) {
		statsData.setSum(statsData.getSum() + amount);
		statsData.setCount(statsData.getCount() + 1);
		statsData.setAvg(customDivide(statsData.getSum(), statsData.getCount()));
		if (amount > statsData.getMax()) {
			statsData.setMax(amount);
		}
		if (statsData.getCount() == 1 || amount < statsData.getMin()) {
			statsData.setMin(amount);
		}
	}

	/**
	 * Custom divide.
	 *
	 * @param value1
	 *            the value 1
	 * @param value2
	 *            the value 2
	 * @return the double
	 */
	public static double customDivide(double value1, double value2) {
		if (value2 == 0) {
			return 0;
		}
		BigDecimal value1Bd = BigDecimal.valueOf(value1);
		BigDecimal value2Bd = BigDecimal.valueOf(value2);
		return value1Bd.divide(value2Bd, RoundingMode.HALF_DOWN).doubleValue();
	}

	/**
	 * Commit stats.
	 */
	private void commitStats() {
		StatisticsDTO tempValue = this.finalStats;
		this.finalStats = this.transientDTO;
		tempValue.setValues(this.finalStats);
		this.transientDTO = tempValue;
	}

	/**
	 * Get the rounded elapsed time from the given timestamp until now.
	 * 
	 * @param roundedTimestamp
	 *            given timeseconds with milliseconds rounded down.
	 * @return elapsed time in seconds
	 */
	public static long getElapsedDuration(long roundedTimestamp) {
		return roundDownToSeconds(System.currentTimeMillis()) - roundedTimestamp;
	}

	/**
	 * Removes the from perodic stats.
	 *
	 * @param removeTimeStamp
	 *            the remove time stamp
	 */
	private void removeElementFromMap(long removeTimeStamp) {
		StatisticsDTO oldStats = statsMap.remove(removeTimeStamp);
		if (oldStats != null) {
			transientDTO.setSum(transientDTO.getSum() - oldStats.getSum());
			transientDTO.setCount(transientDTO.getCount() - oldStats.getCount());
			transientDTO.setAvg(customDivide(transientDTO.getSum(), transientDTO.getCount()));
			// we need to find the max and min. value from all periodic statistics in case
			// that the removed statistics was the min or max value.
			if (finalStats.getMin() == oldStats.getMin() || finalStats.getMax() == oldStats.getMax()) {
				double minValue = statsMap.isEmpty() ? 0d : Double.MAX_VALUE;
				double maxValue = 0;
				for (StatisticsDTO stats : statsMap.values()) {
					if (stats.getMin() < minValue) {
						minValue = stats.getMin();
					}
					if (stats.getMax() > maxValue) {
						maxValue = stats.getMax();
					}
				}
				transientDTO.setMin(minValue);
				transientDTO.setMax(maxValue);
			}
			commitStats();
		}

	}
}