package com.ai.baas.storm.duplicate;

import java.io.Serializable;

public class DuplicateChecking implements Serializable {

	private static final long serialVersionUID = -1050401238763087301L;
	private String tenantId;
	private String serviceType;
	private String source;
	private String dupKey;
	private String tbSuffixKey;

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

	public String getDupKey() {
		return dupKey;
	}

	public void setDupKey(String dupKey) {
		this.dupKey = dupKey;
	}

	public String getTbSuffixKey() {
		return tbSuffixKey;
	}

	public void setTbSuffixKey(String tbSuffixKey) {
		this.tbSuffixKey = tbSuffixKey;
	}
	
}
