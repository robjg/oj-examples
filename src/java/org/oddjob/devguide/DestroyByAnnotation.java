package org.oddjob.devguide;

import org.oddjob.arooa.life.Destroy;

public class DestroyByAnnotation {

	@Destroy
	public void goodbye() {
		System.out.println("You never loved me!");
	}
	
}
