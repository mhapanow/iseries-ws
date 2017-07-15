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
	private String iSeriesPass;
	private String DTAQLib;
	private String dataLib;
	private String InDTAQName;
	private String OutDTAQName;
	private String ServerInDTAQName;
	private String ServerOutDTAQName;
	private String defPath;
	private String charset;
	private int consumers;

	public int getAuthTokenValidityInDays() {
		return authTokenValidityInDays;
	}

	public void setAuthTokenValidityInDays(int authTokenValidityInDays) {
		this.authTokenValidityInDays = authTokenValidityInDays;
	}

	public int getDefaultFromRange() {
		return defaultFromRange;
	}

	public void setDefaultFromRange(int defaultFromRange) {
		this.defaultFromRange = defaultFromRange;
	}

	public int getDefaultToRange() {
		return defaultToRange;
	}

	public void setDefaultToRange(int defaultToRange) {
		this.defaultToRange = defaultToRange;
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

	public String getiSeriesPass() {
		return iSeriesPass;
	}

	public void setiSeriesPass(String iSeriesPass) {
		this.iSeriesPass = iSeriesPass;
	}

	public String getDTAQLib() {
		return DTAQLib;
	}

	public void setDTAQLib(String dTAQLib) {
		DTAQLib = dTAQLib;
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

	public String getDataLib() {
		return dataLib;
	}

	public void setDataLib(String dataLib) {
		this.dataLib = dataLib;
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

	public String getServerInDTAQName() {
		return ServerInDTAQName;
	}

	public void setServerInDTAQName(String serverInDTAQName) {
		ServerInDTAQName = serverInDTAQName;
	}

	public String getServerOutDTAQName() {
		return ServerOutDTAQName;
	}

	public void setServerOutDTAQName(String serverOutDTAQName) {
		ServerOutDTAQName = serverOutDTAQName;
	}

	public int getConsumers() {
		return consumers;
	}

	public void setConsumers(int consumers) {
		this.consumers = consumers;
	}
	
}
