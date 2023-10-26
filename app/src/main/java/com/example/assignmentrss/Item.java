package com.example.assignmentrss;

/**
 * @author Eduard Iacob
 */

//Item class which declare all the variables that we need for our RSS feed
public class Item {
    public String title;
    public String link;
    public String description;
    public String enclosure;
    public String guid;
    public String pubDate;

    //empty constructor
    public Item() {
    }

    //constructor with parameters
    public Item(String title, String description, String link, String enclosure, String guid, String pubDate) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.enclosure = enclosure;
        this.guid = guid;
        this.pubDate = pubDate;
    }

    //getters and setters for all the variables
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public String getGuid() {
        return guid;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
}
