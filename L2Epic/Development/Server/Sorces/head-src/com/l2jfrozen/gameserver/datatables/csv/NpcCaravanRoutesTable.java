package com.l2jfrozen.gameserver.datatables.csv;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.model.L2NpcCaravanNode;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Main Table to Load Npc Walkers Routes and Chat SQL Table.<br>
 * @author Rayan RPG for L2Emu Project
 * @author ProGramMoS
 * @since  927
 */
public class NpcCaravanRoutesTable
{
	protected static final Logger LOGGER = Logger.getLogger(NpcCaravanRoutesTable.class);
	
	private static NpcCaravanRoutesTable instance;
	
	private List<L2NpcCaravanNode> routes;
	
	public static NpcCaravanRoutesTable getInstance()
	{
		if (instance == null)
		{
			instance = new NpcCaravanRoutesTable();
		}
		
		return instance;
	}
	
	public void load()
	{
		routes = new ArrayList<>();
		File fileData = new File(Config.DATAPACK_ROOT + "/data/csv/caravan_routes.csv");
		
		try (FileReader reader = new FileReader(fileData);
			BufferedReader buff = new BufferedReader(reader);
			LineNumberReader lnr = new LineNumberReader(buff))
		{
			L2NpcCaravanNode route;
			String line = null;
			
			// format:
			// route_id;npc_id;move_point;chatText;move_x;move_y;move_z;delay;running
			while ((line = lnr.readLine()) != null)
			{
				// ignore comments
				if (line.trim().length() == 0 || line.startsWith("#"))
				{
					continue;
				}
				
				route = new L2NpcCaravanNode();
				StringTokenizer st = new StringTokenizer(line, ";");
				
				int route_id = Integer.parseInt(st.nextToken());
				int npc_id = Integer.parseInt(st.nextToken());
				String move_point = st.nextToken();
				String chatText = st.nextToken();
				int move_x = Integer.parseInt(st.nextToken());
				int move_y = Integer.parseInt(st.nextToken());
				int move_z = Integer.parseInt(st.nextToken());
				int delay = Integer.parseInt(st.nextToken());
				boolean running = Boolean.parseBoolean(st.nextToken());
				
				route.setRouteId(route_id);
				route.setNpcId(npc_id);
				route.setMovePoint(move_point);
				route.setChatText(chatText);
				route.setMoveX(move_x);
				route.setMoveY(move_y);
				route.setMoveZ(move_z);
				route.setDelay(delay);
				route.setRunning(running);
				
				routes.add(route);
			}
			
			LOGGER.info("CaravanRoutesTable: Loaded " + routes.size() + " Npc Caravan Routes.");
			
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error("NpcCaravanRoutesTable.load : caravan_routes.csv file is missing in gameserver/data/csv folder.");
		}
		catch (IOException e)
		{
			LOGGER.error("NpcCaravanRoutesTable.load : Error while creating table. ", e);
		}
		
	}
	
	public List<L2NpcCaravanNode> getRouteForNpc(int id)
	{
		return routes.stream().filter(node -> node.getNpcId() == id).collect(Collectors.toList());
	}
}
