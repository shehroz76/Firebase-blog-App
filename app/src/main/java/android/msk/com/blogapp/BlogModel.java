package android.msk.com.blogapp;

/**
 * Created by DELL on 9/29/2016.
 */

public class BlogModel {

    private String title;
    private String Desc;
    private String images;

    public BlogModel() {
    }

    public BlogModel(String image, String desc, String title) {
        this.images = image;
        this.Desc = desc;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        this.Desc = desc;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
