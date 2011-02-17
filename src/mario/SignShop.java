package mario;

public class SignShop
{
	public static final int empty=-999;
	
	public String owner="";
	
	public int chest_x = empty;
	public int chest_y = empty;
	public int chest_z = empty;
	
	public int sign_x = empty;
	public int sign_y = empty;
	public int sign_z = empty;
	
	public void ClearValues()
	{	
		owner = "";
		this.chest_x = empty;
		this.chest_y = empty;
		this.chest_z = empty;
		this.sign_x = empty;
		this.sign_y = empty;
		this.sign_z = empty;		
	}
}
