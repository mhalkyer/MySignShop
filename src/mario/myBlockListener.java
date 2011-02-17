package mario;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.SignChangeEvent;

public class myBlockListener extends BlockListener
{
	public MySignShop plugin;
	public SignShop test;
	
	public myBlockListener(MySignShop _plugin)
	{
		plugin = _plugin;
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block block = event.getBlock();
		Material material = event.getBlock().getType();
		Player player = event.getPlayer();		
		
		if (plugin.isPlayerCreatingAShop(player) && material.equals(Material.CHEST))
		{				
			plugin.setShopChest(player, block);
		}
	}
	
	@Override
	public void onSignChange(SignChangeEvent event)
	{
		Block block = event.getBlock();

		//Get the closest player to the sign
		Player player = plugin.getClosestPlayer(block.getX(), block.getY(), block.getZ());	
		
		//Check if player uses a sign
		if(player!=null && plugin.hasPlayerLinkedAChest(player))
		{			
			plugin.setShopSign(player, block);
		}
	}
	
	@Override
	public void onBlockRightClick(BlockRightClickEvent event)
	{
		Block block = event.getBlock();
		Material material = event.getBlock().getType();
		Player player = event.getPlayer();
		String owner = "";
		
		//Player right clicked a sign
		if (material == Material.SIGN_POST || material == Material.WALL_SIGN)
		{
			if(player!=null && plugin.hasPlayerLinkedAChest(player))
			{			
				plugin.setShopSign(player, block);
			}
			else
			{				
				Sign s = (Sign) event.getBlock().getState();			
				owner = plugin.getSignShopOwner(s);
				
				if(owner!="")
				{
					player.sendMessage(plugin.name + " You right clicked " + owner + "'s signshop!");
				}
				
				if (s.getLine(0).equalsIgnoreCase("[mario]"))
				{
					player.sendMessage(plugin.name + " You right clicked a Mario sign!");
					s.setLine(0, "i wub you!");
					s.update();
				}
			}
		}
		
		//Player right clicked a chest
		if (plugin.isPlayerCreatingAShop(player) && material.equals(Material.CHEST))
		{				
			plugin.setShopChest(player, block);
		}
	}
}
