package formula;

public enum AtomConstant {

    TRUE ("true"),
    FALSE ("false");

    private final String image;

    AtomConstant(String img) {
        this.image = img;
    }

    /** @return Returns the image */
    public String getImage() { return this.image; }

}
