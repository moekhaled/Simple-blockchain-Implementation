import java.util.ArrayList;


public class TxHandler {

    private UTXOPool utxoPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. 
     */
    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    
    public boolean isValidTx(Transaction tx) {
    	ArrayList<Transaction.Input> txin  = tx.getInputs(); 
    	double inputSum = 0;
    	double outputSum=0;
    	int k=0;
    	UTXOPool newPool = new UTXOPool();
    	for(Transaction.Input txIn : txin) {
    		UTXO utxo = new UTXO(txIn.prevTxHash, txIn.outputIndex);
    		if(!utxoPool.contains(utxo)) { // check for original pool 
    			return false;
    		}
    		Transaction.Output txout = utxoPool.getTxOutput(utxo);
    		if(!Crypto.verifySignature(txout.address, tx.getRawDataToSign(k), txIn.signature)) {
    			return false;
    		}
    		
    		
    		if(newPool.contains(utxo)) { // for double spending 
    			return false;
    		}
    		newPool.addUTXO(utxo, txout);
    		
    		inputSum += txout.value;
    		k++;
    	}
    	
    	ArrayList<Transaction.Output> txout  = tx.getOutputs(); 
    	for (Transaction.Output txOut : txout) {
    		if( txOut.value < 0) {
    			return false;
    		}
    		 outputSum += txOut.value;
    	}
    	if(outputSum > inputSum) {
    		return false;
    	}
    	
    	
    	return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
 
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
    	 ArrayList<Transaction> validTrans = new ArrayList<Transaction>();
    	
        for(Transaction tx : possibleTxs ) {
        
        	if(isValidTx(tx)) {
        	
        		validTrans.add(tx);
        		
        		for (int j = 0 ; j < tx.getInputs().size() ; j++) {
        			
        			UTXO utxo = new UTXO(tx.getInputs().get(j).prevTxHash, tx.getInputs().get(j).outputIndex);
        			utxoPool.removeUTXO(utxo);
        			
        		}
        		for(int j = 0 ; j < tx.getOutputs().size(); j++) {
        			UTXO utxo = new UTXO(tx.getHash(),j);
        			utxoPool.addUTXO(utxo, tx.getOutput(j));
        		}
        		
        		
        	}
        }
        
        return (Transaction[]) validTrans.toArray();
    	
    }

}