package coordints;

import coordints.event.client.ModClientEvents;
import net.fabricmc.api.ClientModInitializer;

public class CoordintsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientEvents.init();
	}
}