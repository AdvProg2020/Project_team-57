package view.process.account;

import controller.user.SellerControl;
import view.FunctioningOption;

import java.util.HashMap;

public class SellerProcessor extends UserProcessor {
    private static SellerControl sellerControl;
    private static SellerProcessor sellerProcessor;

    private SellerProcessor() {
        this.functionHashMap = new HashMap<String, FunctioningOption>();

        //TODO
    }

    public static SellerProcessor getInstance(){
        if(sellerProcessor == null)
            sellerProcessor = new SellerProcessor();

        return SellerProcessor.sellerProcessor;
    }
}
