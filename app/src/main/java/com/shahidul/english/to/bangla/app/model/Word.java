package com.shahidul.english.to.bangla.app.model;

/**
 * @author Shahidul Islam
 * @since 6/21/2015.
 */
public class Word {
    private int id;
    private String from;
    private String to;
    boolean isFavorite;
    public Word(String from, String to, boolean isFavorite) {
        this.from = from;
        this.to = to;
        this.isFavorite = isFavorite;

    }
    public Word(int id, String from, String to, boolean isFavorite) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.isFavorite = isFavorite;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
