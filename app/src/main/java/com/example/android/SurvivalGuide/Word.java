package com.example.android.SurvivalGuide;
/**
 * {@link Word} represents a vocabulary word that the user wants to learn.
 * It contains a default translation and a Miwok translation for that word.
 */
public class Word {
    /** Miwok translation for the word */
    private String mEventText;
    private int mImageResourceId = NO_IMAGE_PROVIDED;

    /** Default translation for the word */
    private String mDefaultTranslation;
    /** Miwok translation for the word */
    private String mMiwokTranslation;
    private int mAudioResourceId ;
    /** Constant value that represents no image was provided for this word */
    private static final int NO_IMAGE_PROVIDED = -1;
    /**
     * Create a new Word object.
     *
     * @param eventText is the word in the Miwok language
     *
     */
    public Word(String eventText) {
        mEventText = eventText;
    }
    /**
     * Create a new Word object.
     *
     * @param eventText is the word in the Miwok language
     *
     * @param imageResourceId is the drawable resource ID for the image associated with the word
     */
    public Word(String eventText, int imageResourceId) {
        mEventText = eventText;
        mImageResourceId = imageResourceId;
    }

    public Word(String defaultTranslation, String miwokTranslation) {
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
    }

    public Word(String defaultTranslation, String miwokTranslation, int imageResourceId, int audioResourceId) {
        mDefaultTranslation = defaultTranslation;
        mMiwokTranslation = miwokTranslation;
        mImageResourceId = imageResourceId;
        mAudioResourceId = audioResourceId;
    }

    /**
     * Get the Miwok translation of the word.
     */
    public String getEventText() {
        return mEventText;
    }
    /**
     * Get the Miwok translation of the word.
     */
    public String getLocationText() {
        return mEventText;
    }
    /**
     * Return the image resource ID of the word.
     */
    public int getImageResourceId() {return mImageResourceId;}
    /**
     * Returns whether or not there is an image for this word.
     */
    public boolean hasImage() {
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }

}