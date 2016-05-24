package com.ai.baas.storm.failbill;

import java.io.Serializable;

/**
 * 错单实体类
 * @author majun
 *
 */
public class FailureBill implements Serializable {
	private static final long serialVersionUID = -865272664278537978L;
	private String tenantId; //租户ID
	private String serviceType; //业务类型
	private String source;   //数据来源 
	private String bsn;      //批次号
	private String sn;       //唯一标示
	private String accountPeriod;//账期
	private String arrivalTime;  //报文到达时间
	private String failStep;     //错误环节 BMC-unpackingbolt
	private String failCode;     //错误编号 BMC-000001
	private String failReason;   //错误原因，java异常堆栈
	private String failPakcet;   //错误报文信息
	private String failDate; //YYYYMMDDSSHHMM

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getBsn() {
		return bsn;
	}

	public void setBsn(String bsn) {
		this.bsn = bsn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getAccountPeriod() {
		return accountPeriod;
	}

	public void setAccountPeriod(String accountPeriod) {
		this.accountPeriod = accountPeriod;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getFailStep() {
		return failStep;
	}

	public void setFailStep(String failStep) {
		this.failStep = failStep;
	}

	public String getFailCode() {
		return failCode;
	}

	public void setFailCode(String failCode) {
		this.failCode = failCode;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getFailPakcet() {
		return failPakcet;
	}

	public void setFailPakcet(String failPakcet) {
		this.failPakcet = failPakcet;
	}

	public String getFailDate() {
		return failDate;
	}

	public void setFailDate(String failDate) {
		this.failDate = failDate;
	}
	
}
