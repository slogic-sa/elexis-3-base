package at.medevit.elexis.agenda.ui.function;

import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.agenda.ui.rcprap.SingleSourceUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class CheckForUpdatesTimerTask extends TimerTask {
	
	private LoadEventsFunction loadEventsFunction;
	
	public CheckForUpdatesTimerTask(LoadEventsFunction loadEventsFunction){
		this.loadEventsFunction = loadEventsFunction;
	}
	
	@Override
	public void run(){
		long currentLastUpdate =
			CoreModelServiceHolder.get().getHighestLastUpdate(IAppointment.class);
		if (loadEventsFunction.knownLastUpdate != 0
			&& loadEventsFunction.knownLastUpdate < currentLastUpdate) {
			// TODO throws NPE in rap
			if(!SingleSourceUtil.isRap()) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run(){
						loadEventsFunction.scriptingHelper.refetchEvents();
					}
				});
			}

		}
	}
	
}
