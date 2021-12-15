package formula;

public enum AtomConstant {

    TRUE ("true"),
    FALSE ("false");

    private final String image;
    private AtomConstant complement;

    static {
        TRUE.complement = FALSE;
        FALSE.complement = TRUE;
    }

    AtomConstant(String img) { image = img; }

    /** @return Returns the image of the atom constant */
    public String getImage() { return image; }

    public AtomConstant getComplement() {return complement; }

}
