package com.l2jfrozen.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.logging.LogManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.l2jfrozen.Config;
import com.l2jfrozen.FService;
import com.l2jfrozen.ServerType;
import com.l2jfrozen.crypt.nProtect;
import com.l2jfrozen.gameserver.ai.special.manager.AILoader;
import com.l2jfrozen.gameserver.cache.CrestCache;
import com.l2jfrozen.gameserver.cache.HtmCache;
import com.l2jfrozen.gameserver.communitybbs.Manager.ForumsBBSManager;
import com.l2jfrozen.gameserver.controllers.GameTimeController;
import com.l2jfrozen.gameserver.controllers.RecipeController;
import com.l2jfrozen.gameserver.controllers.TradeController;
import com.l2jfrozen.gameserver.datatables.GmListTable;
import com.l2jfrozen.gameserver.datatables.HeroSkillTable;
import com.l2jfrozen.gameserver.datatables.NobleSkillTable;
import com.l2jfrozen.gameserver.datatables.SkillTable;
import com.l2jfrozen.gameserver.datatables.csv.DoorTable;
import com.l2jfrozen.gameserver.datatables.csv.ExtractableItemsData;
import com.l2jfrozen.gameserver.datatables.csv.FishTable;
import com.l2jfrozen.gameserver.datatables.csv.MapRegionTable;
import com.l2jfrozen.gameserver.datatables.csv.NpcWalkerRoutesTable;
import com.l2jfrozen.gameserver.datatables.csv.StaticObjectsTable;
import com.l2jfrozen.gameserver.datatables.csv.SummonItemsData;
import com.l2jfrozen.gameserver.datatables.sql.ArmorSetsTable;
import com.l2jfrozen.gameserver.datatables.sql.CharNameTable;
import com.l2jfrozen.gameserver.datatables.sql.CharTemplateTable;
import com.l2jfrozen.gameserver.datatables.sql.ClanTable;
import com.l2jfrozen.gameserver.datatables.sql.ItemTable;
import com.l2jfrozen.gameserver.datatables.sql.L2PetDataTable;
import com.l2jfrozen.gameserver.datatables.sql.LevelUpData;
import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.datatables.sql.SkillTreeTable;
import com.l2jfrozen.gameserver.datatables.sql.SpawnTable;
import com.l2jfrozen.gameserver.datatables.sql.TeleportLocationTable;
import com.l2jfrozen.gameserver.datatables.sql.TerritoryTable;
import com.l2jfrozen.gameserver.datatables.xml.AccessLevels;
import com.l2jfrozen.gameserver.datatables.xml.AdminCommandAccessRights;
import com.l2jfrozen.gameserver.datatables.xml.AugmentationData;
import com.l2jfrozen.gameserver.datatables.xml.ExperienceData;
import com.l2jfrozen.gameserver.datatables.xml.GlobalDropData;
import com.l2jfrozen.gameserver.datatables.xml.HennaTable;
import com.l2jfrozen.gameserver.datatables.xml.L2Multisell;
import com.l2jfrozen.gameserver.datatables.xml.NewbieGuideBuffTable;
import com.l2jfrozen.gameserver.datatables.xml.RecipeTable;
import com.l2jfrozen.gameserver.datatables.xml.SkillSpellbookTable;
import com.l2jfrozen.gameserver.datatables.xml.ZoneData;
import com.l2jfrozen.gameserver.geo.GeoData;
import com.l2jfrozen.gameserver.geo.geoeditorcon.GeoEditorListener;
import com.l2jfrozen.gameserver.geo.pathfinding.PathFinding;
import com.l2jfrozen.gameserver.handler.AdminCommandHandler;
import com.l2jfrozen.gameserver.handler.AutoAnnouncementHandler;
import com.l2jfrozen.gameserver.handler.AutoChatHandler;
import com.l2jfrozen.gameserver.handler.ItemHandler;
import com.l2jfrozen.gameserver.handler.SkillHandler;
import com.l2jfrozen.gameserver.handler.UserCommandHandler;
import com.l2jfrozen.gameserver.handler.VoicedCommandHandler;
import com.l2jfrozen.gameserver.idfactory.IdFactory;
import com.l2jfrozen.gameserver.managers.AuctionManager;
import com.l2jfrozen.gameserver.managers.AwayManager;
import com.l2jfrozen.gameserver.managers.BoatManager;
import com.l2jfrozen.gameserver.managers.CastleManager;
import com.l2jfrozen.gameserver.managers.CastleManorManager;
import com.l2jfrozen.gameserver.managers.ClanHallManager;
import com.l2jfrozen.gameserver.managers.ClassDamageManager;
import com.l2jfrozen.gameserver.managers.CoupleManager;
import com.l2jfrozen.gameserver.managers.CursedWeaponsManager;
import com.l2jfrozen.gameserver.managers.DayNightSpawnManager;
import com.l2jfrozen.gameserver.managers.DimensionalRiftManager;
import com.l2jfrozen.gameserver.managers.DuelManager;
import com.l2jfrozen.gameserver.managers.FortManager;
import com.l2jfrozen.gameserver.managers.FortSiegeManager;
import com.l2jfrozen.gameserver.managers.FourSepulchersManager;
import com.l2jfrozen.gameserver.managers.GrandBossManager;
import com.l2jfrozen.gameserver.managers.ItemsOnGroundManager;
import com.l2jfrozen.gameserver.managers.MercTicketManager;
import com.l2jfrozen.gameserver.managers.PetitionManager;
import com.l2jfrozen.gameserver.managers.QuestManager;
import com.l2jfrozen.gameserver.managers.RaidBossPointsManager;
import com.l2jfrozen.gameserver.managers.RaidBossSpawnManager;
import com.l2jfrozen.gameserver.managers.ServerVariables;
import com.l2jfrozen.gameserver.managers.SiegeManager;
import com.l2jfrozen.gameserver.model.L2Manor;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.PartyMatchRoomList;
import com.l2jfrozen.gameserver.model.PartyMatchWaitingList;
import com.l2jfrozen.gameserver.model.entity.Announcements;
import com.l2jfrozen.gameserver.model.entity.Hero;
import com.l2jfrozen.gameserver.model.entity.MonsterRace;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.model.entity.sevensigns.SevenSigns;
import com.l2jfrozen.gameserver.model.entity.sevensigns.SevenSignsFestival;
import com.l2jfrozen.gameserver.model.entity.siege.clanhalls.BanditStrongholdSiege;
import com.l2jfrozen.gameserver.model.entity.siege.clanhalls.DevastatedCastle;
import com.l2jfrozen.gameserver.model.entity.siege.clanhalls.FortressOfResistance;
import com.l2jfrozen.gameserver.model.spawn.AutoSpawn;
import com.l2jfrozen.gameserver.network.L2GameClient;
import com.l2jfrozen.gameserver.network.L2GamePacketHandler;
import com.l2jfrozen.gameserver.scripting.CompiledScriptCache;
import com.l2jfrozen.gameserver.scripting.L2ScriptEngineManager;
import com.l2jfrozen.gameserver.skills.HitConditionBonus;
import com.l2jfrozen.gameserver.taskmanager.TaskManager;
import com.l2jfrozen.gameserver.thread.LoginServerThread;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;
import com.l2jfrozen.gameserver.thread.daemons.DeadlockDetector;
import com.l2jfrozen.gameserver.thread.daemons.ItemsAutoDestroy;
import com.l2jfrozen.gameserver.thread.daemons.PcPoint;
import com.l2jfrozen.gameserver.util.DynamicExtension;
import com.l2jfrozen.netcore.NetcoreConfig;
import com.l2jfrozen.netcore.SelectorConfig;
import com.l2jfrozen.netcore.SelectorThread;
import com.l2jfrozen.util.IPv4Filter;
import com.l2jfrozen.util.JVM;
import com.l2jfrozen.util.Memory;
import com.l2jfrozen.util.Util;
import com.l2jfrozen.util.database.L2DatabaseFactory;

