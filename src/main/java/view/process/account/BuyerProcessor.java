package view.process.account;

import controller.user.BuyerControl;
import view.FunctioningOption;

import java.util.HashMap;

public class BuyerProcessor extends UserProcessor {
    private static BuyerControl buyerControl;
    private static BuyerProcessor buyerProcessor;

    private BuyerProcessor() {
        this.functionHashMap = new HashMap<String, FunctioningOption>();

        //TODO
    }

    public static BuyerProcessor getInstance(){
        if(buyerProcessor == null)
            buyerProcessor = new BuyerProcessor();

        return BuyerProcessor.buyerProcessor;
    }
}
