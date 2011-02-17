package mario;

import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;

public class myPlayerListener extends PlayerListener
{
	MySignShop plugin;
	public final int signID = 323;
	
	public myPlayerListener(MySignShop _plugin)
	{
		plugin = _plugin;
	}
	
	@Override
	public void onPlayerItem(PlayerItemEvent event)
	{	
		
	}
}
