package com.fftw.bfa.admin.seam.model;
// Generated Jan 9, 2008 3:43:19 PM by Hibernate Tools 3.2.0.b10

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * MarketCalendarId generated by hbm2java
 */
@Embeddable
public class MarketCalendarId implements java.io.Serializable {

	private String marketId;
	private Date closedOn;

	public MarketCalendarId() {
	}

	public MarketCalendarId(String marketId, Date closedOn) {
		this.marketId = marketId;
		this.closedOn = closedOn;
	}

	@Column(name = "MARKET_ID", nullable = false, length = 10)
	@NotNull
	@Length(max = 10)
	public String getMarketId() {
		return this.marketId;
	}

	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}

	@Column(name = "CLOSED_ON", nullable = false, length = 23)
	@NotNull
	public Date getClosedOn() {
		return this.closedOn;
	}

	public void setClosedOn(Date closedOn) {
		this.closedOn = closedOn;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof MarketCalendarId))
			return false;
		MarketCalendarId castOther = (MarketCalendarId) other;

		return ((this.getMarketId() == castOther.getMarketId()) || (this
				.getMarketId() != null
				&& castOther.getMarketId() != null && this.getMarketId()
				.equals(castOther.getMarketId())))
				&& ((this.getClosedOn() == castOther.getClosedOn()) || (this
						.getClosedOn() != null
						&& castOther.getClosedOn() != null && this
						.getClosedOn().equals(castOther.getClosedOn())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getMarketId() == null ? 0 : this.getMarketId().hashCode());
		result = 37 * result
				+ (getClosedOn() == null ? 0 : this.getClosedOn().hashCode());
		return result;
	}

}
