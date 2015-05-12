package com.fftw.bfa.admin.seam.model;
// Generated Jan 10, 2008 5:29:55 PM by Hibernate Tools 3.2.0.b10

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * TradeRecord generated by hbm2java
 */
@Entity
@Table(name = "TRADE_RECORD", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"CREATION_DATE", "PLATFORM_ID",
				"EXEC_ID"}),
		@UniqueConstraint(columnNames = {"CREATION_DATE", "TRADE_SEQ_NUM"})})
public class TradeRecord implements java.io.Serializable {

	private long tradeRecordId;
	private Platform platform;
	private Date creationDate;
	private long tradeSeqNum;
	private String execId;
	private short status;
	private short securityIdFlag;
	private String securityId;
	private String traderName;
	private short side;
	private BigDecimal quantity;
	private short priceDisplay;
	private BigDecimal price;
	private Date settleDate;
	private String account;
	private short productCode;
	private short sourceCode;
	private String broker;
	private String primeBroker;
	private int ticketNumber;
	private Short returnCode;
	private String errorMsg;
	private Date ackReceived;
	private Date acceptRejectReceived;
	private String processingStatus;
	private String tradingStrategy;
	private String transCode;
	private Date lastUpdated;

	public TradeRecord() {
	}

	public TradeRecord(long tradeRecordId, Platform platform,
			Date creationDate, long tradeSeqNum, String execId, short status,
			short securityIdFlag, String securityId, short side,
			BigDecimal quantity, short priceDisplay, BigDecimal price,
			Date settleDate, String account, short productCode,
			short sourceCode, String broker, String primeBroker,
			int ticketNumber, Date lastUpdated) {
		this.tradeRecordId = tradeRecordId;
		this.platform = platform;
		this.creationDate = creationDate;
		this.tradeSeqNum = tradeSeqNum;
		this.execId = execId;
		this.status = status;
		this.securityIdFlag = securityIdFlag;
		this.securityId = securityId;
		this.side = side;
		this.quantity = quantity;
		this.priceDisplay = priceDisplay;
		this.price = price;
		this.settleDate = settleDate;
		this.account = account;
		this.productCode = productCode;
		this.sourceCode = sourceCode;
		this.broker = broker;
		this.primeBroker = primeBroker;
		this.ticketNumber = ticketNumber;
		this.lastUpdated = lastUpdated;
	}
	public TradeRecord(long tradeRecordId, Platform platform,
			Date creationDate, long tradeSeqNum, String execId, short status,
			short securityIdFlag, String securityId, String traderName,
			short side, BigDecimal quantity, short priceDisplay,
			BigDecimal price, Date settleDate, String account,
			short productCode, short sourceCode, String broker,
			String primeBroker, int ticketNumber, Short returnCode,
			String errorMsg, Date ackReceived, Date acceptRejectReceived,
			String processingStatus, String tradingStrategy, String transCode,
			Date lastUpdated) {
		this.tradeRecordId = tradeRecordId;
		this.platform = platform;
		this.creationDate = creationDate;
		this.tradeSeqNum = tradeSeqNum;
		this.execId = execId;
		this.status = status;
		this.securityIdFlag = securityIdFlag;
		this.securityId = securityId;
		this.traderName = traderName;
		this.side = side;
		this.quantity = quantity;
		this.priceDisplay = priceDisplay;
		this.price = price;
		this.settleDate = settleDate;
		this.account = account;
		this.productCode = productCode;
		this.sourceCode = sourceCode;
		this.broker = broker;
		this.primeBroker = primeBroker;
		this.ticketNumber = ticketNumber;
		this.returnCode = returnCode;
		this.errorMsg = errorMsg;
		this.ackReceived = ackReceived;
		this.acceptRejectReceived = acceptRejectReceived;
		this.processingStatus = processingStatus;
		this.tradingStrategy = tradingStrategy;
		this.transCode = transCode;
		this.lastUpdated = lastUpdated;
	}

	@Id
	@Column(name = "TRADE_RECORD_ID", unique = true, nullable = false)
	@NotNull
	public long getTradeRecordId() {
		return this.tradeRecordId;
	}

