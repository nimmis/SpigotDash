package de.tobias.spigotdash;

import de.tobias.simpsocserv.external.StaticHTTPRequestHandler;
import de.tobias.spigotdash.backend.dataCollectors.DataCollectionManager;
import de.tobias.spigotdash.backend.io.WebsocketRequestHandlers.AuthenticationRequestHandler;
import de.tobias.spigotdash.backend.io.WebsocketRequestHandlers.CacheHistoryRequestHandler;
import de.tobias.spigotdash.backend.io.WebsocketRequestHandlers.DataCollectionRequestHandler;
import de.tobias.spigotdash.backend.logging.fieldLogger;
import de.tobias.spigotdash.backend.logging.globalLogger;
import de.tobias.spigotdash.backend.storage.CacheStore;
import de.tobias.spigotdash.backend.storage.JavaObjectJsonStore;
import de.tobias.spigotdash.backend.storage.UserStore;
import de.tobias.spigotdash.backend.utils.GlobalVariableStore;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.SecretKey;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Date;

public class main extends JavaPlugin {

	public void onEnable() {
		GlobalVariableStore.PLUGIN_STARTUP_TIMESTAMP = System.currentTimeMillis();
		try {
			GlobalVariableStore.pl = this;
			new globalLogger(null);
			globalLogger.constructed.activateDevDebug(); //TODO REMOVE FOR PRODUCTION
			globalLogger.constructed.getDebugFields().add("!HTTPSRV");
			globalLogger.constructed.getDebugFields().add("!SOCREQ1H");
			fieldLogger thisLogger = new fieldLogger("INIT", globalLogger.constructed);

			thisLogger.INFO("Starting up Plugin...", 0);

			thisLogger.WARNING("It seems you have enabled the Debug Log!", 1);
			thisLogger.WARNING("Please never share information from here to someone unknown!", 1);

			GlobalVariableStore.userJSONStore = new JavaObjectJsonStore(UserStore.class, new File(this.getDataFolder(), "test.json"));
			GlobalVariableStore.userJSONStore.loadOrCreate();

			GlobalVariableStore.cacheJSONStore = new JavaObjectJsonStore(CacheStore.class, new File(this.getDataFolder(), "cache.json"));
			GlobalVariableStore.cacheJSONStore.loadOrCreate();

			GlobalVariableStore.serverManager.start();

			GlobalVariableStore.serverManager.simpServer.addHTTPRequestHandler(new StaticHTTPRequestHandler("/*", new File(this.getDataFolder(), "www")));
			GlobalVariableStore.serverManager.simpServer.addSimpleSocketRequestHandler(new AuthenticationRequestHandler());
			GlobalVariableStore.serverManager.simpServer.addSimpleSocketRequestHandler(new DataCollectionRequestHandler());
			GlobalVariableStore.serverManager.simpServer.addSimpleSocketRequestHandler(new CacheHistoryRequestHandler());

			DataCollectionManager.initAllCollectors();
			DataCollectionManager.initCacheTask();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onDisable() {
		GlobalVariableStore.serverManager.stop();
		GlobalVariableStore.userJSONStore.save();

		GlobalVariableStore.getCacheStore().LAST_SAVED = new Date();
		GlobalVariableStore.cacheJSONStore.save();
	}
}
