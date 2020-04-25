package view.process;

import java.util.HashMap;
import java.util.Scanner;

public abstract class MainProcesser {
    protected Scanner scanner = new Scanner(System.in);
    protected HashMap<String, MainProcesser> processers = new HashMap<>();
    protected String name;
    protected String parentName;
    protected boolean hasParentMenu;

}
