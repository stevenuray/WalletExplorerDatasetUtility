package net.stevenuray.walletexplorer.persistence;

import java.util.Iterator;

/**Decorates an Iterator to indicate it returns objects oldest to newest.  
 * @author Steven Uray 2015-12-17
 */
public interface AscendingTimeIterator<T> extends Iterator<T> {

}
