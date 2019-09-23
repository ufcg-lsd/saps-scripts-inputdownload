package model;

import java.io.Serializable;

import utils.PropertiesConstants;

@SuppressWarnings("serial")
public class ImageTask implements Serializable {

    private String name;
    private String downloadLink;
    private String dataSet;
    private String region;
    private String date;
    
	public ImageTask(String name, String dataSet, String region, String date) {
		this.name = name;
		this.dataSet = dataSet;
		this.region = region;
		this.date = date;
	}

    public String getName() {
        return name;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String toString() {
        return "[" + dataSet + region + date + name + ", " + downloadLink + "]";
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String formatedToString() {

        return  "[ DataSet = " + dataSet + " ]" +
                "[ Region = " + region + " ]" +
                "[ Date = " + date + " ]" +
                "[ ImageName = " + name + " ]\n"
                + "[ DownloadLink = " + downloadLink + " ]\n" ;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageTask) {
            ImageTask other = (ImageTask) o;
            return  getDataSet().equals(other.getDataSet()) &&
                    getRegion().equals(other.getRegion()) &&
                    getDate().equals(other.getDate()) &&
                    getName().equals(other.getName()) &&
                    getDownloadLink().equals(other.getDownloadLink());
        }
        return false;
    }}
