package simelectricity.essential.common;

public interface ISEFacing8 {
	/**
	 * @param facing a number between 0 and 7 (inclusive), represents the rotation of the block,
	 * calculated from the yaw of the placer. </p>
	 * facing = 8 - MathHelper.floor(placer.rotationYaw * 8.0F / 360.0F + 0.5D) & 7;
	 */
	void setFacingOnPlacement(int facing);
	
	/**
	 * @return a number between 0 and 7 (inclusive), represents the rotation
	 */
	int getRotation();
}
