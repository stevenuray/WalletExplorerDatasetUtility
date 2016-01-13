# WalletExplorerDatasetUtility
**Uses data from WalletExplorer.com's API to analyze activity in the bitcoin economy.** Project is ready for use with MongoDB. 

WalletExplorer uses machine learning to associate transactions in the blockchain with various bitcoin services. This is useful for analysis of the bitcoin economy. The dataset size of WalletExplorer is currently above 20 Gigabytes and will grow as time progresses. Downloading this dataset to local storage and providing tools to bring it out of storage and into a Java program is a primary function of this program. Analysis of this dataset is another primary function. 

# Current Working Features: 
* Downloads entire dataset to a local MongoDB instance. 
* Converts transaction amounts in bitcoin to USD values, and stores them back in MongoDB.
* Aggregates transaction amounts by Wallet and Category on a per month basis. 
* Creates JavaFX graphs to show transaction volume in USD by user defined categories or by service name. 

# Getting Started: 
* *The default MongoDB instance for WalletExplorerUtility is the default local MongoDB instance.* Ensure the MongoDB instance used by the program is running. 
* Run TransactionDownloader until all wallets have been downloaded. Note this may take over a day. 
* Run TransactionConverter until all wallets have been converted. This has taken around 5 hours on average. 
* Run TransactionAggregator. This has taken around 2 hours on average. 
* Use CategoryReviewer or WalletReviewer to view some graphs of the dataset. Classes and interfaces exist for getting data out of MongoDB in a structured way. 

# Project Goals: 
* An abstract persistence layer, so users can use whatever they want. 
* Ease of dataset use, users should be provided with a strong set of tools that make getting the data in and out of storage as easy as possible, so they can focus on the data. 

