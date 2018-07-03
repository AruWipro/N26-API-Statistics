package com.n26.dto;

import java.util.Collection;
import java.util.DoubleSummaryStatistics;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsDTO {
	private double sum;

	private double avg;

	private double max;

	private double min;

	private long count;

	@JsonIgnore
	public StatisticsDTO(Collection<TransactionDTO> validTransactions) {
		if (!CollectionUtils.isEmpty(validTransactions)) {
			DoubleSummaryStatistics stats = validTransactions.stream().mapToDouble(TransactionDTO::getAmount)
					.summaryStatistics();
			this.setCount(stats.getCount());
			this.setSum(stats.getSum());
			this.setAvg(stats.getAverage());
			this.setMin(stats.getMin());
			this.setMax(stats.getMax());

		}
	}
	
	public StatisticsDTO setValues(StatisticsDTO statistics) {
		this.avg=statistics.getAvg();
		this.count=statistics.getCount();
		this.max=statistics.getMax();
		this.min=statistics.getMin();
		this.sum=statistics.getSum();
		return this;
		
	}

}
