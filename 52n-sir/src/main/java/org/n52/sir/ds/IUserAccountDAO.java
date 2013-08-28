package org.n52.sir.ds;

public interface IUserAccountDAO {
	
	
	//return null if the authentication fail , authToken otherwise
	public String authenticateUser(String name,String password);

}
