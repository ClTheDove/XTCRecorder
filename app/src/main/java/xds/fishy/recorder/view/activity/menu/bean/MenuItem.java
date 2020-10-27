package xds.fishy.recorder.view.activity.menu.bean;

public class MenuItem {
    private int id;
    private int drawableResource;
    private int stringResource;

    public MenuItem(int id, int drawableResource, int stringResource) {
        this.id = id;
        this.drawableResource = drawableResource;
        this.stringResource = stringResource;
    }

    public int getId() {
        return id;
    }

    public int getDrawableResource() {
        return drawableResource;
    }

    public int getStringResource() {
        return stringResource;
    }
}
