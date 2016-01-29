package net.stevenuray.walletexplorer.categories.factories;

import java.util.HashSet;
import java.util.Set;

import net.stevenuray.walletexplorer.categories.WalletCategory;

public class ExchangeCategoryFactory {
	public static WalletCategory getBitfinex(){
		String name = "Bitfinex";
		Set<String> bitfinexNames = getBitfinexNames();
		WalletCategory walletCategory = new WalletCategory(name,bitfinexNames);
		return walletCategory;
	}
	
	public static WalletCategory getBitstamp(){
		String name = "Bitstamp";
		Set<String> bitstampNames = getBitstampNames();
		WalletCategory walletCategory = new WalletCategory(name,bitstampNames);
		return walletCategory;
	}

	public static WalletCategory getBTCChina(){
		String name = "BTCChina";
		Set<String> btcChinaNames = getBTCChinaNames();
		WalletCategory walletCategory = new WalletCategory(name,btcChinaNames);
		return walletCategory;
	}
	
	public static WalletCategory getBTCE(){
		String name = "BTC-e";
		Set<String> btceNames = getBTCENames();
		WalletCategory walletCategory = new WalletCategory(name,btceNames);
		return walletCategory;
	}
	
	public static WalletCategory getExchanges(){
		String name = "Exchanges";
		Set<String> exchangeNames = getExchangeNames();
		WalletCategory walletCategory = new WalletCategory(name,exchangeNames);
		return walletCategory;
	}
	
	public static WalletCategory getHuobi(){
		String name = "Huobi";
		Set<String> huobiNames = getHuobiNames();
		WalletCategory walletCategory = new WalletCategory(name,huobiNames);
		return walletCategory;
	}

	public static WalletCategory getOkCoin(){
		String name = "OkCoin";
		Set<String> okCoinNames = getOkCoinNames();
		WalletCategory walletCategory = new WalletCategory(name,okCoinNames);
		return walletCategory;
	}
	
	private static Set<String> getBitfinexNames() {
		Set<String> names = new HashSet<String>();
		names.add("Bitfinex.com");
		names.add("Bitfinex.com-old");
		names.add("Bitfinex.com-old2");
		return names; 
	}
	
	private static Set<String> getBitstampNames() {
		Set<String> names = new HashSet<String>();
		names.add("Bitstamp.net");
		names.add("Bitstamp.net-old");
		return names;
	}

	private static Set<String> getBTCChinaNames() {
		Set<String> names = new HashSet<String>();
		names.add("BTCC.com");
		names.add("BTCC.com-2");
		return names;
	}

	private static Set<String> getBTCENames() {
		Set<String> names = new HashSet<String>();
		names.add("BTC-e.com");
		names.add("BTC-e.com-output");
		names.add("BTC-e.com-old");
		return names;
	}
	
