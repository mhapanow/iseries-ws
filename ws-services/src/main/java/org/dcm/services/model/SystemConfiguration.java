package org.dcm.services.model;

import java.io.Serializable;

public class SystemConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;

	private int authTokenValidityInDays;
	private int defaultToRange;
	private int defaultFromRange;
	private String defaultLang;
	
	private String iSeriesServer;
	private String iSeriesUser;
	private String DTAQLib;
	private String dataLib;
	private String InDTAQName;
	private String OutDTAQName;
	private String serverInDTAQName;
	private String serverOutDTAQName;
	private String defPath;
	private String charset;
	private int consumers;
	private boolean logFull = true;
	
	public int getAuthTokenValidityInDays() {
		return authTokenValidityInDays;
	}
	public void setAuthTokenValidityInDays(int authTokenValidityInDays) {
		this.authTokenValidityInDays = authTokenValidityInDays;
	}
	public int getDefaultToRange() {
		return defaultToRange;
	}
	public void setDefaultToRange(int defaultToRange) {
		this.defaultToRange = defaultToRange;
	}
	public int getDefaultFromRange() {
		return defaultFromRange;
	}
	public void setDefaultFromRange(int defaultFromRange) {
		this.defaultFromRange = defaultFromRange;
	}
	public String getDefaultLang() {
		return defaultLang;
	}
	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
	}
	public String getiSeriesServer() {
		return iSeriesServer;
	}
	public void setiSeriesServer(String iSeriesServer) {
		this.iSeriesServer = iSeriesServer;
	}
	public String getiSeriesUser() {
		return iSeriesUser;
	}
	public void setiSeriesUser(String iSeriesUser) {
		this.iSeriesUser = iSeriesUser;
	}
	public String getDTAQLib() {
		return DTAQLib;
	}
	public void setDTAQLib(String dTAQLib) {
		DTAQLib = dTAQLib;
	}
	public String getDataLib() {
		return dataLib;
	}
	public void setDataLib(String dataLib) {
		this.dataLib = dataLib;
	}
	public String getInDTAQName() {
		return InDTAQName;
	}
	public void setInDTAQName(String inDTAQName) {
		InDTAQName = inDTAQName;
	}
	public String getOutDTAQName() {
		return OutDTAQName;
	}
	public void setOutDTAQName(String outDTAQName) {
		OutDTAQName = outDTAQName;
	}
	public String getServerInDTAQName() {
		return serverInDTAQName;
	}
	public void setServerInDTAQName(String serverInDTAQName) {
		this.serverInDTAQName = serverInDTAQName;
	}
	public String getServerOutDTAQName() {
		return serverOutDTAQName;
	}
	public void setServerOutDTAQName(String serverOutDTAQName) {
		this.serverOutDTAQName = serverOutDTAQName;
	}
	public String getDefPath() {
		return defPath;
	}
	public void setDefPath(String defPath) {
		this.defPath = defPath;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public int getConsumers() {
		return consumers;
	}
	public void setConsumers(int consumers) {
		this.consumers = consumers;
	}
	public boolean isLogFull() {
		return logFull;
	}
	public void setLogFull(boolean logFull) {
		this.logFull = logFull;
	}

}
