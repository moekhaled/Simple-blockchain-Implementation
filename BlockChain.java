// The BlockChain class should maintain only limited block nodes to satisfy the functionality.
// You should not have all the blocks added to the block chain in memory 
// as it would cause a memory overflow.

import java.util.ArrayList;

import java.util.HashMap;

public class BlockChain {
    public static final int CUT_OFF_AGE = 10;

    /**
     * create an empty blockchain with just a genesis block. Assume {@code genesisBlock} is a valid
     * block
     */
    
    public ArrayList<Block> blockStore; // Blocks being held in memory
    public HashMap<byte[], byte[]> blockChain; // Map of a block's hash to its previous hash
    public TransactionPool currentTXPool;
    public UTXOPool maxUTXOPool;
    
    public BlockChain(Block genesisBlock) {
        // IMPLEMENT THIS
        blockChain = new HashMap<byte[], byte[]>();
        blockStore = new ArrayList<Block>();
        
        // ** TODO ** Set up the genesis block correctly before adding
        blockChain.put(genesisBlock.getHash(), null);
        blockStore.add(genesisBlock);
    }

    /** Get the maximum height block */
    public Block getMaxHeightBlock() {
        // IMPLEMENT THIS
    	return blockStore.get(blockStore.size()-1);
    }

    /** Get the UTXOPool for mining a new block on top of max height block */
    public UTXOPool getMaxHeightUTXOPool() {
        // IMPLEMENT THIS
    	return maxUTXOPool;

    }

    /** Get the transaction pool to mine a new block */
    public TransactionPool getTransactionPool() {
        // IMPLEMENT THIS
    	return currentTXPool;

    }

    /**
     * Add {@code block} to the blockchain if it is valid. For validity, all transactions should be
     * valid and block should be at {@code height > (maxHeight - CUT_OFF_AGE)}, where maxHeight is 
     * the current height of the blockchain.
	 * <p>
	 * Assume the Genesis block is at height 1.
     * For example, you can try creating a new block over the genesis block (i.e. create a block at 
	 * height 2) if the current blockchain height is less than or equal to CUT_OFF_AGE + 1. As soon as
	 * the current blockchain height exceeds CUT_OFF_AGE + 1, you cannot create a new block at height 2.
     * 
     * @return true if block is successfully added
     */
    public boolean addBlock(Block block) {
        // IMPLEMENT THIS
    	TxHandler txhandler = new TxHandler(null);//double check this again
    	for(Transaction tx : block.getTransactions())
    	{
    		if(!txhandler.isValidTx(tx))
    			return false;
    	}
    	
    	int height =1;
    	
    	for(Block b : blockStore)
    	{
    		if(b.getHash() == block.getPrevBlockHash())
    		{
    			if(height==blockStore.size())
    			{
 				   blockStore.add(block);
 				  blockChain.put(block.getHash(), block.getPrevBlockHash());
                   if(blockStore.size() == 11)
                	   blockStore.remove(0);
                   return true;
    			}
    				
    				
    				
    		}
    	}
    	
    	
    	
    	return false;
    }

    /** Add a transaction to the transaction pool */
    public void addTransaction(Transaction tx) {
        // IMPLEMENT THIS
    	currentTXPool.addTransaction(tx);
    }
}