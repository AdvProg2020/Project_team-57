package server.controller.product;

import server.model.existence.Product;

import java.util.Comparator;

public class Sorting {
    public static class ViewSortAscending implements Comparator<Product> {
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

    public static class ViewSortDescending implements Comparator<Product> {
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

    public static class ScoreSortAscending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getAverageScore() > o2.getAverageScore())
                return 1;
            else if (o2.getAverageScore() > o1.getAverageScore())
                return -1;
            else
                return 0;
        }
    }

    public static class ScoreSortDescending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            if (o1.getAverageScore() > o2.getAverageScore())
                return -1;
            else if (o2.getAverageScore() > o1.getAverageScore())
                return 1;
            else
                return 0;
        }
    }

    public static class TimeSortAscending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            return o1.getApprovalDate().compareTo(o2.getApprovalDate());
        }
    }

    public static class TimeSortDescending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            return -o1.getApprovalDate().compareTo(o2.getApprovalDate());
        }
    }

    public static class NameSortAscending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            return  o1.getName().compareTo(o2.getName());
        }
    }

    public static class NameSortDescending implements Comparator<Product> {
        @Override
        public int compare(Product o1, Product o2) {
            return (-1 * o1.getName().compareTo(o2.getName()));
        }
    }
}
