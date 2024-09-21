package uqac.dim.pixelpursuit;

public class Developer {
    private int photoId;
    private String firstName;
    private String lastName;
    private String description;

    public Developer(int photoId, String firstName, String lastName, String description) {
        this.photoId = photoId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {this.photoId = photoId;}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
