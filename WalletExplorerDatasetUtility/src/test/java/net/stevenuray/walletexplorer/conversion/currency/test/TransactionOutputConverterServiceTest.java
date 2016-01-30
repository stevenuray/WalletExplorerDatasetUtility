package net.stevenuray.walletexplorer.conversion.currency.test;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import net.stevenuray.walletexplorer.conversion.currency.HistoricalBTCToUSDConverter;
import net.stevenuray.walletexplorer.conversion.currency.TransactionOutputConverterService;
import net.stevenuray.walletexplorer.testobjects.TestTransactionOutputs;
import net.stevenuray.walletexplorer.wallettransactions.dto.ConvertedWalletTransactionOutput;
import net.stevenuray.walletexplorer.wallettransactions.dto.TransactionOutput;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionOutputConverterServiceTest {	
	private TransactionOutputConverterService service;
	
	@Test
	public void convertsWithCorrectUsdVolume() {
		//Setup
		double usdValue = 10;
		DateTime transactionTime = new DateTime();
		HistoricalBTCToUSDConverter mockConverter = mock(HistoricalBTCToUSDConverter.class);
		when(mockConverter.convertBTCVolumeToUSDVolume(anyDouble(), (DateTime) anyObject())).thenReturn(usdValue);
		setupServiceWithConverter(mockConverter);
		TransactionOutput transactionOutput = TestTransactionOutputs.getOutput();		
		
		//Exercise
		ConvertedWalletTransactionOutput convertedOutput = service.convert(transactionOutput, transactionTime);
		
		//Verify
		int scale = 10;
		RoundingMode roundingMode = RoundingMode.HALF_EVEN;
		BigDecimal expectedUsdValue = new BigDecimal(usdValue).setScale(scale,roundingMode);
		BigDecimal returnedUsdValue = new BigDecimal(convertedOutput.getUsdVolume()).setScale(scale, roundingMode);
		assertEquals(expectedUsdValue,returnedUsdValue);
	}

	@Before
	public void setUp() throws Exception {
		HistoricalBTCToUSDConverter mockConverter = mock(HistoricalBTCToUSDConverter.class);
		setupServiceWithConverter(mockConverter);
	}
	
	private void setupServiceWithConverter(HistoricalBTCToUSDConverter converter){
		service = new TransactionOutputConverterService(converter);
	}
}