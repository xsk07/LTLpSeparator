package formula;

public enum AtomConstant {

    TRUE ("true"),
    FALSE ("false");

    private final String image;

    AtomConstant(String img) { image = img; }

    /** @return Returns the image of the atom constant */
    public String getImage() { return image; }

}
