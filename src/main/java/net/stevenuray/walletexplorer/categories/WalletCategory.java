package net.stevenuray.walletexplorer.categories;

import java.util.Iterator;
import java.util.Set;

/**Represents one or more wallets grouped together by some kind of association. 
 * 
 * @author Steven Uray 2015-10-23 
 */
public class WalletCategory {
	private final String name;
	public String getName(){
		return name;
	}
	private final Set<String> walletNames;	
	public Set<String> getWalletNames() {
		return walletNames;
	}

	public WalletCategory(String name, Set<String> walletNames){
		this.name = name;
		this.walletNames = walletNames;
	}
	
	public Iterator<String> getWalletNameIterator(){		
		Iterator<String> walletNameIterator = walletNames.iterator();
		return walletNameIterator;
	}
}
