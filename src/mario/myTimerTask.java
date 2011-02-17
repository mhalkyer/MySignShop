package mario;

import java.util.TimerTask;

public class myTimerTask extends TimerTask 
{
	MySignShop plugin;
	
	public myTimerTask(MySignShop _plugin)
	{
		plugin = _plugin;
	}
	
	@Override
	public void run() 
	{
		UpdateAllSignShops();
	}
	
	public void UpdateAllSignShops()
	{
		for(SignShop shop : plugin.signshops)
			if(shop.owner!="" && shop.chest_x!=SignShop.empty && shop.sign_x!=SignShop.empty)
				plugin.autoSetSignText(shop);
	}
}
