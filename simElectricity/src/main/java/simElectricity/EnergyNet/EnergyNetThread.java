package simElectricity.EnergyNet;

import simElectricity.Common.SEUtils;

public class EnergyNetThread extends Thread{
	private volatile boolean alive;
	private volatile boolean processing;
	private volatile boolean needOptimize;
	
	private EnergyNet energyNet;
	
	public static EnergyNetThread create(int dimID, EnergyNet energyNet){
		EnergyNetThread energyNetThread = new EnergyNetThread();
		energyNetThread.setName("SEEnergyNet_DIM" + String.valueOf(dimID));
		energyNetThread.energyNet = energyNet;
		energyNetThread.alive = true;
		energyNetThread.start();
		
		SEUtils.logInfo("EnergyNet thread for DIM " + String.valueOf(dimID) + " has been created!", SEUtils.simulator);
		return energyNetThread;
	}
		
	public void wakeUp(boolean needsOptimize){
		this.needOptimize = needsOptimize;
		this.interrupt();
	}
	
	public void terminate(){
		this.alive = false;
		this.interrupt();
	}
	
	public boolean isWorking(){
		return processing;
	}
	
	@Override
	public void run() {
		while(alive){
			try {			
				SEUtils.logInfo(this.getName() + " Sleep", SEUtils.simulator);
				this.join();	//Hangs the thread until interrupt
			} catch (InterruptedException e) {
				SEUtils.logInfo(this.getName() + " wake up", SEUtils.simulator);
				
				if (!alive)
					break;
				
				this.processing = true;
				SEUtils.logInfo(this.getName() + " Started", SEUtils.simulator);
				energyNet.runSimulator(needOptimize);
				SEUtils.logInfo(this.getName() + " Done", SEUtils.simulator);
				energyNet.executeHandlers();
				this.processing = false;
			}
		}
		SEUtils.logInfo(this.getName() + " is shutting down", SEUtils.simulator);
	}
	
}
