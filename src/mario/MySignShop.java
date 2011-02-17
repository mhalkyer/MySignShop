package mario;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.inventory.ItemStack;

import com.nijikokun.bukkit.iConomy.iConomy;

/**
 * Plugin: My Sign Shop
 * @author mhalkyer
 */
public class MySignShop extends JavaPlugin
{
	public final int max_signshops = 300;
	public List<String> players_creatingashop = new ArrayList<String>();
	public List<String> players_linkedchest = new ArrayList<String>();
	public SignShop[] signshops = new SignShop[max_signshops];	
	public Logger logger = Logger.getLogger("Minecraft");;
	public String name = "";
	public String version = "";  
	public boolean debugging = false;	
	public Timer timer = new Timer();
	
	public MySignShop(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader)
	{
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}

	public void onEnable()
	{		
		// Initializa plugin manager
		PluginManager pm = getServer().getPluginManager();

		// Initialize listeners
		myBlockListener bListener = new myBlockListener(this);
		//myPlayerListener pListener = new myPlayerListener(this);
		
		// Initialize signshops array
		for(int i=0; i<signshops.length; i++)
			signshops[i] = new SignShop();
		
		//Schedule the timer
		timer.schedule(new myTimerTask(this), 0, 500);

		// Register Listeners
	    pm.registerEvent(Event.Type.BLOCK_RIGHTCLICKED, bListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.BLOCK_PLACED, bListener, Event.Priority.Normal, this);
	    pm.registerEvent(Event.Type.SIGN_CHANGE, bListener, Event.Priority.Normal, this);
	    //pm.registerEvent(Event.Type.PLAYER_ITEM, pListener, Event.Priority.Normal, this);
		
	    // Display startup message
		PluginDescriptionFile pdfFile = this.getDescription();
		name = "[" + pdfFile.getName() + "]";
		version = "[" + pdfFile.getVersion() + "]";
		logger.log(Level.INFO, name + " version " + version + " is enabled.");
		
		// Check for iConomy - disable plugin if not found
		if(isIconomyInstalled()==false)
		{
			logger.log(Level.INFO, name + " iConomy not detected, shutting down...");
			pm.disablePlugin(this);
		}
		
		logger.log(Level.INFO, name + " Done loading.");
	}

	public void onDisable()
	{
		//Clear all the arrays
		players_creatingashop.clear();
		players_linkedchest.clear();
		for(int i=0; i<signshops.length; i++)
			signshops[i].ClearValues();
		
		//Stop the timer
		timer.cancel();
		
		//Log a message
		logger.log(Level.INFO, name + " version " + version + " is disabled.");
	}
 
	@Override	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{		
		String commandName = command.getName().toLowerCase();
		
		if(debugging)
			logger.log(Level.INFO, name + " onCommand called.");

		if (commandName.equals("mss") && sender instanceof Player)
		{
			if(debugging)
				logger.log(Level.INFO, name + " /mss command detected from player.");
			
			Player player = (Player)sender;
			players_creatingashop.add(player.getName());
			sender.sendMessage(name + " Place or right-click a chest.");
			return true;
		}
		return false;
	}

	private boolean isIconomyInstalled()
	{
		Plugin test = this.getServer().getPluginManager().getPlugin("iConomy");

        if (test != null)
            return true;
        else
            return false;
	}
	
	public boolean isPlayerCreatingAShop(Player player)
	{
		if(players_creatingashop.contains(player.getName()))
			return true;
		else
			return false;
	}
	
	public boolean hasPlayerLinkedAChest(Player player)
	{
		if(players_linkedchest.contains(player.getName()))
			return true;
		else
			return false;
	}
	
	public void sell()
	{
		//TODO: Add sell logic
		iConomy.db.set_balance("mario", 1000);
	}
	
	public void buy()
	{
		//TODO: Add buy logic
		iConomy.db.set_balance("mario", 1000);
	}
	
	public void load_signshops(String filename)
	{
		//TODO: Add load signshops logic
	}

	public int getFirstEmptySignShop()
	{
		for(int i=0; i<signshops.length; i++)
			if (signshops[i].owner.equalsIgnoreCase(""))
				return i;
		
		//No empty signshop available
		return -1;
	}
	
	public int getPlayerUnfinishedSignShop(String name)
	{		
		for(int i=0; i<signshops.length; i++)
			if(signshops[i].owner.equalsIgnoreCase(name) && signshops[i].sign_x==SignShop.empty)
				return i;
		
		//Player has no unfinished signshop
		return -1;
	}
	
