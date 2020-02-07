package com.l2jfrozen.gameserver.network.serverpackets;

import org.apache.log4j.Logger;

import com.l2jfrozen.gameserver.network.L2GameClient;
import com.l2jfrozen.netcore.SendablePacket;

/**
 * @author ProGramMoS
 * @author ReynalDev
 */
public abstract class L2GameServerPacket extends SendablePacket<L2GameClient>
{
	private static final Logger LOGGER = Logger.getLogger(L2GameServerPacket.class);
	
	@Override
	protected void write()
	{
		try
		{
			writeImpl();
		}
		catch (Exception e)
		{
			LOGGER.error("Client: " + getClient().toString() + " - Failed writing: " + getType(), e);
		}
	}
	
	public void runImpl()
	{
		
	}
	
	protected abstract void writeImpl();
	
	/**
	 * @return A String with this packet name for debuging purposes
	 */
	public abstract String getType();
}
