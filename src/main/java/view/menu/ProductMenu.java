package view.menu;

import model.existence.Product;
import view.PrintOptionSpecs;
import view.process.Processor;

public class ProductMenu extends ListicOptionMenu implements PrintOptionSpecs {
    private Product product;

    public Menu execute(){
        processor = Processor.findProcessorWithName(processorName);
        Menu nextMenu = this;
        boolean flag = true;
        int input = 0;

        while(flag){
            try {
                input = Integer.parseInt(scanner.nextLine().trim());

                if (input > options.size() || input < 0)
                    throw new InputIsBiggerThanExistingNumbers("Invalid Number!!! \nWhat are you doing, man?!");
                else
                    flag = false;

            } catch (NumberFormatException e) {
                System.out.println("Please Enter An Integer");
            } catch (NullPointerException e) {
                System.out.println("Please Enter An Integer");
            } catch (InputIsBiggerThanExistingNumbers e) {
                System.out.println(e.getMessage());
            }
        }

        if(input == 0) {
            nextMenu = parentMenu;
        } else if(processor.isThereFunctionWithName(options.get(input - 1))) {
            nextMenu = processor.executeTheFunctionWithName(options.get(input - 1), this, product);
        }

        return nextMenu;
    }

    public void printOptionSpecs()
    {
        printSpecificProductSpecs(product);
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
