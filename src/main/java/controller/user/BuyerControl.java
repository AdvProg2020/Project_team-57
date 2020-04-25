package controller.user;

public class BuyerControl extends UserControl {
    private static BuyerControl buyerControl;

    public static BuyerControl getInstance(){
        if(buyerControl == null)
            return new BuyerControl();

        return BuyerControl.buyerControl;
    }

}
