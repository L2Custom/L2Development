package com.l2jfrozen.gameserver.network.clientpackets;

import com.l2jfrozen.gameserver.model.L2World;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.FriendRecvMsg;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;

/**
 * Recieve Private (Friend) Message - 0xCC Format: c SS S: Message S: Receiving Player
 * @author L2JFrozen
 */
public final class RequestSendFriendMsg extends L2GameClientPacket
{
	private String message;
	private String reciever;
	
	@Override
	protected void readImpl()
	{
		message = readS();
		reciever = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final L2PcInstance targetPlayer = L2World.getInstance().getPlayerByName(reciever);
		if (targetPlayer == null || !targetPlayer.getFriendList().contains(activeChar.getName()))
		{
			activeChar.sendPacket(new SystemMessage(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE));
			return;
		}
		
		final FriendRecvMsg frm = new FriendRecvMsg(activeChar.getName(), reciever, message);
		targetPlayer.sendPacket(frm);
	}
	
	@Override
	public String getType()
	{
		return "[C] CC RequestSendMsg";
	}
}
