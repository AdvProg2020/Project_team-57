package view.process;

import java.util.HashMap;
import java.util.Scanner;

public abstract class Processor {
    protected Scanner scanner = new Scanner(System.in);
    protected HashMap<String, Processor> processers = new HashMap<String, Processor>();
    protected String name;
    protected String parentName;
    protected boolean hasParentMenu;

}