	public boolean blockisaSignShop(int x, int y, int z)
	{	
		for(SignShop shop : signshops)
			if(shop.sign_x==x && shop.sign_y==y && shop.sign_z==z)
				return true;
		
		return false;
	}

	public Player getClosestPlayer(int x, int y, int z)
	{
		Player[] players = getServer().getOnlinePlayers();
		int detection_radius = 5;

		for(Player p : players)
		{
			double px = p.getLocation().getBlockX();
			double py = p.getLocation().getBlockY();
			double pz = p.getLocation().getBlockZ();
		
			if(Math.abs(px-x) < detection_radius && 
			   Math.abs(py-y) < detection_radius && 
			   Math.abs(pz-z) < detection_radius)
				return p;
		}
		
		return null;
	}

	public String getSignShopOwner(Sign sign)
	{
		for(SignShop shop : signshops)
			if(shop.sign_x==sign.getX() && shop.sign_y==sign.getY() && shop.sign_z==sign.getZ())
				return shop.owner;
		
		return "";
	}

	public void setShopChest(Player player, Block block)
	{
		//Find slot to store in array
		int i = getFirstEmptySignShop();
		
		if(i!=-1)
		{
			signshops[i].owner = player.getName();
			signshops[i].chest_x = block.getX();
			signshops[i].chest_y = block.getY();
			signshops[i].chest_z = block.getZ();
			
			if(debugging)
				player.sendMessage(name + " chest coords stored in array index: " + i);
			
			logger.log(Level.INFO, name + " " + player.getName() + " placed a shop chest at: " + 
										  signshops[i].chest_x + ", " + signshops[i].chest_y + ", " + signshops[i].chest_z);
			
			player.sendMessage(name + " Shop chest set! Place or right-click a sign.");								
		
			//Move player to the other array
			players_creatingashop.remove(player.getName());
			players_linkedchest.add(player.getName());
		}
		else
		{
			player.sendMessage(name + " Max limit of [" + max_signshops + "] SignShops met!");
		}
	}

	public void setShopSign(Player player, Block block)
	{
		//Find the player's unfinished signshop
		int i = getPlayerUnfinishedSignShop(player.getName());
		
		//Link sign if the player is creating a signshop
		if(i!=-1)
		{
			signshops[i].sign_x = block.getX();
			signshops[i].sign_y = block.getY();
			signshops[i].sign_z = block.getZ();
							
			if(debugging)
				player.sendMessage(name + " sign coords stored in array index: " + i);
			
			logger.log(Level.INFO, name + " " + player.getName() + " placed a shop sign at: " + 
					  					  signshops[i].sign_x + ", " + signshops[i].sign_y + ", " + signshops[i].sign_z);
			
			player.sendMessage(name + " Shop sign placed and linked to chest.");
			
			//Set the sign text			
			autoSetSignText(signshops[i]);
		}
		else
		{
			player.sendMessage(name + " ERROR linking to shop chest. Please try again.");				
		}
		
		//Remove the player from the array
		players_linkedchest.remove(player.getName());
	}

	public void autoSetSignText(SignShop shop)
	{		
		World world = getServer().getWorlds().get(0);
		Chest chest = (Chest) world.getBlockAt(shop.chest_x, shop.chest_y, shop.chest_z).getState();
		Sign sign = (Sign) world.getBlockAt(shop.sign_x, shop.sign_y, shop.sign_z).getState();		
		List<ItemStack> contents = new ArrayList<ItemStack>(); 
		String itemName = "";
		int quantity = 0;
		String price = "";
				
		//Compile a item list ignoring the 'AIR' entries in the chest
		for(ItemStack item : chest.getInventory().getContents())
			if(item.getType() != Material.AIR)
				contents.add(item);
				
		//Get 1st chest item (check if empty or air)
		if(contents.isEmpty()==false)
			itemName = contents.get(0).getType().toString();
		else
			itemName = "SOLD OUT";
		
		//Set Quantity
		if(itemName!="SOLD OUT")
			for(ItemStack item : contents)
				if (item.getType().toString() == itemName)
					quantity += item.getAmount();
		
		//Get Price
		if(itemName!="SOLD OUT")
			price = getPrice(itemName);
		
		//Set the sign text
		sign.setLine(0, "[BUY]");
		sign.setLine(1, "Item: " + itemName);
		sign.setLine(2, "Quantity: " + quantity);
		sign.setLine(3, "Price: $" + price);
		sign.update();		
	}
	
	public String getPrice(String item)
	{
		return "1";
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
