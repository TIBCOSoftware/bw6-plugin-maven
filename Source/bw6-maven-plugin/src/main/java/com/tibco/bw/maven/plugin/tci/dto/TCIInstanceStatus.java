package com.tibco.bw.maven.plugin.tci.dto;

public class TCIInstanceStatus {
	
	private boolean running;
	private boolean starting;
	private boolean failed;
	private boolean desired;
	private boolean vpn;
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isStarting() {
		return starting;
	}
	public void setStarting(boolean starting) {
		this.starting = starting;
	}
	public boolean isFailed() {
		return failed;
	}
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	public boolean isDesired() {
		return desired;
	}
	public void setDesired(boolean desired) {
		this.desired = desired;
	}
	public boolean isVpn() {
		return vpn;
	}
	public void setVpn(boolean vpn) {
		this.vpn = vpn;
	}
	
	

}
