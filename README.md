# WalletExplorerDatasetUtility
**Uses data from WalletExplorer.com's API to analyze activity in the bitcoin economy.** 

WalletExplorer uses machine learning to associate transactions in the blockchain with various bitcoin services. This is useful for analysis of the bitcoin economy. The dataset size of WalletExplorer is currently above 20 Gigabytes and will grow as time progresses. Downloading this dataset to local storage and providing tools to bring it out of storage and into a Java program is a primary function of this program. Analysis of this dataset is another primary function. 

#Example Products: 

The activity of a bitcoin service. 

![Individual Service](http://i.imgur.com/Y045Wjw.png)

Multiple bitcoin services grouped into a category.

![Category Graph](http://i.imgur.com/IXWtg2c.png)

# Features: 
* Creates JavaFX graphs to show transaction volume in USD by user defined categories or by service name. 
* Downloads entire dataset from WalletExplorerAPI with detailed progress and error reporting. 
* Converts transaction amounts in bitcoin to USD values.
* Aggregates transaction amounts by Wallet and Category on a per month basis. 

# Getting Started: 
* *The default MongoDB instance for WalletExplorerUtility is the default local MongoDB instance.* Ensure the MongoDB instance used by the program is running. 
* Run TransactionDownloader until all wallets have been downloaded. Note this may take over a day. 
* Run TransactionConverter until all wallets have been converted. This has taken around 5 hours on average. 
* Run TransactionAggregator. This has taken around 2 hours on average. 
* Use CategoryReviewer or WalletReviewer to view some graphs of the dataset. Classes and interfaces exist for getting data out of MongoDB in a structured way. 

# Project Goals: 
* An abstract persistence layer, so users can use whatever they want. 
* Ease of dataset use, users should be provided with a strong set of tools that make getting the data in and out of storage as easy as possible, so they can focus on the data. 

