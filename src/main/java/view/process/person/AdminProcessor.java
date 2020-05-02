package view.process.person;


import controller.account.Admin;

public class AdminProcessor extends AccountProcessor {
    private static Admin adminControl = Admin.getController();
    private static AdminProcessor adminProcessor = null;

    private AdminProcessor(){
        super();

    }

    public static AccountProcessor getInstance(){
        if(adminProcessor == null)
            adminProcessor = new AdminProcessor();

        return adminProcessor;
    }
}
