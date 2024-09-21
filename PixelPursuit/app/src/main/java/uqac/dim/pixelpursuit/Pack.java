package uqac.dim.pixelpursuit;

public class Pack {
    private String mainImageFileName;
    private String[] secondaryImageFileNames;
    private String title;
    private String description;
    private Boolean isCustom;
    private int packId;

    public Pack(String mainImageFileName, String[] secondaryImageFileNames, String title, String description, int packId, boolean isCustom) {
        this.mainImageFileName = mainImageFileName;
        this.secondaryImageFileNames = secondaryImageFileNames;
        this.title = title;
        this.description = description;
        this.packId = packId;
        this.isCustom = isCustom;
    }


    public String getMainImageFileName() {
        return mainImageFileName;
    }

    public void setMainImageFileName(String mainImageFileName) {
        this.mainImageFileName = mainImageFileName;
    }

    public String[] getSecondaryImageFileNames() {
        return secondaryImageFileNames;
    }

    public void setSecondaryImageFileNames(String[] secondaryImageFileNames) {
        this.secondaryImageFileNames = secondaryImageFileNames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainImage() {
        return mainImageFileName;
    }

    public void setMainImage(String mainImageFileName) {
        this.mainImageFileName = mainImageFileName;
    }

    public String[] getSecondaryImages() {
        return secondaryImageFileNames;
    }

    public void setSecondaryImages(String[] secondaryImageFileNames) {
        this.secondaryImageFileNames = secondaryImageFileNames;
    }

    public void setIsCustom(Boolean isCustom){
        this.isCustom = isCustom;
    }
    public Boolean getIsCustom(){
        return this.isCustom;
    }

    public void setPackId(int id) {this.packId = id;}
    public int getPackId(){
        return this.packId;
    }


}
