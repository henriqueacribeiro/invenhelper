package hrtech.bigmanager.invenhelper.exception;

public class InvalidUserPermission extends IllegalArgumentException {

    /**
     * Exception constructor
     *
     * @param permissionName name of the permission
     */
    public InvalidUserPermission(String permissionName) {
        super("Invalid permission name:" + permissionName);
    }
}
