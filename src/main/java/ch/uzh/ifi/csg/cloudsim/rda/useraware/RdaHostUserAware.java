package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;

import ch.uzh.ifi.csg.cloudsim.rda.RdaHost;
import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisioner;

public class RdaHostUserAware extends RdaHost implements
		UserAwareHost {

	/**
	 * Instantiates a new host.
	 * 
	 * @param id
	 *            the id
	 * @param ramProvisioner
	 *            the ram provisioner
	 * @param bwProvisioner
	 *            the bw provisioner
	 * @param storage
	 *            the storage
	 * @param peList
	 *            the pe list
	 * @param vmScheduler
	 *            the VM scheduler
	 */
	public RdaHostUserAware(int id, RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner, StorageIOProvisioner sProvisioner,
			long storage, List<? extends Pe> peList, VmScheduler vmScheduler,
			double scarcitySchedulingInterval) {
		super(id, ramProvisioner, bwProvisioner, sProvisioner, storage, peList,
				vmScheduler, scarcitySchedulingInterval);
	}

	public Map<String, Float> getUserPriorities(double currentTime) {

		return ((RdaUserAwareVmScheduler) getVmScheduler())
				.getUserPriorities(currentTime, getVmList());

	}

	public double updateVmsProcessing(double currentTime,
			Map<String, Float> priorities) {
		((RdaUserAwareVmScheduler) getVmScheduler())
				.allocateResourcesForAllVms(currentTime, getVmList(),
						priorities);

		double smallerTime = Double.MAX_VALUE;
		for (Vm vm : getVmList()) {
			double time = ((RdaVm) vm).updateVmProcessing(currentTime,
					getVmScheduler().getAllocatedMipsForVm(vm),
					((RdaVm) vm).getCurrentAllocatedBwFine(),
					((RdaVm) vm).getCurrentAllocatedStorageIO());
			if (time > 0.0 && time < smallerTime) {
				smallerTime = time;
			}
		}

		smallerTime = checkForScarcity(smallerTime, currentTime);

		updateHostState(currentTime);

		return smallerTime;
	}

	@Override
	public double updateVmsProcessing(double currentTime) {
		throw new UnsupportedOperationException();
	}

}
