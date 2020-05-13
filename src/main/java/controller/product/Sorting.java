package controller.product;

import controller.Control;
import model.existence.Product;

import java.util.Comparator;

public class Sorting extends Control {
    public static class ViewSortAscending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getSeen() > o2.getSeen())
                return -1;
            else if (o2.getSeen() > o1.getSeen())
                return 1;
            else
                return 0;
        }
    }

    public static class ViewSortDescending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getSeen() > o2.getSeen())
                return 1;
            else if (o2.getSeen() > o1.getSeen())
                return -1;
            else
                return 0;
        }
    }
}
