package net.stevenuray.walletexplorer.categories;

import java.util.HashSet;
import java.util.Set;

public class ManualCategories {
	public static WalletCategory getDarknetMarkets(){
		String name = "Darknet Markets";
		Set<String> walletNames = getDarknetMarketNames();
		WalletCategory walletCategory = new WalletCategory(name,walletNames);
		return walletCategory;
	}
	
	public static WalletCategory getGamblingServices(){
		String name = "Gambling Services";
		Set<String> walletNames = getGamblingServiceNames();
		WalletCategory walletCategory = new WalletCategory(name,walletNames);
		return walletCategory;
	}
	
	public static WalletCategory getExchanges(){
		String name = "Exchanges";
		Set<String> exchangeNames = getExchangeNames();
		WalletCategory walletCategory = new WalletCategory(name,exchangeNames);
		return walletCategory;
	}
	
	public static WalletCategory getPools(){
		String name = "Mining Pools";
		Set<String> poolNames = getPoolNames();
		WalletCategory walletCategory = new WalletCategory(name,poolNames);
		return walletCategory;
	}
	
	

	public static WalletCategory getTestCategory(){
		String name = "Test Category";
		Set<String> testNames = getTestNames();
		WalletCategory walletCategory = new WalletCategory(name,testNames);
		return walletCategory;
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
	
	private static Set<String> getTestNames(){
		Set<String> names = new HashSet<String>();
		names.add("BTCJam.com");
		names.add("HolyTransaction.com");
		return names;
	}

	private static Set<String> getDarknetMarketNames() {
		Set<String> names = new HashSet<String>();
		names.add("SilkRoadMarketplace");
		names.add("SilkRoad2Market");
		names.add("EvolutionMarket");
		names.add("PandoraOpenMarket");
		names.add("PandoraOpenMarket-old");
		names.add("SheepMarketplace");
		names.add("BlackBankMarket");
		names.add("CannabisRoadMarket");
		names.add("BraveBunnyMarket");
		names.add("AgoraMarket");
		names.add("NucleusMarket");
		names.add("AbraxasMarket");
		names.add("MiddleEarthMarketplace");
		names.add("MrNiceGuyMarket");
		names.add("BabylonMarket");		
		names.add("AlphaBayMarket");
		names.add("AlphaBayMarket-old");
		names.add("GermanPlazaMarket");
		names.add("DoctorDMarket");
		names.add("GreenRoadMarket");
		names.add("BlueSkyMarketplace");
		return names;
	}

	private static Set<String> getGamblingServiceNames(){
		Set<String> names = new HashSet<String>();
		names.add("BetcoinDice.tm");
		names.add("SealsWithClubs.eu");
		names.add("BtcDice.com");
		names.add("PinballCoin.com");
		names.add("Just-Dice.com");
		names.add("Just-Dice.com-hot2");
		names.add("Just-Dice.com-cold");
		names.add("Just-Dice.com-cold2");
		names.add("Betcoins.net");
		names.add("Bitcoin-Roulette.com");
		names.add("Cryptorush.in");
		names.add("ActionCrypto.com");
		names.add("BitMillions.com");
		names.add("BetsOfBitco.in");
		names.add("Ice-Dice.com");
		names.add("Dice.Bitco.in");
		names.add("Bitcash.cz");
		names.add("PonziCoin.co");
		names.add("10xBitco.in");
		names.add("EveryDice.com");
		names.add("BitDoubler.org");
		names.add("CasinoBum.com");
		names.add("SatoshiDice.com");
		names.add("SatoshiDice.com-original");
		names.add("LuckyB.it");
		names.add("BitZillions.com");
		names.add("999Dice.com");
		names.add("PrimeDice.com");
		names.add("PrimeDice.com-old");
		names.add("PrimeDice.com-old2");
		names.add("PrimeDice.com-old3");
		names.add("NitrogenSports.eu");
		names.add("BitZino.com");
		names.add("SecondsTrade.com");
		names.add("SatoshiMines.com");
		names.add("CoinGaming.io");
		names.add("BitcoinVideoCasino.com");
		names.add("BitcoinVideoCasino.com-old");
		names.add("Betcoin.ag");
		names.add("Betcoin.ag-old");
		names.add("CloudBet.com");
		names.add("DiceOnCrack.com");
		names.add("SatoshiBet.com");
		names.add("FortuneJack.com");
		names.add("Coinroll.com");
		names.add("Rollin.io");
		names.add("SatoshiRoulette.com");
		names.add("Betcoin.tm");
		names.add("PocketDice.io");
		names.add("BTCOracle.com");
		names.add("Peerbet.org");
		names.add("BitAces.me");
		names.add("BitAces.me-old");
		names.add("SwCPoker.eu");
		names.add("BitStarz.com");
		names.add("AnoniBet.com");
		names.add("Satoshi-Karoshi.com");
		names.add("Chainroll.com");
		names.add("Chainroll.com-old");
		names.add("CoinRoyale.com-old");
		names.add("CoinRoyale.com-old2");
		names.add("777Coin.com");
		names.add("SafeDice.com");
		names.add("SatoshiCircle.com");
		names.add("Coinichiwa.com");
		names.add("Crypto-Games.net");
		names.add("Playt.in");
		names.add("PocketRocketsCasino.eu");
		names.add("Coin-Sweeper.com");
		names.add("BetChain.com-old");
		names.add("DaDice.com");
		names.add("JetWin.com");
		names.add("AdmiralCoin.com");
		names.add("Birwo.com-old");
		names.add("CryptoBounty.com");
		names.add("DiceNow.com");
		names.add("YABTCL.com");
		names.add("SuzukiDice.com");
		names.add("FairProof.com");
		names.add("BitcoinPokerTables.com");
		names.add("K8Poker.net");
		names.add("BetMoose.com");
		names.add("LuckyHash.com");
		names.add("DiceCoin.io");
		names.add("MineField.BitcoinLab.org");
		return names;
	}
	
	private static Set<String> getPoolNames() {
		Set<String> names = new HashSet<String>(); 
		names.add("BTCChinaPool");
		names.add("GHash.io");
		names.add("mining.bitcoin.cz");
		names.add("mining.bitcoin.cz-old");
		names.add("mining.bitcoin.cz-old2");
		names.add("BitMinter.com");
		names.add("AntPool.com");
		names.add("EclipseMC.com");
		names.add("EclipseMC.com-old");
		names.add("EclipseMC.com-old2");
		names.add("KnCMiner.com");
		names.add("Eligius.st");
		names.add("BW.com");
		names.add("Bitfury.org");
		names.add("Polmine.pl");
		names.add("CloudHashing.com");
		names.add("Kano.is");		
		return names;
	}
}
