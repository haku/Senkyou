package com.vaguehope.senkyou.reporter;

import com.vaguehope.senkyou.DataStore;

public class DataStoreReporter implements ReportProvider {

	private final DataStore dataStore;

	public DataStoreReporter (DataStore dataStore) {
		this.dataStore = dataStore;
	}
	
	@Override
	public void appendReport (StringBuilder r) {
		this.dataStore.report(r);
	}

}
