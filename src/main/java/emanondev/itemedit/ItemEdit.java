package emanondev.itemedit;

import emanondev.itemedit.storage.ServerStorage;

public class ItemEdit {
    private static ItemEdit instance = new ItemEdit();
    
    public static ItemEdit get() {
        return instance;
    }
    
    public ServerStorage getServerStorage() {
        return new ServerStorage();
    }
}
