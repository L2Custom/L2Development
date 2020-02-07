package com.l2jfrozen.gameserver.network.clientpackets;

import com.l2jfrozen.gameserver.model.L2Clan;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.serverpackets.PledgePowerGradeList;

/**
 * Format: (ch)
 * @author -Wooden-
 * @author ReynalDev
 */
public final class RequestPledgePowerGradeList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		// trigger
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		final L2Clan clan = player.getClan();
		
		if (clan != null)
		{
			player.sendPacket(new PledgePowerGradeList(clan.getAllRankPrivs().keySet(), clan.getMembers()));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:1A RequestPledgePowerGradeList";
	}
	
}
