package net.stevenuray.walletexplorer.categories.factories;

import java.util.HashSet;
import java.util.Set;

import net.stevenuray.walletexplorer.categories.WalletCategory;

public class CategoryFactory {
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
		
	public static WalletCategory getPools(){
		String name = "Mining Pools";
		Set<String> poolNames = getPoolNames();
		WalletCategory walletCategory = new WalletCategory(name,poolNames);
		return walletCategory;
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