	public void setTradeRecordId(long tradeRecordId) {
		this.tradeRecordId = tradeRecordId;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLATFORM_ID", nullable = false)
	@NotNull
	public Platform getPlatform() {
		return this.platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	@Column(name = "CREATION_DATE", nullable = false, length = 23)
	@NotNull
	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "TRADE_SEQ_NUM", nullable = false)
	@NotNull
	public long getTradeSeqNum() {
		return this.tradeSeqNum;
	}

	public void setTradeSeqNum(long tradeSeqNum) {
		this.tradeSeqNum = tradeSeqNum;
	}

	@Column(name = "EXEC_ID", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getExecId() {
		return this.execId;
	}

	public void setExecId(String execId) {
		this.execId = execId;
	}

	@Column(name = "STATUS", nullable = false)
	@NotNull
	public short getStatus() {
		return this.status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	@Column(name = "SECURITY_ID_FLAG", nullable = false)
	@NotNull
	public short getSecurityIdFlag() {
		return this.securityIdFlag;
	}

	public void setSecurityIdFlag(short securityIdFlag) {
		this.securityIdFlag = securityIdFlag;
	}

	@Column(name = "SECURITY_ID", nullable = false, length = 24)
	@NotNull
	@Length(max = 24)
	public String getSecurityId() {
		return this.securityId;
	}

	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}

	@Column(name = "TRADER_NAME", length = 8)
	@Length(max = 8)
	public String getTraderName() {
		return this.traderName;
	}

	public void setTraderName(String traderName) {
		this.traderName = traderName;
	}

	@Column(name = "SIDE", nullable = false)
	@NotNull
	public short getSide() {
		return this.side;
	}

	public void setSide(short side) {
		this.side = side;
	}

	@Column(name = "QUANTITY", nullable = false, precision = 20)
	@NotNull
	public BigDecimal getQuantity() {
		return this.quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	@Column(name = "PRICE_DISPLAY", nullable = false)
	@NotNull
	public short getPriceDisplay() {
		return this.priceDisplay;
	}

	public void setPriceDisplay(short priceDisplay) {
		this.priceDisplay = priceDisplay;
	}

	@Column(name = "PRICE", nullable = false, precision = 15, scale = 8)
	@NotNull
	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Column(name = "SETTLE_DATE", nullable = false, length = 23)
	@NotNull
	public Date getSettleDate() {
		return this.settleDate;
	}

	public void setSettleDate(Date settleDate) {
		this.settleDate = settleDate;
	}

	@Column(name = "ACCOUNT", nullable = false, length = 10)
	@NotNull
	@Length(max = 10)
	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Column(name = "PRODUCT_CODE", nullable = false)
	@NotNull
	public short getProductCode() {
		return this.productCode;
	}

	public void setProductCode(short productCode) {
		this.productCode = productCode;
	}

	@Column(name = "SOURCE_CODE", nullable = false)
	@NotNull
	public short getSourceCode() {
		return this.sourceCode;
	}

	public void setSourceCode(short sourceCode) {
		this.sourceCode = sourceCode;
	}

	@Column(name = "BROKER", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getBroker() {
		return this.broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	@Column(name = "PRIME_BROKER", nullable = false, length = 10)
	@NotNull
	@Length(max = 10)
	public String getPrimeBroker() {
		return this.primeBroker;
	}

	public void setPrimeBroker(String primeBroker) {
		this.primeBroker = primeBroker;
	}

	@Column(name = "TICKET_NUMBER", nullable = false)
	@NotNull
	public int getTicketNumber() {
		return this.ticketNumber;
	}

	public void setTicketNumber(int ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	@Column(name = "RETURN_CODE")
	public Short getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(Short returnCode) {
		this.returnCode = returnCode;
	}

	@Column(name = "ERROR_MSG", length = 50)
	@Length(max = 50)
	public String getErrorMsg() {
		return this.errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Column(name = "ACK_RECEIVED", length = 23)
	public Date getAckReceived() {
		return this.ackReceived;
	}

	public void setAckReceived(Date ackReceived) {
		this.ackReceived = ackReceived;
	}

	@Column(name = "ACCEPT_REJECT_RECEIVED", length = 23)
	public Date getAcceptRejectReceived() {
		return this.acceptRejectReceived;
	}

	public void setAcceptRejectReceived(Date acceptRejectReceived) {
		this.acceptRejectReceived = acceptRejectReceived;
	}

	@Column(name = "PROCESSING_STATUS", length = 30)
	@Length(max = 30)
	public String getProcessingStatus() {
		return this.processingStatus;
	}

	public void setProcessingStatus(String processingStatus) {
		this.processingStatus = processingStatus;
	}

	@Column(name = "TRADING_STRATEGY", length = 50)
	@Length(max = 50)
	public String getTradingStrategy() {
		return this.tradingStrategy;
	}

	public void setTradingStrategy(String tradingStrategy) {
		this.tradingStrategy = tradingStrategy;
	}

	@Column(name = "TRANS_CODE", length = 1)
	@Length(max = 1)
	public String getTransCode() {
		return this.transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	@Column(name = "LAST_UPDATED", nullable = false, length = 23)
	@NotNull
	public Date getLastUpdated() {
		return this.lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}