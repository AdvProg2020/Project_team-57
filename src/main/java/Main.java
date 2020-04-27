import view.Menu;
import view.process.Processor;

public class Main {
    public static void main(String[] args) {
        Processor.initProcessorsHashMap();
        Menu menu = Menu.makeMenu("Main Menu");

        while(menu != null)
            menu = menu.show();
    }
}
