package simElectricity.EnergyNet;

import simElectricity.Common.SEUtils;

public class EnergyNetThread extends Thread{
	public volatile boolean alive;
	private volatile boolean processing;
	private volatile boolean needOptimize;
	
	private volatile long duration;
	private long startAt;
	
	private EnergyNet energyNet;
	
	public static EnergyNetThread create(int dimID, EnergyNet energyNet){
		EnergyNetThread energyNetThread = new EnergyNetThread();
		energyNetThread.setName("SEEnergyNet_DIM" + String.valueOf(dimID));
		energyNetThread.energyNet = energyNet;
		energyNetThread.alive = true;
		energyNetThread.processing = false;
		energyNetThread.start();
		
		SEUtils.logInfo("EnergyNet thread for DIM " + String.valueOf(dimID) + " has been created!", SEUtils.simulator);
		return energyNetThread;
	}
		
	public void wakeUp(boolean needsOptimize){
		this.needOptimize = needsOptimize;
		this.interrupt();
	}
	
	public void notifyServerShuttingDown(){
		this.alive = false;
	}
	
	public boolean isWorking(){
		return processing;
	}
	
	public long lastDuration(){
		return duration;
	}
	
	@Override
	public void run() {
		while(alive){
			try {			
				SEUtils.logInfo(this.getName() + " Sleep", SEUtils.simulator);
				while (alive){
					this.sleep(1);
				}
			} catch (InterruptedException e) {
				SEUtils.logInfo(this.getName() + " wake up", SEUtils.simulator);
				
				if (!alive)
					break;
				
				this.processing = true;
				SEUtils.logInfo(this.getName() + " Started", SEUtils.simulator);
				startAt = System.currentTimeMillis();
				energyNet.runSimulator(needOptimize);
				SEUtils.logInfo(this.getName() + " Done", SEUtils.simulator);
				duration = (System.currentTimeMillis() - startAt);
				energyNet.executeHandlers();
				this.processing = false;
			}
		}
		SEUtils.logInfo(this.getName() + " is shutting down", SEUtils.general);
	}
	
}
