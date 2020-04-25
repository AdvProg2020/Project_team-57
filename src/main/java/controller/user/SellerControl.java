package controller.user;

public class SellerControl extends UserControl {
    private static SellerControl sellerControl;

    public static SellerControl getInstance(){
        if(sellerControl == null)
            return new SellerControl();

        return SellerControl.sellerControl;
    }
}
