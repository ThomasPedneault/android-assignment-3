package ca.qc.johnabbott.cs616.notes.model;

import ca.qc.johnabbott.cs616.notes.R;

/**
 * Enumeration of note categories, represented as colors.
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public enum Category {

    RED(1), ORANGE(2), YELLOW(3), GREEN(4), LIGHT_BLUE(5), DARK_BLUE(6), PURPLE(7), BROWN(8);

    private int colorId;

    // create a category with a specific color ID.
    Category(int colorId) {
        this.colorId = colorId;
    }

    /**
     * Get the category's color ID.
     * @return
     */
    public int getInternalColorId() {
        return colorId;
    }

    public int getAndroidColorId() {
        switch (this) {
            case RED:
                return R.color.base08;
            case ORANGE:
                return R.color.base09;
            case YELLOW:
                return R.color.base0A;
            case GREEN:
                return R.color.base0B;
            case LIGHT_BLUE:
                return R.color.base0C;
            case DARK_BLUE:
                return R.color.base0D;
            case PURPLE:
                return R.color.base0E;
            case BROWN:
                return R.color.base0F;
            default:
                return R.color.base00;
        }
    }

}
