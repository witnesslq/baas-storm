package com.ai.baas.storm.message;

import org.apache.commons.lang.StringUtils;

import com.ai.baas.storm.util.BaseConstants;


public class RecordFmt {
	private RecordFmtKey recordFmtKey;
	private String tenantId;
	private String serviceType;
	private String source;
	private String formatType;
	private Integer fieldSerial;
	private String fieldName;
	private String fieldCode;
	private String dataType;

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

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

	public Integer getFieldSerial() {
		return fieldSerial;
	}

	public void setFieldSerial(Integer fieldSerial) {
		this.fieldSerial = fieldSerial;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public static class RecordFmtKey {
		private String tenantId;
		private String serviceType;
		private String source;
		
		public RecordFmtKey(String tenantId,String serviceType,String source){
			this.tenantId = tenantId;
			this.serviceType = serviceType;
			this.source = source;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((serviceType == null) ? 0 : serviceType.hashCode());
			result = prime * result
					+ ((source == null) ? 0 : source.hashCode());
			result = prime * result
					+ ((tenantId == null) ? 0 : tenantId.hashCode());
			return result;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RecordFmtKey other = (RecordFmtKey) obj;
			if (serviceType == null) {
				if (other.serviceType != null)
					return false;
			} else if (!serviceType.equals(other.serviceType))
				return false;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			if (tenantId == null) {
				if (other.tenantId != null)
					return false;
			} else if (!tenantId.equals(other.tenantId))
				return false;
			return true;
		}


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
	}
	
	public RecordFmtKey getRecordFmtKey(){
		if (recordFmtKey == null){
			recordFmtKey = new RecordFmtKey(tenantId,serviceType,source);
		}
		return recordFmtKey;
	}

}
