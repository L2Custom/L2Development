package com.l2jfrozen.gameserver.handler.itemhandlers;

import org.apache.log4j.Logger;

import com.l2jfrozen.gameserver.datatables.sql.NpcTable;
import com.l2jfrozen.gameserver.handler.IItemHandler;
import com.l2jfrozen.gameserver.idfactory.IdFactory;
import com.l2jfrozen.gameserver.model.actor.instance.L2ItemInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PlayableInstance;
import com.l2jfrozen.gameserver.model.spawn.L2Spawn;
import com.l2jfrozen.gameserver.templates.L2NpcTemplate;
import com.l2jfrozen.gameserver.thread.ThreadPoolManager;

/**
 * @author ReynalDev
 */
public class ChristmasTree implements IItemHandler
{
	private static final Logger LOGGER = Logger.getLogger(ChristmasTree.class);
	
	private static final int[] ITEM_IDS =
	{
		5560, // Normal x-mas tree
		5561 // Special x-mas tree
		
	};
	
	private static final int[] NPC_IDS =
	{
		13006, // Normal x-mas tree
		13007 // Special x-mas tree
	};
	
	@Override
	public void useItem(L2PlayableInstance playable, L2ItemInstance item)
	{
		if(!playable.isPlayer())
		{
			return;
		}
		
		L2PcInstance activeChar = (L2PcInstance) playable;
		
		L2NpcTemplate template1 = null;
		
		final int itemId = item.getItemId();
		
		for (int i = 0; i < ITEM_IDS.length; i++)
		{
			if (ITEM_IDS[i] == itemId)
			{
				template1 = NpcTable.getInstance().getTemplate(NPC_IDS[i]);
				break;
			}
		}
		
		if (template1 == null)
		{
			return;
		}
		
		try
		{
			L2Spawn spawn = new L2Spawn(template1);
			spawn.setId(IdFactory.getInstance().getNextId());
			spawn.setLocx(activeChar.getX());
			spawn.setLocy(activeChar.getY());
			spawn.setLocz(activeChar.getZ());
			L2NpcInstance npc = spawn.spawnOne();
			
			activeChar.destroyItemByItemId("Spawning X-MASS Tree", itemId, 1, activeChar, true);
			
			ThreadPoolManager.getInstance().scheduleGeneral(() -> 
			{
				npc.onDecay();
			}, 3600000);
		}
		catch (Exception e)
		{
			LOGGER.error("Error while spawning christmas tree", e);
		}
	}
	
	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
