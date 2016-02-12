package net.stevenuray.walletexplorer.walletnames;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**Represents a set of wallet names that represent bitcoin companies as WalletExplorer.com defines them. 
 * All names in WalletNames are assumed to be valid names that will return transactions when given to 
 * WalletExplorer's API.
 * @author Steven Uray
 */
public class WalletNames implements Iterable<String>{
	private final Set<String> walletNamesSet = new HashSet<String>();
	
	public WalletNames(Iterator<String> walletNames){
		while(walletNames.hasNext()){
			String nextWalletName = walletNames.next();
			walletNamesSet.add(nextWalletName);
		}		
	}
	
	public Set<String> getWalletNamesSetUnmodifiable(){
		return Collections.unmodifiableSet(walletNamesSet);
	}
	
	@Override
	public Iterator<String> iterator() {
		return walletNamesSet.iterator();
	}
	
	public long size(){
		return walletNamesSet.size();
	}	
}