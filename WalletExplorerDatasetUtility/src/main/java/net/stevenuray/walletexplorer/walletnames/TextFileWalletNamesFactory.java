package net.stevenuray.walletexplorer.walletnames;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import net.stevenuray.walletexplorer.downloader.WalletExplorerAPIConfigSingleton;

public class TextFileWalletNamesFactory implements WalletNamesFactory {

	@Override
	public WalletNames getWalletNames() {
		try{
			Iterator<String> walletNamesIterator = getWalletNamesFromFile();
			WalletNames walletNames = new WalletNames(walletNamesIterator);
			return walletNames;
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	private static Iterator<String> getWalletNamesFromFile() throws Exception {	
		Charset encoding = WalletExplorerAPIConfigSingleton.ENCODING;
		File file = new File("resources/wallets.txt");
		Path path = file.toPath();
		Iterator<String> wallets = Files.readAllLines(path, encoding).iterator();
		return wallets;
	}

}
