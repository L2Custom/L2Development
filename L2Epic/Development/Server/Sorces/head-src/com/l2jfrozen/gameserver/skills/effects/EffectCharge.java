package com.l2jfrozen.gameserver.skills.effects;

import com.l2jfrozen.gameserver.model.L2Effect;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.network.SystemMessageId;
import com.l2jfrozen.gameserver.network.serverpackets.EtcStatusUpdate;
import com.l2jfrozen.gameserver.network.serverpackets.SystemMessage;
import com.l2jfrozen.gameserver.skills.Env;

public class EffectCharge extends L2Effect
{
	public int numCharges;
	
	public EffectCharge(final Env env, final EffectTemplate template)
	{
		super(env, template);
		numCharges = 1;
		if (env.target instanceof L2PcInstance)
		{
			env.target.sendPacket(new EtcStatusUpdate((L2PcInstance) env.target));
			final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_FORCE_HAS_INCREASED_TO_S1_LEVEL);
			sm.addNumber(numCharges);
			getEffected().sendPacket(sm);
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CHARGE;
	}
	
	@Override
	public boolean onActionTime()
	{
		// ignore
		return true;
	}
	
	@Override
	public int getLevel()
	{
		return numCharges;
	}
	
	public void addNumCharges(final int i)
	{
		numCharges = numCharges + i;
	}
}
