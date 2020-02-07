package com.l2jfrozen.gameserver.network.clientpackets;

import org.apache.log4j.Logger;

import com.l2jfrozen.Config;
import com.l2jfrozen.gameserver.ai.CtrlIntention;
import com.l2jfrozen.gameserver.communitybbs.CommunityBoard;
import com.l2jfrozen.gameserver.datatables.xml.AdminCommandAccessRights;
import com.l2jfrozen.gameserver.handler.AdminCommandHandler;
import com.l2jfrozen.gameserver.handler.IAdminCommandHandler;
import com.l2jfrozen.gameserver.handler.custom.CustomBypassHandler;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.actor.position.L2CharPosition;
import com.l2jfrozen.gameserver.model.entity.olympiad.Olympiad;
import com.l2jfrozen.gameserver.network.serverpackets.ActionFailed;
import com.l2jfrozen.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jfrozen.util.L2Log;

import main.EngineModsManager;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger LOGGER = Logger.getLogger(RequestBypassToServer.class);
	
	private String bypassCommand;
	
	@Override
	protected void readImpl()
	{
		bypassCommand = readS();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (!getClient().getFloodProtectors().getServerBypass().tryPerformAction(bypassCommand))
		{
			return;
		}
		
		try
		{
			if (bypassCommand.startsWith("admin_"))
			{
				if (!activeChar.isGM())
				{
					LOGGER.warn(activeChar.getName() + " tried to use admin command without being GM!");
					return;
				}
				
				if (EngineModsManager.onVoiced(activeChar, bypassCommand))
				{
					return;
				}
				
				// DaDummy: this way we LOGGER every admincommand with all related info
				String command;
				
				if (bypassCommand.contains(" "))
				{
					command = bypassCommand.substring(0, bypassCommand.indexOf(" "));
				}
				else
				{
					command = bypassCommand;
				}
				
				IAdminCommandHandler ach = AdminCommandHandler.getInstance().getAdminCommandHandler(command);
				
				if (ach == null)
				{
					activeChar.sendMessage("The command " + command + " does not exists!");
					LOGGER.warn("No handler registered for admin command '" + command + "'");
					return;
				}
				
				if (!AdminCommandAccessRights.getInstance().hasAccess(command, activeChar.getAccessLevel()))
				{
					activeChar.sendMessage("You don't have the access right to use this command!");
					LOGGER.warn("Character " + activeChar.getName() + " tried to use admin command " + command + ", but doesn't have access to it!");
					return;
				}
				
				if (Config.GMAUDIT)
				{
					String target = activeChar.getTarget() != null ? activeChar.getTarget().getName() : "no-target";
					String message = "GM: " + activeChar + ", Target: [" + target + "], Command: [" + command + "], Params: [" + bypassCommand.replace(command, "") + "]";
					String fileName = activeChar.getName() + "_" + activeChar.getObjectId(); // ReynalDev_268477761
					L2Log.add(message, "gm_commands", fileName);
				}
				
				ach.useAdminCommand(bypassCommand, activeChar);
			}
			else if (bypassCommand.equals("come_here") && activeChar.isGM())
			{
				comeHere(activeChar);
			}
			else if (bypassCommand.startsWith("player_help "))
			{
				playerHelp(activeChar, bypassCommand.substring(12));
			}
			else if (bypassCommand.startsWith("npc_"))
			{
				if (!activeChar.validateBypass(bypassCommand))
				{
					return;
				}
				
				int endOfId = bypassCommand.indexOf('_', 5);
				String id;
				
				if (endOfId > 0)
				{
					id = bypassCommand.substring(4, endOfId);
				}
				else
				{
					id = bypassCommand.substring(4);
				}
				
				try
				{
					L2Object npc = L2World.getInstance().findObject(Integer.parseInt(id));
					
					if (npc instanceof L2NpcInstance && endOfId > 0 && activeChar.isInsideRadius(npc, L2NpcInstance.INTERACTION_DISTANCE, false, false))
					{
						((L2NpcInstance) npc).onBypassFeedback(activeChar, bypassCommand.substring(endOfId + 1));
					}
					
					activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				}
				catch (NumberFormatException nfe)
				{
					LOGGER.error("RequestByPassToServer.runImpl : invalid number format", nfe);
				}
			}
			// Navigate throught Manor windows
			else if (bypassCommand.startsWith("manor_menu_select?"))
			{
				L2Object object = activeChar.getTarget();
				if (object instanceof L2NpcInstance)
				{
					((L2NpcInstance) object).onBypassFeedback(activeChar, bypassCommand);
				}
			}
			else if (bypassCommand.startsWith("bbs_"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), bypassCommand);
			}
			else if (bypassCommand.startsWith("_bbs"))
			{
				CommunityBoard.getInstance().handleCommands(getClient(), bypassCommand);
			}
			else if (bypassCommand.startsWith("Quest "))
			{
				if (!activeChar.validateBypass(bypassCommand))
				{
					return;
				}
				
				L2PcInstance player = getClient().getActiveChar();
				if (player == null)
				{
					return;
				}
				
				String p = bypassCommand.substring(6).trim();
				int idx = p.indexOf(' ');
				
				if (idx < 0)
				{
					player.processQuestEvent(p, "");
				}
				else
				{
					player.processQuestEvent(p.substring(0, idx), p.substring(idx).trim());
				}
			}
			else if (bypassCommand.startsWith("custom_"))
			{
				CustomBypassHandler.getInstance().handleBypass(activeChar, bypassCommand);
			}
			else if (bypassCommand.startsWith("OlympiadArenaChange"))
			{
				Olympiad.bypassChangeArena(bypassCommand, activeChar);
			}
			else if (bypassCommand.startsWith("Engine"))
			{
				EngineModsManager.onEvent(activeChar, bypassCommand.replace("Engine ", ""));
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Bad RequestBypassToServer: ", e);
		}
	}
	
	private void comeHere(L2PcInstance activeChar)
	{
		L2Object obj = activeChar.getTarget();
		if (obj == null)
		{
			return;
		}
		
		if (obj instanceof L2NpcInstance)
		{
			final L2NpcInstance temp = (L2NpcInstance) obj;
			temp.setTarget(activeChar);
			temp.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(activeChar.getX(), activeChar.getY(), activeChar.getZ(), 0));
		}
		
	}
	
	private void playerHelp(L2PcInstance activeChar, String path)
	{
		if (path.contains(".."))
		{
			return;
		}
		
		String filename = "data/html/help/" + path;
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile(filename);
		activeChar.sendPacket(html);
	}
	
	@Override
	public String getType()
	{
		return "[C] 21 RequestBypassToServer";
	}
}
