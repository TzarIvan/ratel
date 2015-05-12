package com.fftw.bfa.admin.seam.model;
// Generated Jan 10, 2008 5:29:55 PM by Hibernate Tools 3.2.0.b10

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Platform generated by hbm2java
 */
@Entity
@Table(name = "PLATFORM")
public class Platform implements java.io.Serializable {

	private String platformId;
	private String description;
	private Set<TradeRecord> tradeRecords = new HashSet<TradeRecord>(0);
	private Set<FuturesSymbolMapping> futuresSymbolMappings = new HashSet<FuturesSymbolMapping>(
			0);
	private Set<TradingStrategy> tradingStrategies = new HashSet<TradingStrategy>(
			0);

	public Platform() {
	}

	public Platform(String platformId) {
		this.platformId = platformId;
	}
	public Platform(String platformId, String description,
			Set<TradeRecord> tradeRecords,
			Set<FuturesSymbolMapping> futuresSymbolMappings,
			Set<TradingStrategy> tradingStrategies) {
		this.platformId = platformId;
		this.description = description;
		this.tradeRecords = tradeRecords;
		this.futuresSymbolMappings = futuresSymbolMappings;
		this.tradingStrategies = tradingStrategies;
	}

	@Id
	@Column(name = "PLATFORM_ID", unique = true, nullable = false, length = 5)
	@NotNull
	@Length(max = 5)
	public String getPlatformId() {
		return this.platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	@Column(name = "Description", length = 50)
	@Length(max = 50)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "platform")
	public Set<TradeRecord> getTradeRecords() {
		return this.tradeRecords;
	}

	public void setTradeRecords(Set<TradeRecord> tradeRecords) {
		this.tradeRecords = tradeRecords;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "platform")
	public Set<FuturesSymbolMapping> getFuturesSymbolMappings() {
		return this.futuresSymbolMappings;
	}

	public void setFuturesSymbolMappings(
			Set<FuturesSymbolMapping> futuresSymbolMappings) {
		this.futuresSymbolMappings = futuresSymbolMappings;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "platform")
	public Set<TradingStrategy> getTradingStrategies() {
		return this.tradingStrategies;
	}

	public void setTradingStrategies(Set<TradingStrategy> tradingStrategies) {
		this.tradingStrategies = tradingStrategies;
	}

}