	private static Set<String> getExchangeNames() {
		Set<String> names = new HashSet<String>();
		names.add("BTC-e.com");
		names.add("BTC-e.com-output");
		names.add("BTC-e.com-old");
		names.add("Cryptsy.com");
		names.add("Cryptsy.com-old");
		names.add("LocalBitcoins.com");
		names.add("Cex.io");
		names.add("Bitstamp.net");
		names.add("Bitstamp.net-old");
		names.add("Bittrex.com");
		names.add("Poloniex.com");
		names.add("Bitcoin.de");
		names.add("Bitcoin.de-old");
		names.add("Huobi.com");
		names.add("Huobi.com-2");
		names.add("Kraken.com");
		names.add("Bitfinex.com");
		names.add("Bitfinex.com-old");
		names.add("Bitfinex.com-old2");
		names.add("Bter.com");
		names.add("Bter.com-old");
		names.add("Bter.com-old2");
		names.add("Bter.com-old3");
		names.add("Bter.com-output");
		names.add("Bter.com-cold");
		names.add("OKCoin.com");
		names.add("OKCoin.com-2");
		names.add("BTCC.com");
		names.add("BTCC.com-2");
		names.add("Hashnest.com");
		names.add("BitX.co");
		names.add("C-Cex.com");
		names.add("C-Cex.com-old");
		names.add("Cavirtex.com");
		names.add("AnxPro.com");
		names.add("MercadoBitcoin.com.br");
		names.add("BitBargain.co.uk");
		names.add("Vircurex.com");
		names.add("VirWoX.com");
		names.add("Igot.com");
		names.add("Bleutrade.com");
		names.add("BitVC.com");
		names.add("Justcoin.com");
		names.add("Exmo.com");
		names.add("CampBX.com");
		names.add("CampBX.com-old");
		names.add("CoinTrader.net");
		names.add("Btc38.com");
		names.add("BX.in.th");
		names.add("AllCoin.com");
		names.add("Korbit.co.kr");
		names.add("Matbea.com");
		names.add("TheRockTrading.com");
		names.add("TheRockTrading.com-old");
		names.add("796.com");
		names.add("YoBit.net");
		names.add("BtcTrade.com");
		names.add("FYBSG.com");
		names.add("Coins-e.com");
		names.add("MaiCoin.com");
		names.add("FoxBit.com.br");
		names.add("FoxBit.com.br-2");
		names.add("FoxBit.com.br-cold");
		names.add("FoxBit.com.br-cold-old");
		names.add("Bitcurex.com");
		names.add("HitBtc.com");
		names.add("HitBtc.com-old");
		names.add("Coin.mx");
		names.add("Exchanging.ir");
		names.add("BTradeAustralia.com");
		names.add("BTradeAustrailia.com-incoming");
		names.add("LiteBit.eu");
		names.add("SpectroCoin.com");
		names.add("BitYes.com");
		names.add("ChBtc.com");
		names.add("OrderBook.net");
		names.add("Coinomat.com");
		names.add("HappyCoins.com");
		names.add("CoinSpot.com.au");
		names.add("Bit-x.com");
		names.add("Masterxchange.com");
		names.add("QuadrigaCX.com");
		names.add("Banx.io");
		names.add("Banx.io-old");
		names.add("Banx.io-old2");
		names.add("CoinMotion.com");
		names.add("BitBay.net");
		names.add("Comkort.com");
		names.add("Indacoin.com");
		names.add("SimpleCoin.cz");
		names.add("SimpleCoin.cz-old");
		names.add("CoinCafe.com");
		names.add("MeXBT.com");
		names.add("CoinArch.com");
		names.add("CoinChimp.com");
		names.add("CoinMkt.com");
		names.add("BitKonan.com");
		names.add("CleverCoin.com");
		names.add("Vaultoro.com");
		names.add("BitcoinVietnam.com.vn");
		names.add("Coinmate.io");
		names.add("LakeBTC.com");
		names.add("Biso.com");
		names.add("Dgex.com");
		names.add("Dgex.com-old");
		names.add("Exchange-Credit.ru");
		names.add("Cryptonit.net");
		names.add("Cryptonit.net-old");
		names.add("Ccedk.com");
		names.add("UseCryptos.com");
		names.add("EmpoEX.com");
		names.add("Coinbroker.io");
		names.add("Coinimal.com");
		names.add("BtcMarkets.net");
		names.add("1Coin.com");
		names.add("Europex.eu");
		names.add("UrduBit.com-cold");
		names.add("BlockTrades.us");
		names.add("Zyado.com");
		return names;
	}

	private static Set<String> getHuobiNames() {
		Set<String> names = new HashSet<String>();
		names.add("Huobi.com");
		names.add("Huobi.com-2");
		return names; 
	}
	
	private static Set<String> getOkCoinNames() {
		Set<String> names = new HashSet<String>();
		names.add("OKCoin.com");
		names.add("OKCoin.com-2");
		return names; 
	}
}
