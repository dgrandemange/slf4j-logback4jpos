package org.jpos.jposext.logging.slf4j.logback;

import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import org.jpos.transaction.Context;

@SuppressWarnings("unchecked")
class DemoThread implements Runnable {
	private DemoQBean parent;
	
	public DemoThread(DemoQBean parent) {
		this.parent = parent;
	}

	@SuppressWarnings("rawtypes")
	public void run() {
		try {
			while (getParent().running()) {
				
				Space sp = SpaceFactory.getSpace("tspace:default");
				Context ctx = new Context();
				sp.out("myTxQueue", ctx, 10000);

				Thread.sleep(2500);
			}
		} catch (InterruptedException e) {
		}
	}
	
	protected synchronized DemoQBean getParent() {
		return parent;
	}
}