import main.EngineModsManager;

/**
 * @author ReynalDev
 */
public class GameServer
{
	private static final Logger LOGGER = Logger.getLogger(GameServer.class);
	private static SelectorThread<L2GameClient> selectorThread;
	private static LoginServerThread loginThread;
	private static L2GamePacketHandler gamePacketHandler;
	
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	private static boolean headRevisionChecked = false;
	
	public static void main(String[] args)
	{
		PropertyConfigurator.configure(FService.LOG4J_CONF_FILE);
		
		if(!JVM.isUsingOpenJDK11())
		{
			System.exit(1);
		}
		
		ServerType.serverMode = ServerType.MODE_GAMESERVER;
		
		final String LOG_FOLDER_BASE = "log"; // Name of folder for LOGGER base file
		final File logFolderBase = new File(LOG_FOLDER_BASE);
		logFolderBase.mkdir();
		
		File log4jConfigFile = new File(FService.LOG4J_CONF_FILE);
		
		try(InputStream is = new FileInputStream(log4jConfigFile))
		{
			LogManager.getLogManager().readConfiguration(is);
		}
		catch (Exception e) 
		{
			LOGGER.error("Gameserver: Could not load log configuration file", e);
		}
		
		// Load GameServer Configs
		Config.load();
		
		Util.printSection("Database");
		L2DatabaseFactory.getInstance();
		
		Util.printSection("Threads");
		ThreadPoolManager.getInstance();
		ThreadPoolManager.getInstance().scheduleGeneral(() -> 
		{
			try
			{
				HttpClient httpClient = HttpClient.newBuilder().build();
				
				HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create("https://reynaldev.pythonanywhere.com/l2jfrozen15/revision/head/"))
			        .build();
				
				HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
				
				if(response.statusCode() == 200)
				{
					StringTokenizer st = new StringTokenizer(response.body(), ";");
					
					if(st.countTokens() == 2)
					{
						headRevisionChecked = true;
						Config.HEAD_REVISION = st.nextToken();
						Config.HEAD_PUB_DATE = st.nextToken();
					}
				}
			}
			catch (Exception e)
			{
			}
		}, 0);
		if (Config.DEADLOCKCHECK_INTIAL_TIME > 0)
		{
			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(DeadlockDetector.getInstance(), Config.DEADLOCKCHECK_INTIAL_TIME, Config.DEADLOCKCHECK_DELAY_TIME);
		}
		new File(Config.DATAPACK_ROOT, "data/clans").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/crests").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/pathnode").mkdirs();
		new File(Config.DATAPACK_ROOT, "data/geodata").mkdirs();
		
		ServerVariables.getInstance().loadVariables();
		
		Util.printSection("EngineMods Data");
		EngineModsManager.loadData();
		
		HtmCache.getInstance();
		CrestCache.getInstance();
		L2ScriptEngineManager.getInstance();
		
		nProtect.getInstance();
		if (nProtect.isEnabled())
		{
			LOGGER.info("nProtect System Enabled");
		}
		
		Util.printSection("World");
		L2World.getInstance();
		MapRegionTable.getInstance();
		Announcements.getInstance();
		AutoAnnouncementHandler.getInstance();
		IdFactory.getInstance();
		StaticObjectsTable.getInstance();
		TeleportLocationTable.getInstance();
		TerritoryTable.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		GameTimeController.getInstance();
		CharNameTable.getInstance();
		ExperienceData.getInstance();
		HitConditionBonus.getInstance();
		DuelManager.getInstance();
		
		if (Config.ENABLE_CLASS_DAMAGES)
		{
			ClassDamageManager.loadConfig();
		}
		
		Util.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeTable.getInstance();
		SkillSpellbookTable.getInstance();
		NobleSkillTable.getInstance();
		HeroSkillTable.getInstance();
		
		Util.printSection("Items");
		ItemTable.getInstance();
		
		ArmorSetsTable.getInstance();
		ExtractableItemsData.getInstance();
		SummonItemsData.getInstance();
		if (Config.ALLOWFISHING)
		{
			FishTable.getInstance().loadData();
		}
		
		Util.printSection("Npc");
		NpcWalkerRoutesTable.getInstance().load();
		NpcTable.getInstance();
		
		Util.printSection("Characters");
		if (Config.COMMUNITY_TYPE.equals("full"))
		{
			ForumsBBSManager.getInstance().initRoot();
		}
		
		ClanTable.getInstance();
		CharTemplateTable.getInstance();
		LevelUpData.getInstance();
		HennaTable.getInstance();
		
		NewbieGuideBuffTable.getInstance();
		
		Util.printSection("Geodata");
		GeoData.getInstance();
		if (Config.GEODATA == 2)
		{
			PathFinding.getInstance();
		}
		
		Util.printSection("Economy");
		TradeController.getInstance();
		L2Multisell.getInstance();
		
		Util.printSection("Clan Halls");
		ClanHallManager.getInstance();
		FortressOfResistance.getInstance();
		DevastatedCastle.getInstance();
		BanditStrongholdSiege.getInstance();
		AuctionManager.getInstance();
		
		Util.printSection("Zone");
		ZoneData.getInstance();
		
		Util.printSection("Spawnlist");
		
		if (!Config.ALT_DEV_NO_SPAWNS)
		{
			SpawnTable.getInstance();
		}
		else
		{
			LOGGER.info("Spawn: disable load.");
		}
		
		if (!Config.ALT_DEV_NO_RB)
		{
			RaidBossSpawnManager.getInstance();
			GrandBossManager.getInstance();
			RaidBossPointsManager.init();
		}
		else
		{
			LOGGER.info("RaidBoss: disable load.");
		}
		
		DayNightSpawnManager.getInstance().notifyChangeMode();
		
		Util.printSection("Dimensional Rift");
		DimensionalRiftManager.getInstance();
		
		Util.printSection("Misc");
		RecipeTable.getInstance();
		RecipeController.getInstance();
		GlobalDropData.getInstance();
		AugmentationData.getInstance();
		MonsterRace.getInstance();
		MercTicketManager.getInstance();
		PetitionManager.getInstance();
		CursedWeaponsManager.getInstance();
		TaskManager.getInstance();
		L2PetDataTable.getInstance().loadPetsData();
		if (Config.ACCEPT_GEOEDITOR_CONN)
		{
			GeoEditorListener.getInstance();
		}
		if (Config.SAVE_DROPPED_ITEM)
		{
			ItemsOnGroundManager.getInstance();
		}
		if (Config.AUTODESTROY_ITEM_AFTER > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
		{
			ItemsAutoDestroy.getInstance();
		}
		
		Util.printSection("Manor");
		L2Manor.getInstance();
		CastleManorManager.getInstance();
		
		Util.printSection("Castles");
		CastleManager.getInstance();
		SiegeManager.getInstance();
		FortManager.getInstance();
		FortSiegeManager.getInstance();
		
		Util.printSection("Boat");
		BoatManager.getInstance();
		
		Util.printSection("Doors");
		DoorTable.getInstance().parseData();
		
		Util.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance();
		
		Util.printSection("Seven Signs");
		SevenSigns.getInstance();
		SevenSignsFestival.getInstance();
		AutoSpawn.getInstance();
		AutoChatHandler.getInstance();
		
		Util.printSection("Olympiads");
		Olympiad.getInstance();
		Util.printSection("Heroes");
		Hero.getInstance();
		
		Util.printSection("Access Levels");
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		GmListTable.getInstance();
		
		Util.printSection("Handlers");
		ItemHandler.getInstance();
		SkillHandler.getInstance();
		AdminCommandHandler.getInstance();
		UserCommandHandler.getInstance();
		VoicedCommandHandler.getInstance();
		
		LOGGER.info("AutoChatHandler : Loaded " + AutoChatHandler.getInstance().size() + " handlers in total.");
		LOGGER.info("AutoSpawnHandler : Loaded " + AutoSpawn.getInstance().size() + " handlers in total.");
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		try
		{
			DoorTable doorTable = DoorTable.getInstance();
			
			// Opened by players like L2OFF
			// doorTable.getDoor(19160010).openMe();
			// doorTable.getDoor(19160011).openMe();
			
			doorTable.getDoor(19160012).openMe();
			doorTable.getDoor(19160013).openMe();
			doorTable.getDoor(19160014).openMe();
			doorTable.getDoor(19160015).openMe();
			doorTable.getDoor(19160016).openMe();
			doorTable.getDoor(19160017).openMe();
			doorTable.getDoor(24190001).openMe();
			doorTable.getDoor(24190002).openMe();
			doorTable.getDoor(24190003).openMe();
			doorTable.getDoor(24190004).openMe();
			doorTable.getDoor(23180001).openMe();
			doorTable.getDoor(23180002).openMe();
			doorTable.getDoor(23180003).openMe();
			doorTable.getDoor(23180004).openMe();
			doorTable.getDoor(23180005).openMe();
			doorTable.getDoor(23180006).openMe();
			doorTable.checkAutoOpen();
			doorTable = null;
		}
		catch (final NullPointerException e)
		{
			LOGGER.info("There is errors in your Door.csv file. Update door.csv");
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				e.printStackTrace();
			}
		}
		
		Util.printSection("AI");
		if (!Config.ALT_DEV_NO_AI)
		{
			AILoader.init();
		}
		else
		{
			LOGGER.info("AI: disable load.");
		}
		
		Util.printSection("Scripts");
		if (!Config.ALT_DEV_NO_SCRIPT)
		{
			final File scripts = new File(Config.DATAPACK_ROOT, "data/scripts.cfg");
			L2ScriptEngineManager.getInstance().executeScriptsList(scripts);
			
			final CompiledScriptCache compiledScriptCache = L2ScriptEngineManager.getInstance().getCompiledScriptCache();
			if (compiledScriptCache == null)
			{
				LOGGER.info("Compiled Scripts Cache is disabled.");
			}
			else
			{
				compiledScriptCache.purge();
				if (compiledScriptCache.isModified())
				{
					compiledScriptCache.save();
					LOGGER.info("Compiled Scripts Cache was saved.");
				}
				else
				{
					LOGGER.info("Compiled Scripts Cache is up-to-date.");
				}
			}
		}
		else
		{
			LOGGER.info("Script: disable load.");
		}
		
		/* QUESTS */
		Util.printSection("Quests");
		if (!Config.ALT_DEV_NO_QUESTS)
		{
			
			if (QuestManager.getInstance().getQuests().size() == 0)
			{
				QuestManager.getInstance().reloadAllQuests();
			}
			else
			{
				QuestManager.getInstance().report();
			}
			
		}
		
		Util.printSection("Game Server");
		LOGGER.info("Maximum players allowed: " + Config.MAXIMUM_ONLINE_USERS);
		LOGGER.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
		try
		{
			DynamicExtension.getInstance();
		}
		catch (final Exception ex)
		{
			if (Config.ENABLE_ALL_EXCEPTIONS)
			{
				ex.printStackTrace();
			}
			
			LOGGER.info("DynamicExtension could not be loaded and initialized" + ex);
		}
		
		if (Config.L2JMOD_ALLOW_WEDDING)
		{
			CoupleManager.getInstance();
		}
		
		if (Config.ALLOW_AWAY_STATUS)
		{
			AwayManager.getInstance();
		}
		
		if (Config.PCB_ENABLE)
		{
			ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(PcPoint.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);
		}
		
		// Start Engine scripts
		Util.printSection("EngineMods Scripts");
		EngineModsManager.loadScripts();
				
		Util.printSection("Project info");
		LOGGER.info("Your revision: " + Config.REVISION);
		LOGGER.info("Your build date: " + Config.BUILD_DATE);
		
		if(headRevisionChecked)
		{
			Util.printSection("Last project revision available");
			LOGGER.info("Last revision: " + Config.HEAD_REVISION);
			LOGGER.info("Last revision publication date: " + Config.HEAD_PUB_DATE);
		}
		
		Util.printSection("OS info");
		LOGGER.info("Operating system: " + Util.getOSName() + " " + Util.getOSVersion() + " " + Util.getOSArch());
		LOGGER.info("Available CPUs: " + Util.getAvailableProcessors());
		LOGGER.info("Free memory: " + Memory.getFreeMemory() + " MB of " + Memory.getTotalMemory() + " MB");
		LOGGER.info("Used memory: " + Memory.getUsedMemory() + " MB of " + Memory.getTotalMemory() + " MB");
		
		Util.printSection("Java Info");
		LOGGER.info("JRE name: " + System.getProperty("java.vendor"));
		LOGGER.info("JRE version: " + System.getProperty("java.version"));
		LOGGER.info("--- Detecting Java Virtual Machine (JVM)");
		LOGGER.info("JVM implementation name: " + System.getProperty("java.vm.name"));
		LOGGER.info("JVM installation directory: " + System.getProperty("java.home"));
		
		ServerStatusTask.getInstance();
		
		Util.printSection("Login");
		loginThread = LoginServerThread.getInstance();
		loginThread.start();
		javolution.testing.Logger.logMe();
		final SelectorConfig sc = new SelectorConfig();
		sc.setMaxReadPerPass(NetcoreConfig.getInstance().MMO_MAX_READ_PER_PASS);
		sc.setMaxSendPerPass(NetcoreConfig.getInstance().MMO_MAX_SEND_PER_PASS);
		sc.setSleepTime(NetcoreConfig.getInstance().MMO_SELECTOR_SLEEP_TIME);
		sc.setHelperBufferCount(NetcoreConfig.getInstance().MMO_HELPER_BUFFER_COUNT);
		
		gamePacketHandler = new L2GamePacketHandler();
		
		try
		{
			selectorThread = new SelectorThread<>(sc, gamePacketHandler, gamePacketHandler, gamePacketHandler, new IPv4Filter());
		}
		catch (Exception e)
		{
			LOGGER.error("Gameserver: Selector thread, something went wrong", e);
		}
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (final UnknownHostException e1)
			{
				if (Config.ENABLE_ALL_EXCEPTIONS)
				{
					e1.printStackTrace();
				}
				
				LOGGER.warn("The GameServer bind address is invalid, using all avaliable IPs. Reason: ", e1);
			}
		}
		
		try
		{
			selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to open server socket", e);
			System.exit(1);
		}
		selectorThread.start();
	}
	
	public static SelectorThread<L2GameClient> getSelectorThread()
	{
		return selectorThread;
	}
}